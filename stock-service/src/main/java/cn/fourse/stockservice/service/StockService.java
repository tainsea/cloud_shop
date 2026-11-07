package cn.fourse.stockservice.service;

import cn.fourse.stockservice.entity.Stock;
import cn.fourse.stockservice.mapper.StockMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    public Stock getByProductId(Long id){
        return stockMapper.selectOne(new LambdaQueryWrapper<Stock>().eq(Stock::getProductId,id));
    }

    public boolean deduct(
            Long productId,
            Integer count) {

        int rows = stockMapper.deductStock(
                productId,
                count
        );

        return rows > 0;
    }
}
