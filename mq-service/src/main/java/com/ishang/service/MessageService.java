package com.ishang.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.server.ServerEndpoint;

@Service
@Slf4j
//一读取到mq里面的消息就开始向卖家端前端进行发送
@RocketMQMessageListener(consumerGroup = "message",topic = "myTopic")
public class MessageService  implements RocketMQListener<String> {

    @Autowired
    private WebSocket webSocket;
    @Override
    public void onMessage(String s) {
        log.info("收到消息:{}",s);
        this.webSocket.sendMessage(s);

    }
}
