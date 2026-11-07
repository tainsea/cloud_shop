package cn.fourse.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "stock-service",
        fallbackFactory  = StockClientFallbackFactory.class
)
public interface StockClient {

    @PostMapping("/stocks/deduct")
    Boolean deduct(@RequestParam("productId")Long productId,
                   @RequestParam("count")Integer count);
}
