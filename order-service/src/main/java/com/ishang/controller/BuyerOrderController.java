package com.ishang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ishang.entity.OrderMaster;
import com.ishang.form.BuyerOrderForm;
import com.ishang.service.OrderMasterService;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单详情表 前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-13
 */
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {

    @Autowired
    private OrderMasterService orderMasterService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public ResultVO create(@RequestBody BuyerOrderForm buyerOrderForm) {
//        调用service层后的返回值是orderId
        String orderId = this.orderMasterService.create(buyerOrderForm);
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        return ResultVOUtil.success(map);
    }

    @ApiOperation("订单列表")
    @GetMapping("/list/{buyerId}/{page}/{size}")
    public ResultVO list(@PathVariable("buyerId") Integer buyerId,
                         @PathVariable("page") Integer page,
                         @PathVariable("size") Integer size) {
        //        定义分页信息
        Page<OrderMaster> orderMasterPage = new Page<>(page, size);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("buyer_openid", buyerId);
        Page page1 = this.orderMasterService.page(orderMasterPage, queryWrapper);
        List records = page1.getRecords();
        return ResultVOUtil.success(records);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/detail/{buyerId}/{orderId}")
    public ResultVO detail(@PathVariable("buyerId") Integer buyerId, @PathVariable("orderId") String orderId) {
        return ResultVOUtil.success(this.orderMasterService.detail(buyerId, orderId));
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{buyerId}/{orderId}")
    public ResultVO cancel(@PathVariable("buyerId") Integer buyerId, @PathVariable("orderId") String orderId) {

        this.orderMasterService.cancel(buyerId, orderId);
        return ResultVOUtil.success(null);
    }

    @ApiOperation("完结订单")
    @PutMapping("/finish/{orderId}")
    public ResultVO finish (@PathVariable("orderId") String orderId) {
        this.orderMasterService.finish(orderId);
        return ResultVOUtil.success(null);
    }

    @ApiOperation("支付订单")
    @PutMapping("/pay/{buyerId}/{orderId}")
    public ResultVO pay(@PathVariable("buyerId") Integer buyerId,@PathVariable("orderId") String orderId){
        this.orderMasterService.pay(buyerId,orderId);
        return ResultVOUtil.success(null);
    }

}

