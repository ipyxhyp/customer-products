package org.ptr.orders.app.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.rest.dto.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper extends ConfigurableMapper {

    protected void configure(MapperFactory factory) {
        factory.classMap(Customer.class, CustomerResponse.class).byDefault().register();
    }
}
