package com.ishang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ishang.entity.Admin;
import com.ishang.exception.ShopException;
import com.ishang.form.AdminForm;
import com.ishang.mapper.AdminMapper;
import com.ishang.result.ResponseEnum;
import com.ishang.service.AdminService;
import com.ishang.util.JwtUtil;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.AdminVO;
import com.ishang.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-17
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @ApiOperation("admin用户登录")
    @GetMapping("/login")
    public ResultVO  login(AdminForm adminForm){

//        检查username是否存在
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username",adminForm.getUsername());
            Admin one = this.adminService.getOne(queryWrapper);
            if(one == null){
                throw  new ShopException(ResponseEnum.USERNAME_ERRO.getMsg());
            }
//            验证密码
        if(!adminForm.getPassword().equals(one.getPassword())){
            throw new ShopException(ResponseEnum.PASSWORD_ERRO.getMsg());
        }
//        生成token
        String token = JwtUtil.createToken(one.getAdminId(), one.getUsername());
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(one,adminVO);
        adminVO.setToken(token);
        return ResultVOUtil.success(adminVO);
    }

    @ApiOperation("Token验证")
    @GetMapping("/checkToken")
    public ResultVO checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean result = JwtUtil.checkToken(token);
        if(!result){
            throw new ShopException(ResponseEnum.TOKEN_ERRO.getMsg());
        }
        return ResultVOUtil.success(null);

    }



}

