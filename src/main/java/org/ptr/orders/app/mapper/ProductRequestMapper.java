package org.ptr.orders.app.mapper;


import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.ptr.orders.app.model.Product;
import org.ptr.orders.app.rest.dto.ProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductRequestMapper extends ConfigurableMapper {

    protected void configure(MapperFactory factory) {
        factory.classMap(Product.class, ProductRequest.class)
            .byDefault()
//            .field("customer.id", "customerId")
            .mapNulls(Boolean.FALSE)
            .register();
    }
}