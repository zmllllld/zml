package com.ishang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ishang.entity.User;
import com.ishang.exception.ShopException;
import com.ishang.form.UserForm;
import com.ishang.result.ResponseEnum;
import com.ishang.service.UserService;
import com.ishang.util.JwtUtil;
import com.ishang.util.MD5Util;
import com.ishang.util.RegexValidateUtil;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import com.ishang.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("注册用户")
    @PostMapping("/register")
    public ResultVO register(@RequestBody UserForm userForm){
//        验证手机号码格式是否正确
        boolean b = RegexValidateUtil.checkMobile(userForm.getMobile());
        if(!b){
            throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        }
//        验证手机号码是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",userForm.getMobile());
        User one = this.userService.getOne(queryWrapper);
        if (one != null){
            throw  new ShopException(ResponseEnum.MOBILE_EXIST.getMsg());
        }
//        验证码校验
//        获取redis中的验证码
        String code = (String) this.redisTemplate.opsForValue().get("uushop-sms-code" + userForm.getMobile());
//        如果输入的验证码和redis中存储的验证码不同，则抛出异常
        if(!code.equals(userForm.getCode())){
            throw new ShopException(ResponseEnum.CODE_ERRO.getMsg());
        }
//        校验后将注册信息存到user表中
        User user = new User();
        user.setMobile(userForm.getMobile());
        user.setPassword(MD5Util.getSaltMD5(userForm.getPassword()));
        this.userService.save(user);
        return ResultVOUtil.success(null);
    }

    @ApiOperation("用户登录")
    @GetMapping("/login")
//    get请求不能接收json数据，因此不能加Requestbody
    public ResultVO login(UserForm userForm){
//        验证手机号格式是否正确
        boolean b = RegexValidateUtil.checkMobile(userForm.getMobile());
        if (!b){
            throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        }
//        验证手机号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",userForm.getMobile());
        User one = this.userService.getOne(queryWrapper);
        if(one == null){
            throw new ShopException(ResponseEnum.MOBILE_IS_NULL.getMsg());
        }
//        验证密码
       if (!MD5Util.getSaltverifyMD5(userForm.getPassword(),one.getPassword())){
           throw  new ShopException(ResponseEnum.PASSWORD_ERRO.getMsg());
        }
//       生成Token
        String token = JwtUtil.createToken(one.getUserId(), one.getMobile());
       UserVO userVO = new UserVO(one.getUserId(),one.getMobile(),one.getPassword(),token);
       return ResultVOUtil.success(userVO);


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

