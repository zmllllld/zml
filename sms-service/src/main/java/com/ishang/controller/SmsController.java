package com.ishang.controller;


import com.ishang.exception.ShopException;
import com.ishang.result.ResponseEnum;
import com.ishang.service.SmsService;
import com.ishang.util.RandomUtil;
import com.ishang.util.RegexValidateUtil;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/send/{mobile}")
    public ResultVO send(@PathVariable("mobile") String mobile){
//        验证手机号是否合格
        boolean b = RegexValidateUtil.checkMobile(mobile);
        if (!b){
            throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        }
// 生成验证码
        String code = RandomUtil.getSixBitRandom();
//        调用service层的jdwx发送验证码
        boolean send = this.smsService.send(mobile,code);
        if(send){
//返回true时，将验证码给redis存一份
            this.redisTemplate.opsForValue().set("uushop-sms-code"+mobile,code);
            return ResultVOUtil.success("短信发送成功");
        }
        return ResultVOUtil.fail("短信发送失败");
    }
}
