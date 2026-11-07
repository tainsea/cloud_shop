package cn.fourse.stockservice.controller;


import cn.fourse.stockservice.entity.Stock;
import cn.fourse.stockservice.mapper.StockMapper;
import cn.fourse.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;



    @GetMapping("/{id}")
    public Stock getByProductId(@PathVariable Long id){
        return stockService.getByProductId(id);
    }

    @PostMapping("/deduct")
    public boolean deduct(
            @RequestParam Long productId,
            @RequestParam Integer count) throws InterruptedException {

//        Thread.sleep(3000);

        return stockService.deduct(
                productId,
                count
        );
    }

}
