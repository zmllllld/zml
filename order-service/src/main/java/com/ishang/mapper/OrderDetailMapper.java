package com.ishang.mapper;

import com.ishang.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ishang.vo.BarResultVO;
import com.ishang.vo.BarValueVO;
import com.ishang.vo.BasicLineResultVO;
import com.ishang.vo.StackedLineInnerVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@Repository
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    public List<String> dates();
    public List<String> names();
    public List<Integer> stackedData(String name);
    public List<BarResultVO> barData();
    public List<BasicLineResultVO> basicLineData();


}
