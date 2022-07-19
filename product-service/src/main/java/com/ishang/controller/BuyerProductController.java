package com.ishang.controller;


import com.ishang.entity.ProductInfo;
import com.ishang.service.ProductCategoryService;
import com.ishang.service.ProductInfoService;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 类目表 前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-09
 */
@RestController
    @RequestMapping("/buyer/product")
public class BuyerProductController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductInfoService productInfoService;


    @ApiOperation("商品列表")
    @GetMapping("/list")
    public ResultVO list(){

        return ResultVOUtil.success(this.productCategoryService.buyerlist());
    }

    @ApiOperation("根据 ID 查询商品价格")
    @GetMapping("/findPriceById/{id}")
    public BigDecimal findPriceById(@PathVariable("id") Integer id){
        return this.productInfoService.findPriceById(id);

    }

    @ApiOperation("通过 ID 查询商品")
    @GetMapping("/findById/{id}")
    public ProductInfo findById(@PathVariable("id") Integer id){

        return this.productInfoService.getById(id);

    }

    @ApiOperation("减库存")
    @PutMapping("/subStockById/{id}/{quantity}")
        public Boolean subStockById(@PathVariable("id") Integer id,@PathVariable("quantity") Integer quantity){
            return this.productInfoService.subStockById(id,quantity);
            }

     @ApiOperation("恢复库存")
     @PutMapping("/recoverStockById/{id}/{quantity}")
     public Boolean recoverStockById(@PathVariable("id") Integer id,@PathVariable("quantity") Integer quantity){
         return this.productInfoService.recoverStockById(id,quantity);
     }


}

