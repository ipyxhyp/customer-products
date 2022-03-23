package org.ptr.orders.app.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.ptr.orders.app.model.Product;
import org.ptr.orders.app.rest.dto.ProductRequest;
import org.ptr.orders.app.rest.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper extends ConfigurableMapper {

    protected void configure(MapperFactory factory) {
        factory.classMap(Product.class, ProductResponse.class)
            .byDefault()
            .field("customer.id", "customerId")
            .mapNulls(Boolean.TRUE)
            .register();
    }
}