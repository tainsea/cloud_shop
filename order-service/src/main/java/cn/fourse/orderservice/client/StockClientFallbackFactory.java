package cn.fourse.orderservice.client;


import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class StockClientFallbackFactory
        implements FallbackFactory<StockClient> {

    @Override
    public StockClient create(Throwable cause) {

        return new StockClient() {

            @Override
            public Boolean deduct(
                    Long productId,
                    Integer count) {

                System.err.println(
                        "库存服务调用失败："
                                + cause.getMessage()
                );

                throw new RuntimeException(
                        "库存服务暂时不可用，请稍后重试"
                );
            }
        };
    }
}