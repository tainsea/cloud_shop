package cn.fourse.orderservice.controller;

import cn.fourse.orderservice.mq.PayMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
@RequiredArgsConstructor
@RefreshScope
public class MqTestController {

    private final PayMessageProducer payMessageProducer;

    @PostMapping("/pay-success/{orderNo}")
    public String paySuccess(
            @PathVariable String orderNo) {

        payMessageProducer.sendPaySuccess(orderNo);

        return "消息发送成功";
    }
}