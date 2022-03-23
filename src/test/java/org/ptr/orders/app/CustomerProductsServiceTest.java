package org.ptr.orders.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ptr.orders.app.rest.dto.CustomerRequest;
import org.ptr.orders.app.rest.dto.CustomerResponse;
import org.ptr.orders.app.rest.dto.ProductRequest;
import org.ptr.orders.app.rest.dto.ProductResponse;
import org.ptr.orders.app.service.CustomerProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CustomerProductsServiceTest {

    public static final List<Long> CUSTOMER_ID_LIST = Arrays.asList(1L, 2L, 3L, 4L);
    @Autowired
    private CustomerProductsService customerProductsService;
    @Test
    public void getAllCustomersTest() throws Exception {

        int page = 0;
        int size = 2;
        Pageable paging = PageRequest.of(page, size);
        Page<CustomerResponse> customerDtoPage = customerProductsService.getCustomers(paging);
        assertNotNull(customerDtoPage);
        assertTrue(customerDtoPage.getTotalElements() > 0 );
    }

    @Test
    public void getCustomerByIdTest() throws Exception {

        String customerTitle = "Test customer %d";
        CUSTOMER_ID_LIST.stream()
            .forEach( i -> {
                CustomerResponse customerResponse = customerProductsService.getCustomerById(i);
                assertNotNull(customerResponse);
                assertEquals(customerResponse.getId(), i);
                assertEquals(customerResponse.getTitle(), String.format(customerTitle, i));
            }
        );
    }

    @Test
    public void getProductsByCustomerIdTest() throws Exception {

        int page = 0;
        int size = 2;
        Pageable paging = PageRequest.of(page, size);
        CUSTOMER_ID_LIST.stream()
            .forEach( i -> {
                    Page<ProductResponse> productDtoPage = customerProductsService.getProductsByCustomerId(i, paging);
                    assertNotNull(productDtoPage);
                    List<ProductResponse> productRequestList = productDtoPage.getContent();
                    assertNotNull(productRequestList);
                    assertFalse(productRequestList.isEmpty());
                    assertEquals(productRequestList.get(0).getCustomerId(), i);
                }
            );
    }

    @Test
    public void createProductsForCustomerTest() throws Exception {

        final Long customerId = 2L;
        ProductRequest request = ProductRequest.builder()
            .customerId(customerId)
            .title("Created Product for customer : " + customerId )
            .price(BigDecimal.valueOf(100L))
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .isDeleted(Boolean.FALSE)
            .build();
        ProductResponse productResponse = customerProductsService.createProduct(customerId, request);
        assertNotNull(productResponse);
        assertNotNull(productResponse.getId());

    }

    @Test
    public void createCustomerTestSuccess() throws Exception {

        final String titleNewCreated = "New created customer 100500";
        CustomerRequest request = CustomerRequest.builder()
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .title(titleNewCreated)
            .isDeleted(Boolean.FALSE)
            .build();
        CustomerResponse response = customerProductsService.createCustomer(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(titleNewCreated, response.getTitle());

        CustomerResponse customerResponse = customerProductsService.getCustomerById(response.getId());
        assertNotNull(customerResponse);
        assertEquals(customerResponse.getId(), response.getId());
        assertEquals(customerResponse.getTitle(), titleNewCreated);

    }


    @Test
    public void updateCustomerTestSuccess() throws Exception {

        final String titleUpdated = "Updated customer 100500";
        final LocalDateTime modifiedDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final Long customerId = 3L;
        CustomerResponse customerResponse = customerProductsService.getCustomerById(customerId);
        assertNotNull(customerResponse);
        assertEquals(customerResponse.getId(), customerId);
        assertNotEquals(customerResponse.getTitle(), titleUpdated);
        assertNotEquals(customerResponse.getModifiedAt(), modifiedDateTime);

        CustomerRequest updateRequest = CustomerRequest.builder()
            .modifiedAt(modifiedDateTime)
            .title(titleUpdated)
            .build();
        Long updatedId = customerProductsService.updateCustomer(customerId, updateRequest);
        assertNotEquals(updatedId, Long.valueOf(-1L));
        assertEquals(updatedId, customerId);

        customerResponse = customerProductsService.getCustomerById(customerId);
        assertNotNull(customerResponse);
        assertEquals(customerResponse.getId(), updatedId);
        assertEquals(customerResponse.getTitle(), titleUpdated);
        assertEquals(customerResponse.getModifiedAt(), modifiedDateTime);

    }

    @Test(expected = NoSuchElementException.class)
    public void deleteCustomerTestSuccess() throws Exception {

        final Long customerId = 5L;
        CustomerResponse customerResponse = customerProductsService.getCustomerById(customerId);
        assertNotNull(customerResponse);
        assertEquals(customerResponse.getId(), customerId);

        customerProductsService.deleteCustomer(customerId);
        customerProductsService.getCustomerById(customerId);
    }

    @Test
    public void updateProductTestSuccess() throws Exception {

        final String titleUpdated = "Updated product 100500";
        final LocalDateTime modifiedDateTime = LocalDateTime.now();
        final Long productId = 7L;
        ProductResponse productResponse = customerProductsService.getProductById(productId);
        assertNotNull(productResponse);
        assertEquals(productResponse.getId(), productId);
        assertNotEquals(productResponse.getTitle(), titleUpdated);
        assertNotEquals(productResponse.getModifiedAt(), modifiedDateTime);

        ProductRequest updateRequest = ProductRequest.builder()
            .modifiedAt(modifiedDateTime)
            .title(titleUpdated)
            .build();
        Long updatedId = customerProductsService.updateProduct(productId, updateRequest);
        assertNotEquals(updatedId, Long.valueOf(-1L));
        assertEquals(updatedId, productId);

        productResponse = customerProductsService.getProductById(productId);
        assertNotNull(productResponse);
        assertEquals(productResponse.getId(), updatedId);
        assertEquals(productResponse.getTitle(), titleUpdated);
    }
}