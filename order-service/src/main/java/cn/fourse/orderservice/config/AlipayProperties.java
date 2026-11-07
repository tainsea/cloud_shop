package cn.fourse.orderservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private String appId;

    private String privateKey;

    private String alipayPublicKey;

    private String gatewayUrl;

    private String notifyUrl;

    private String returnUrl;
}