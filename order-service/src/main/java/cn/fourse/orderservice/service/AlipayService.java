package cn.fourse.orderservice.service;

import cn.fourse.orderservice.config.AlipayProperties;
import cn.fourse.orderservice.entity.Order;
import cn.fourse.orderservice.mapper.OrderMapper;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlipayService {

    private final AlipayClient alipayClient;

    private final AlipayProperties properties;

    private final OrderMapper orderMapper;

    private final OrderPayService orderPayService;

    public String pay(String orderNo) throws Exception {

        // 1. 查询订单
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 判断订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }

        // 3. 创建电脑网站支付请求
        AlipayTradePagePayRequest request =
                new AlipayTradePagePayRequest();

        // 4. 支付宝服务器异步通知地址
        request.setNotifyUrl(
                properties.getNotifyUrl()
        );

        // 5. 用户浏览器支付完成后的跳转地址
        request.setReturnUrl(
                properties.getReturnUrl()
        );

        // 6. 设置支付参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOrderNo());
        bizContent.put("total_amount", order.getTotalAmount());
        bizContent.put("subject", order.getProductName());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toJSONString());

        // 7. 生成支付宝支付页面
        return alipayClient.pageExecute(request).getBody();
    }

    public String handleNotify(
            Map<String, String> params)
            throws Exception {

        // 1. 验签
        boolean signVerified =
                AlipaySignature.rsaCheckV1(
                        params,
                        properties.getAlipayPublicKey(),
                        "UTF-8",
                        "RSA2"
                );

        if (!signVerified) {
            return "failure";
        }

        // 2. 支付状态
        String tradeStatus =
                params.get("trade_status");

        if (!"TRADE_SUCCESS".equals(tradeStatus)
                &&
                !"TRADE_FINISHED".equals(tradeStatus)) {

            return "failure";
        }

        // 3. 获取订单号
        String orderNo =
                params.get("out_trade_no");

        // 4. 查询订单
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );

        if (order == null) {
            return "failure";
        }

        // 5. 校验APP_ID
        String appId =
                params.get("app_id");

        if (!properties.getAppId().equals(appId)) {
            return "failure";
        }

        // 6. 校验金额
        BigDecimal paidAmount =
                new BigDecimal(
                        params.get("total_amount")
                );

        if (order.getTotalAmount()
                .compareTo(paidAmount) != 0) {

            return "failure";
        }

        // 7. 已经处理过
        if (order.getStatus() == 1) {
            return "success";
        }

        // 8. 真正处理支付成功
        orderPayService.markPaid(order);

        return "success";
    }

    public String query(String orderNo) throws Exception {

        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        AlipayTradeQueryRequest request =
                new AlipayTradeQueryRequest();

        request.setBizContent(
                """
                {
                    "out_trade_no":"%s"
                }
                """.formatted(orderNo)
        );

        AlipayTradeQueryResponse response =
                alipayClient.execute(request);

        if (!response.isSuccess()) {

            throw new RuntimeException(
                    "支付宝查单失败：" +
                            response.getSubMsg()
            );
        }

        String tradeStatus =
                response.getTradeStatus();

        // 支付宝已经成功，但本地还是待支付
        if ("TRADE_SUCCESS".equals(tradeStatus)
                && order.getStatus() == 0) {

            orderPayService.markPaid(order);
        }

        return tradeStatus;
    }
}