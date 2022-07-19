package com.ishang.feign;


import com.ishang.entity.ProductInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;

//调用商品服务
@FeignClient("product-service")
public interface ProductFeign {
    @GetMapping("/buyer/product/findPriceById/{id}")
    public BigDecimal findPriceById(@PathVariable("id") Integer id);

    @GetMapping("/buyer/product/findById/{id}")
    public ProductInfo findById(@PathVariable("id") Integer id);

    @PutMapping("/buyer/product/subStockById/{id}/{quantity}")
    public boolean subStockById(@PathVariable("id") Integer id, @PathVariable("quantity") Integer quantity);


    @PutMapping("/buyer/product/recoverStockById/{id}/{quantity}")
    public boolean RecoverStockById(@PathVariable("id") Integer id, @PathVariable("quantity") Integer quantity);
}