package cn.fourse.orderservice.service;

import cn.fourse.orderservice.entity.MqConsumeRecord;
import cn.fourse.orderservice.mapper.MqConsumeRecordMapper;
import cn.fourse.orderservice.mq.PaySuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayMessageService {

    private final MqConsumeRecordMapper consumeRecordMapper;

    @Transactional
    public void handlePaySuccess(
            PaySuccessMessage message) {

        MqConsumeRecord record =
                new MqConsumeRecord();

        record.setBusinessKey(message.getOrderNo());
        record.setTopic("pay-success-topic");
        record.setCreateTime(LocalDateTime.now());

        try {

            consumeRecordMapper.insert(record);

        } catch (DuplicateKeyException e) {

            System.out.println(
                    "重复消息，忽略："
                            + message.getOrderNo()
            );

            return;
        }

        // 真正业务
        System.out.println(
                "处理支付成功业务："
                        + message.getOrderNo()
        );

        // 故意制造异常
//        throw new RuntimeException("模拟消费失败");

        // 后面可能：
        // 增加积分
        // 记录通知
        // 创建物流任务
    }
}