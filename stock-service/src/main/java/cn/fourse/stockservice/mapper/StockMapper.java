package cn.fourse.stockservice.mapper;

import cn.fourse.stockservice.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    @Update("""
        UPDATE stock set stock = stock -#{count} WHERE product_id = #{productId} AND stock >= #{count}
""")
    int deductStock(@Param("productId") Long productId,@Param("count") Integer count);
}
