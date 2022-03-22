package org.ptr.orders.app.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.rest.dto.CustomerRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomerRequestMapper extends ConfigurableMapper {

    protected void configure(MapperFactory factory) {
        factory.classMap(CustomerRequest.class, Customer.class)
            .mapNulls(false)
            .byDefault().register();
    }

}
