package cn.fourse.orderservice.service;

import cn.fourse.orderservice.client.ProductClient;
import cn.fourse.orderservice.client.StockClient;
import cn.fourse.orderservice.dto.CreateOrderDTO;
import cn.fourse.orderservice.dto.ProductDTO;
import cn.fourse.orderservice.entity.Order;
import cn.fourse.orderservice.exception.BusinessException;
import cn.fourse.orderservice.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;

    private final ProductClient productClient;

    private final StockClient stockClient;


    @GlobalTransactional(
            name = "create-order",
            rollbackFor = Exception.class
    )
    public Order createOrder(CreateOrderDTO dto){
        //1.查询商品
        ProductDTO product = productClient.getById(dto.getProductId());
        //2.判断商品
        if(product == null){
            throw new BusinessException("商品不存在");
        }
        if(product.getStatus()!= 1){
            throw new BusinessException("商品未上架");
        }
        //3.扣库存
        Boolean success = stockClient.deduct(dto.getProductId(), dto.getQuantity());

        if(!Boolean.TRUE.equals(success)){
            throw new BusinessException("库存不足");
        }

        //4.计算订单金额
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        //5.创建订单
        Order order = new Order();
        order.setOrderNo(
                UUID.randomUUID().toString().replace("-","")
        );
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setPrice(product.getPrice());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        //0 = 待支付
        order.setStatus(0);

        //6.Mybatis-plus 保存订单
        orderMapper.insert(order);

        //制造异常
        //throw new BusinessException("模拟订单服务异常");
        return order;
    }
}
