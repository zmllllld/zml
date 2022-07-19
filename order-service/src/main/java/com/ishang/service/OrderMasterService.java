package com.ishang.service;

import com.ishang.entity.OrderMaster;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ishang.form.BuyerOrderForm;
import com.ishang.vo.BuyerOderMasterVO;
import com.ishang.vo.SellerOrderMasterVO;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
public interface OrderMasterService extends IService<OrderMaster> {
    public String create(BuyerOrderForm buyerOrderForm);
    public BuyerOderMasterVO detail(Integer buyerId,String orderId);
    public boolean cancel(Integer id, String orderId);
    public boolean finish(String orderId);
    public boolean pay(Integer buyerId,String orderId);


}
