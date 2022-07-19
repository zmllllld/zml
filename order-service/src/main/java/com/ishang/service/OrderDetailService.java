package com.ishang.service;

import com.ishang.entity.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ishang.vo.BarVO;
import com.ishang.vo.BasicLineVO;
import com.ishang.vo.StackedLineSaleVO;

/**
 * <p>
 * 订单详情表 服务类
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
public interface OrderDetailService extends IService<OrderDetail> {
    public BarVO barData();
    public BasicLineVO basicLineSale();
    public StackedLineSaleVO stackedLineSale();
}
