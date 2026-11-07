package cn.fourse.orderservice.mq;

import lombok.RequiredArgsConstructor;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PayMessageProducer {

    private final StreamBridge streamBridge;

    public void sendPaySuccess(String orderNo) {

        PaySuccessMessage message =
                new PaySuccessMessage(orderNo);

        streamBridge.send(
                "paySuccess-out-0",
                message
        );

        System.out.println(
                "支付成功消息发送：" + orderNo
        );
    }
}
