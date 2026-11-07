package cn.fourse.productservice.service;

import cn.fourse.productservice.entity.Product;
import cn.fourse.productservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public Product getById(Long id){
        return productMapper.selectById(id);
    }

}
