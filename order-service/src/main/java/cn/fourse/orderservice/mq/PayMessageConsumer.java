package cn.fourse.orderservice.mq;


import cn.fourse.orderservice.entity.MqConsumeRecord;
import cn.fourse.orderservice.mapper.MqConsumeRecordMapper;
import cn.fourse.orderservice.service.PayMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PayMessageConsumer {

    private final PayMessageService payMessageService;

    @Bean
    public Consumer<PaySuccessMessage> paySuccess() {
        return message -> {

            payMessageService.handlePaySuccess(message);

        };
    }
}