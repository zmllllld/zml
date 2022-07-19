package com.ishang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ishang.entity.OrderDetail;
import com.ishang.entity.OrderMaster;
import com.ishang.entity.ProductInfo;
import com.ishang.feign.ProductFeign;
import com.ishang.form.BuyerOrderDetailForm;
import com.ishang.form.BuyerOrderForm;
import com.ishang.mapper.OrderDetailMapper;
import com.ishang.mapper.OrderMasterMapper;
import com.ishang.service.OrderMasterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ishang.vo.BuyerOderMasterVO;
import com.ishang.vo.BuyerOrderDetailVO;
import com.ishang.vo.SellerOrderMasterVO;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@Service
public class OrderMasterServiceImpl extends ServiceImpl<OrderMasterMapper, OrderMaster> implements OrderMasterService {

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private OrderMasterMapper orderMasterMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;



    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional
    public String create(BuyerOrderForm buyerOrderForm) {
//        创建的订单 先存主表
//        因为BuyerOrderForm的字段不与entity对应，所以要手动添加
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setBuyerAddress(buyerOrderForm.getAddress());
        orderMaster.setBuyerName(buyerOrderForm.getName());
        orderMaster.setBuyerOpenid(buyerOrderForm.getId());
        orderMaster.setBuyerPhone(buyerOrderForm.getPhone());
        orderMaster.setOrderStatus(0);
        orderMaster.setPayStatus(0);
//        orderamount订单总金额需要数量乘以单价，而单价需要通过id到商品服务中调用
        List<BuyerOrderDetailForm> items = buyerOrderForm.getItems();
        BigDecimal orderamount = new BigDecimal(0);
        for (BuyerOrderDetailForm item : items) {
//            获取商品id
            Integer productID = item.getProductID();
//            根据商品id查询商品单价
            BigDecimal priceById = this.productFeign.findPriceById(productID);
//            获取商品购买数量
            Integer productQuantity = item.getProductQuantity();
//            计算商品总价
            BigDecimal amount = priceById.multiply(new BigDecimal(productQuantity));
//            将计算出的值赋给orderamount
            orderamount = orderamount.add(amount);
        }
        orderMaster.setOrderAmount(orderamount);
//        将订单插入到数据库
        this.orderMasterMapper.insert(orderMaster);

//        再存从表
        for (BuyerOrderDetailForm item : items) {
//            每一个商品对应一条订单详情信息
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderMaster.getOrderId());
            orderDetail.setProductQuantity(item.getProductQuantity());
//            根据商品id获得商品信息
            ProductInfo productInfo = this.productFeign.findById(item.getProductID());
            BeanUtils.copyProperties(productInfo,orderDetail);
            this.orderDetailMapper.insert(orderDetail);
//            更新库存操作
            this.productFeign.subStockById(item.getProductID(), item.getProductQuantity());

        }
        //通知后台管理系统,先将消息存储到mq中
        this.rocketMQTemplate.convertAndSend("myTopic","有新的订单");
        return orderMaster.getOrderId();


    }

    @Override
    @Transactional
//    查询订单详情
    public BuyerOderMasterVO detail(Integer buyerId, String orderId) {
//        先查询出订单信息,两个id锁定一条订单
        BuyerOderMasterVO buyerOderMasterVO = new BuyerOderMasterVO();
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper();
        queryWrapper.eq("buyer_openid",buyerId);
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = this.orderMasterMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(orderMaster,buyerOderMasterVO);
//        一条总订单中有订单商品集合,一个订单对应多个商品

        QueryWrapper<OrderDetail> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = this.orderDetailMapper.selectList(queryWrapper1);
        List<BuyerOrderDetailVO> buyerOrderDetailVOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            BuyerOrderDetailVO buyerOrderDetailVO = new BuyerOrderDetailVO();
            BeanUtils.copyProperties(orderDetail,buyerOrderDetailVO);
            buyerOrderDetailVO.setDatailId(orderDetail.getDetailId());
            buyerOrderDetailVOList.add(buyerOrderDetailVO);
        }
        buyerOderMasterVO.setOrderDetailList(buyerOrderDetailVOList);
        return buyerOderMasterVO;



    }
//    取消订单
//    @Override
//    @Transactional
//    public boolean cancel(Integer buyerId, String orderId) {
////        根据order_id将属于一个订单的商品集合查询出来
//        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("order_id", orderId);
//        List<OrderDetail> orderDetails = this.orderDetailMapper.selectList(queryWrapper);
//        for (OrderDetail orderDetail : orderDetails) {
////            将订单中的商品库存恢复
//            this.productFeign.RecoverStockById(orderDetail.getProductId(), orderDetail.getProductQuantity());
//        }
////        将订单状态修改为取消状态
//        if (buyerId == null) {
//            this.orderMasterMapper.cancel2(orderId);
//        } else {
//            return this.orderMasterMapper.cancel(buyerId, orderId);
//        }
//    }
    @Override
    @Transactional

    public boolean cancel(Integer buyerId, String orderId) {
        //加库存
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetailList = this.orderDetailMapper.selectList(queryWrapper);
        for (OrderDetail orderDetail : orderDetailList) {
            this.productFeign.RecoverStockById(orderDetail.getProductId(), orderDetail.getProductQuantity());
        }
        if(buyerId == null) {
            return this.orderMasterMapper.cancel2(orderId);
        } else {
            return this.orderMasterMapper.cancel(buyerId, orderId);
        }
    }

//    完结订单
    @Override
    public boolean finish(String orderId) {
        this.orderMasterMapper.finish(orderId);
        return true;
    }

//    支付订单
    @Override
    public boolean pay(Integer buyerId, String orderId) {
        this.orderMasterMapper.pay(buyerId,orderId);
        return true;
    }




}
