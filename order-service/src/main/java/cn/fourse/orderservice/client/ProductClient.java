package cn.fourse.orderservice.client;

import cn.fourse.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient("product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductDTO getById(@PathVariable("id") Long id);
}
