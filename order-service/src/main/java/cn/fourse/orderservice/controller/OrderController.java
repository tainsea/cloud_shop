package cn.fourse.orderservice.controller;


import cn.fourse.orderservice.dto.CreateOrderDTO;
import cn.fourse.orderservice.entity.Order;
import cn.fourse.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@RefreshScope
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderDTO dto){
        return orderService.createOrder(dto);
    }
    @GetMapping("/test")
    public String test() {
        return "order test";
    }
}
