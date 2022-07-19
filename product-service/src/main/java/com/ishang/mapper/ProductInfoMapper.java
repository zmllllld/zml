package com.ishang.mapper;

import com.ishang.entity.ProductInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author zml
 * @since 2022-04-09
 */
@Repository
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

    public BigDecimal findPriceById(Integer id);
    public Integer findsubStockById(Integer id);
    public int updateStock(Integer id,Integer stock);
    public int recoverStock(Integer id,Integer quantity);

}
