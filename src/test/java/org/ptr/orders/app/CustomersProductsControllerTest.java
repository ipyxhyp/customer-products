package org.ptr.orders.app;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.ptr.orders.app.dao.CustomersRepository;
import org.ptr.orders.app.dao.ProductsRepository;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.model.Product;
import org.ptr.orders.app.rest.CustomersProductsController;
import org.ptr.orders.app.rest.dto.CustomerResponse;
import org.ptr.orders.app.rest.dto.ProductResponse;
import org.ptr.orders.app.service.CustomerProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@WebMvcTest(CustomersProductsController.class)
@AutoConfigureMockMvc
public class CustomersProductsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerProductsService customerProductsService;

    @MockBean
    private CustomersRepository customersRepository;

    @MockBean
    private ProductsRepository productsRepository;

    private List<Customer> customerList;
    private List<CustomerResponse> customerResponseList;
    private List<Product> productList = new ArrayList<>();
    private List<ProductResponse> productResponseList;

    @Before
    public void setUp(){

        customerList = Arrays.asList(1L, 2L, 3L, 4L, 5L).stream().map((i) ->
            Customer.builder()
                .id(i)
                .title("Test customer " + i)
                .isDeleted(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .build()
        ).collect(Collectors.toList());

        customerResponseList = customerList.stream().map(
            item -> CustomerResponse.builder().id(item.getId())
                .isDeleted(item.getIsDeleted())
                .createdAt(item.getCreatedAt())
                .title("Test customer " + item.getId())
                .build()
        ).collect(Collectors.toList());

        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).stream().forEach((i) -> {
            int customerId = i % 2;
            Product product = Product.builder()
                .id(i.longValue())
                .title("Test product " + i )
                .price(new BigDecimal(i * 100))
                .isDeleted(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .customer(customerList.get(customerId))
                .build();
            productList.add(product);
        });

        productResponseList = productList.stream().map(product -> ProductResponse.builder().id(product.getId())
            .isDeleted(product.getIsDeleted())
            .createdAt(product.getCreatedAt())
            .title(product.getTitle())
            .customerId(product.getCustomer().getId())
            .build()
        ).collect(Collectors.toList());
    }

    @Test
    public void testGetCustomerById() throws Exception {
        CustomerResponse customerResponse1 = CustomerResponse.builder().id(100500L)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .title("Test customer 1")
            .build();
        Customer customer1 = Customer.builder().id(100500L)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .title("Test customer 1")
            .build();
        when(customersRepository.findById(anyLong())).thenReturn(Optional.of(customer1));
        when(customerProductsService.getCustomerById(anyLong())).thenReturn(customerResponse1);
        mvc.perform(MockMvcRequestBuilders.get("/customers/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id", is((100500))));
    }


    @Test
    public void testGetAllCustomer() throws Exception {

        Page<Customer> customersPage = new PageImpl<>(customerList);

        Page<CustomerResponse> customersDtoPage = new PageImpl<>(customerResponseList);

        when(customersRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(customersPage);
        when(customerProductsService.getCustomers(ArgumentMatchers.any(Pageable.class))).thenReturn(customersDtoPage);

        mvc.perform(MockMvcRequestBuilders
            .get("/customers")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentPage", is(0)))
            .andExpect(jsonPath("$.totalItems", is((5))));
    }


    @Test
    public void testGetProductsByCustomer() throws Exception {

        List<Product> customerOneProducts = productList.stream()
            .filter( p-> p.getCustomer().getId() == 1)
            .collect(Collectors.toList());
        List<ProductResponse> customerOneProductsDto = productResponseList.stream()
            .filter( p-> p.getCustomerId() == 1)
            .collect(Collectors.toList());
        Page<Product> customerOneProductsPage = new PageImpl<>(customerOneProducts);
        Page<ProductResponse> customerOneProductsDtoPage = new PageImpl<>(customerOneProductsDto);

        when(productsRepository.findProductsByCustomer_Id(anyLong(), ArgumentMatchers.any(Pageable.class)))
            .thenReturn(customerOneProductsPage);
        when(customerProductsService.getProductsByCustomerId(anyLong(), ArgumentMatchers.any(Pageable.class)))
            .thenReturn(customerOneProductsDtoPage);

        mvc.perform(MockMvcRequestBuilders
            .get("/customers/1/products")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentPage", is(0)))
            .andExpect(jsonPath("$.totalItems", is((5))))
            .andExpect(jsonPath("$.productsList[0].customerId", is(1)))
            .andExpect(jsonPath("$.productsList[1].customerId", is(1)))
            .andExpect(jsonPath("$.productsList[2].customerId", is(1)));
    }
}
