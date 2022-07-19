package com.ishang.service.impl;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ishang.exception.ShopException;
import com.ishang.result.ResponseEnum;
import com.ishang.service.SmsService;
import com.ishang.util.SmsUtil;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean send(String mobile, String code) {
        RequestModel model = new RequestModel();
        model.setGwUrl(SmsUtil.Url);
        model.setAppkey(SmsUtil.Appkey);
        Map queryMap = new HashMap();
        queryMap.put("sign",SmsUtil.Sign); //访问参数
        queryMap.put("mobile",mobile); //访问参数
        queryMap.put("content","您本次的验证码是："+code); //访问参数
        model.setQueryParams(queryMap);
        try {
            WxApiCall call = new WxApiCall();
            call.setModel(model);
            call.request();
            String request = call.request();
            Gson gson = new Gson();
            Map<String,String> map = gson.fromJson(request,
                    new TypeToken<Map<String,String>>(){}.getType());
            System.out.println(map);
//            正确的状态码应该是10000，由于没有付费，所以状态码会提示10010，
//            这里验证如果获取状态码为10010，说明短信服务被调通，则返回true
            if(map.get("code").equals("10010"))return true;
        } catch (JsonSyntaxException e) {
            log.error("短信调用失败！");
            throw new ShopException(ResponseEnum.SMS_SEND_ERROR.getMsg());
        }
        return false;
    }
}
