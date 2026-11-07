package cn.fourse.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mq_consume_record")
public class MqConsumeRecord {

    private Long id;

    private String businessKey;

    private String topic;

    private LocalDateTime createTime;
}