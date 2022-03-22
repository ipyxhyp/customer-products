package org.ptr.orders.app;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.ptr.orders.app.dao.CustomersRepository;
import org.ptr.orders.app.dao.ProductsRepository;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@Slf4j
@SpringBootApplication
public class OrdersManagementApplication {

    public static final String TEST_CUSTOMER = "Test customer ";
    public static final String TEST_PRODUCT = "Test product ";

    public static final void main(String[] args){

        SpringApplication.run(OrdersManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(CustomersRepository customersRepository, ProductsRepository productsRepository) {
        return (String[] args) -> {
            Arrays.asList(1, 2 , 3 , 4, 5).stream().forEach((i) -> {
                Customer customer = Customer.builder()
                    .title(TEST_CUSTOMER + i )
                    .isDeleted(Boolean.FALSE)
                    .createdAt(LocalDateTime.now())
                    .build();
                customersRepository.save(customer);
            });
            Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L).stream().forEach((i) -> {
                Long customerId = i / 2;
                Product product = Product.builder()
                    .title(TEST_PRODUCT + i )
                    .price(new BigDecimal(i * 100))
                    .isDeleted(Boolean.FALSE)
                    .createdAt(LocalDateTime.now())
                    .customer(customersRepository.getOne(customerId))
                    .build();
                productsRepository.save(product);
            });
        };
    }
}
