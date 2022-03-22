package org.ptr.orders.app.rest;

import io.swagger.annotations.Api;
import org.ptr.orders.app.rest.dto.CustomerResponse;
import org.ptr.orders.app.rest.dto.CustomerRequest;
import org.ptr.orders.app.rest.dto.CustomersTotalResponse;
import org.ptr.orders.app.rest.dto.ProductRequest;
import org.ptr.orders.app.rest.dto.ProductResponse;
import org.ptr.orders.app.rest.dto.ProductTotalResponse;
import org.ptr.orders.app.service.CustomerProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
    value = "Customers and Products API",
    produces = "application/json"
)
@RestController
public class CustomersProductsController {

    public static final String CUSTOMER_ID_MUST_BE_NOT_EMPTY = "input path variable customerId must not be empty/null";
    public static final String CUSTOMER_BY_ID_NOT_FOUND = "customer not found by given customerId %s";
    public static final String PRODUCT_ID_MUST_BE_NOT_EMPTY = "input path variable productId must not be empty/null";
    public static final String PRODUCT_DATA_ID_MUST_BE_NOT_EMPTY = "input product object must not be empty/null";

    private CustomerProductsService customerProductsService;


    /**
     * get All customers
     *
     * @return list of all customers
     */
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomers(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "2") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<CustomerResponse> pageCustomerDtos = customerProductsService.getCustomers(paging);

        CustomersTotalResponse customersTotalResponseResponse = CustomersTotalResponse.builder()
            .customersList(pageCustomerDtos.getContent())
            .totalItems(pageCustomerDtos.getTotalElements())
            .currentPage(pageCustomerDtos.getNumber())
            .build();
        return ResponseEntity.ok(customersTotalResponseResponse);

    }


    /**
     * get One customer by path
     *
     * @return customer found
     */
    @GetMapping(value = "/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {

        CustomerResponse customerResponse;
        if (customerId != null) {
            customerResponse = customerProductsService.getCustomerById(customerId);
            return ResponseEntity.ok(customerResponse);
        } else {
            return ResponseEntity.badRequest().body(String.format(CUSTOMER_BY_ID_NOT_FOUND, customerId));
        }
    }


    @GetMapping(value = "/customers/{customerId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsByCustomerId(@PathVariable Long customerId,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        if (customerId != null) {
            Page<ProductResponse> pageProductsDto = customerProductsService.getProductsByCustomerId(customerId, paging);
            ProductTotalResponse productsTotalDtoResponse = ProductTotalResponse.builder()
                .productsList(pageProductsDto.getContent())
                .totalItems(pageProductsDto.getTotalElements())
                .currentPage(pageProductsDto.getNumber())
                .build();
            return ResponseEntity.ok(productsTotalDtoResponse);
        } else {
            return ResponseEntity.badRequest().body(String.format(CUSTOMER_ID_MUST_BE_NOT_EMPTY, customerId));
        }
    }


    @PostMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequest customerRequest) {

        CustomerResponse createdCustomer;
        if (customerRequest != null) {
            createdCustomer = customerProductsService.createCustomer(customerRequest);
            if (createdCustomer != null) {
                return ResponseEntity.ok().body(createdCustomer);
            } else {
                return ResponseEntity.badRequest().body("customer cannot be created");
            }
        } else {
            return ResponseEntity.badRequest().body("customerRequest input is empty");
        }
    }

    @PutMapping(value = "/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCustomer(@PathVariable Long customerId,
        @RequestBody CustomerRequest customerRequest) {

        if (customerRequest != null && customerId != null) {
            customerId = customerProductsService.updateCustomer(customerId, customerRequest);
            return ResponseEntity.ok(customerId);
        } else {
            return ResponseEntity.badRequest().body(PRODUCT_DATA_ID_MUST_BE_NOT_EMPTY);
        }

    }

    @DeleteMapping(value = "/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {

        if (customerId != null) {
            customerProductsService.deleteCustomer(customerId);
            return ResponseEntity.ok().body(String.format("removed customerId : %d", customerId) );
        } else {
            return ResponseEntity.badRequest().body(CUSTOMER_ID_MUST_BE_NOT_EMPTY);
        }
    }

    /**
     * Create new product for existing customer
     *
     * @return productId or bad request
     */
    @PostMapping(value = "/customers/{customerId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNewProductForCustomer(@PathVariable Long customerId,
        @RequestBody ProductRequest productRequest) {

        if (productRequest != null && customerId != null) {
            ProductResponse productResponse = customerProductsService.createProduct(customerId, productRequest);
            return ResponseEntity.ok(productResponse.getId());
        } else {
            return ResponseEntity.badRequest().body(PRODUCT_DATA_ID_MUST_BE_NOT_EMPTY);
        }
    }

    @GetMapping(value = "/products/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {

        ProductResponse productResponse;
        if (productId != null) {
            productResponse = customerProductsService.getProductById(productId);
            return ResponseEntity.ok(productResponse);
        } else {
            return ResponseEntity.badRequest().body(PRODUCT_ID_MUST_BE_NOT_EMPTY);
        }
    }

    @PutMapping(value = "/products/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {

        if (productId != null && productRequest != null) {
            productId = customerProductsService.updateProduct(productId, productRequest);
            return ResponseEntity.ok(productId);
        } else {
            return ResponseEntity.badRequest().body(PRODUCT_DATA_ID_MUST_BE_NOT_EMPTY);
        }

    }


    @DeleteMapping(value = "/products/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {

        if (productId != null) {
            customerProductsService.deleteProduct(productId);
            return ResponseEntity.ok().body(String.format("removed productId : %d", productId) );
        } else {
            return ResponseEntity.badRequest().body(PRODUCT_ID_MUST_BE_NOT_EMPTY);
        }
    }

    @Autowired
    public void setCustomerProductsService(CustomerProductsService customerProductsService) {
        this.customerProductsService = customerProductsService;
    }

}
