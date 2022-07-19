package com.ishang.mapper;

import com.ishang.entity.OrderMaster;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@Repository
public interface OrderMasterMapper extends BaseMapper<OrderMaster> {
    public boolean cancel(Integer buyerId, String orderId);
    public boolean cancel2(String orderId);
    public boolean finish(String orderId);
    public boolean pay(Integer buyerId,String orderId);



}
