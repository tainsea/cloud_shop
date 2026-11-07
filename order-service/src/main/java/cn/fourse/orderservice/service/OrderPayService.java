package cn.fourse.orderservice.service;

import cn.fourse.orderservice.entity.Order;
import cn.fourse.orderservice.mapper.OrderMapper;
import cn.fourse.orderservice.mq.PayMessageProducer;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderPayService {

    private final OrderMapper orderMapper;

    private final PayMessageProducer payMessageProducer;

    private final AlipayClient alipayClient;

    @Transactional(rollbackFor = Exception.class)
    public void markPaid(Order order) {

        int rows = orderMapper.update(
                null,
                new LambdaUpdateWrapper<Order>()
                        .eq(Order::getId, order.getId())
                        .eq(Order::getStatus, 0)
                        .set(Order::getStatus, 1)
        );

        if (rows == 0) {

            System.out.println(
                    "订单已经被处理："
                            + order.getOrderNo()
            );

            return;
        }

        System.out.println(
                "订单支付成功："
                        + order.getOrderNo()
        );

        payMessageProducer.sendPaySuccess(
                order.getOrderNo()
        );
    }

    public String query(String orderNo) throws Exception {

        // 1. 查询本地订单
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 创建支付宝查单请求
        AlipayTradeQueryRequest request =
                new AlipayTradeQueryRequest();

        // 3. 告诉支付宝查哪个订单
        request.setBizContent(
                """
                {
                    "out_trade_no":"%s"
                }
                """.formatted(orderNo)
        );

        // 4. 请求支付宝
        AlipayTradeQueryResponse response =
                alipayClient.execute(request);

        if (!response.isSuccess()) {

            throw new RuntimeException(
                    "支付宝查单失败：" +
                            response.getSubMsg()
            );
        }

        // 5. 获取支付宝交易状态
        String tradeStatus =
                response.getTradeStatus();

        System.out.println(
                "订单：" + orderNo +
                        "，支付宝状态：" + tradeStatus
        );

        return tradeStatus;
    }
}