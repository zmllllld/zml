package com.ishang.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ishang.entity.OrderMaster;
import com.ishang.mapper.OrderDetailMapper;
import com.ishang.service.OrderDetailService;
import com.ishang.service.OrderMasterService;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import com.ishang.vo.SellerOrderMasterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@RestController
@RequestMapping("/seller/order")
public class SellerOrderController {
    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private OrderDetailService orderDetailService;
    @ApiOperation("查询订单")
    @GetMapping("/list/{page}/{size}")
    public ResultVO list(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size){
        Page<OrderMaster> orderMasterPage = new Page<>(page,size);
        Page<OrderMaster> page1 = this.orderMasterService.page(orderMasterPage);
        List<OrderMaster> records = page1.getRecords();
        SellerOrderMasterVO sellerOrderMasterVO = new SellerOrderMasterVO();
        sellerOrderMasterVO.setSize(size);
        sellerOrderMasterVO.setTotal(page1.getTotal());
        sellerOrderMasterVO.setContent(records);
        return ResultVOUtil.success(sellerOrderMasterVO);
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{orderId}")
    public ResultVO cancel(@PathVariable("orderId") String orderId){

        return ResultVOUtil.success(this.orderMasterService.cancel(null,orderId));

    }

    @ApiOperation("完结订单")
    @PutMapping("/finish/{orderId}")
    public ResultVO finish(@PathVariable("orderId") String orderId){
        this.orderMasterService.finish(orderId);
        return ResultVOUtil.success(null);
    }

    @ApiOperation("柱状图")
    @GetMapping("/barSale")
    public ResultVO barSale(){
        return ResultVOUtil.success(this.orderDetailService.barData());
    }

    @ApiOperation("基础折线图")
    @GetMapping("/basicLineSale")
    public ResultVO basicLineSale(){
        return ResultVOUtil.success(this.orderDetailService.basicLineSale());

    }
    @ApiOperation("折线图堆叠")
    @GetMapping("/stackedLineSale")
    public ResultVO stackedLineSale(){
        return ResultVOUtil.success(this.orderDetailService.stackedLineSale());

    }





}

