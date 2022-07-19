package com.ishang.service.impl;

import com.ishang.entity.OrderDetail;
import com.ishang.mapper.OrderDetailMapper;
import com.ishang.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ishang.util.EChartsColorUtil;
import com.ishang.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;


//每个商品的总销售量
    @Override
    public BarVO barData() {

        List<String> names = new ArrayList<>();
        List<BarResultVO> barResultVOS = this.orderDetailMapper.barData();
        List<BarValueVO> barValueVOS =new ArrayList<>();
        for (BarResultVO barResultVO : barResultVOS) {
            names.add(barResultVO.getName());
            BarValueVO barValueVO = new BarValueVO();
            barValueVO.setValue(barResultVO.getValue());
            barValueVO.setItemStyle(EChartsColorUtil.createItemStyle(barResultVO.getValue()));
            barValueVOS.add(barValueVO);
        }
        BarVO barVO = new BarVO();
        barVO.setNames(names);
        barVO.setValues(barValueVOS);
        return barVO;
    }

    //    每一天的销售量
    @Override
    public BasicLineVO basicLineSale() {
        BasicLineVO basicLineVO = new BasicLineVO();
        List<BasicLineResultVO> basicLineResultVOS = this.orderDetailMapper.basicLineData();
        List<String> name =new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        for (BasicLineResultVO basicLineResultVO : basicLineResultVOS) {
            name.add(basicLineResultVO.getDate());
            value.add(basicLineResultVO.getValue());
        }
        basicLineVO.setNames(name);
        basicLineVO.setValues(value);
        return basicLineVO;
    }

    @Override
    public StackedLineSaleVO stackedLineSale() {
        StackedLineSaleVO stackedLineSaleVO = new StackedLineSaleVO();
        List<String> names = this.orderDetailMapper.names();
        stackedLineSaleVO.setNames(names);
        List<String> dates = this.orderDetailMapper.dates();
        stackedLineSaleVO.setDates(dates);
        List<StackedLineInnerVO> stackedLineInnerVOS =new ArrayList<>();
        for (String name : names) {
            StackedLineInnerVO stackedLineInnerVO = new StackedLineInnerVO();
            stackedLineInnerVO.setName(name);
            List<Integer> integers = this.orderDetailMapper.stackedData(name);
            stackedLineInnerVO.setData(integers);
            stackedLineInnerVOS.add(stackedLineInnerVO);
        }
        stackedLineSaleVO.setDatas(stackedLineInnerVOS);
        return stackedLineSaleVO;

    }


}
