package com.ishang.mapper;

import com.ishang.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 类目表 Mapper 接口
 * </p>
 *
 * @author zml
 * @since 2022-04-09
 */
@Repository
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    public String findCategoryNameByType(Integer type);

}
