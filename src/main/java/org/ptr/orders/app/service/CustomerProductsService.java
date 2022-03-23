package org.ptr.orders.app.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ptr.orders.app.dao.CustomersRepository;
import org.ptr.orders.app.dao.ProductsRepository;
import org.ptr.orders.app.mapper.CustomerMapper;
import org.ptr.orders.app.mapper.CustomerRequestMapper;
import org.ptr.orders.app.mapper.ProductMapper;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.model.Product;
import org.ptr.orders.app.rest.dto.CustomerResponse;
import org.ptr.orders.app.rest.dto.CustomerRequest;
import org.ptr.orders.app.rest.dto.ProductRequest;
import org.ptr.orders.app.rest.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * Service logic located here, for customer and products operations
 *
 * */
@Slf4j
@Service("customerService")
public class CustomerProductsService {

    private static final String CUSTOMER_BY_ID_NOT_FOUND = "customer by id : %d not found ";
    private static final String CUSTOMER_INPUT_INVALID = "customer is empty : %s, customerId is empty : %d ";
    private static final String PRODUCT_BY_ID_NOT_FOUND = "product by id : %d not found ";
    private static final String PRODUCTS_BY_CUSTOMER_ID_NOT_FOUND = "products by customer id : %d not found ";
    private static final String PRODUCTS_INPUT_INVALID = "product is empty : %s, product id is empty : %d ";


    private CustomerMapper customerMapper;

    private CustomerRequestMapper customerRequestMapper;

    private ProductMapper productMapper;

    private CustomersRepository customersRepository;

    private ProductsRepository productsRepository;

    @Autowired
    public void setCustomerMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Autowired
    public void setCustomerRequestMapper(CustomerRequestMapper customerRequestMapper) {
        this.customerRequestMapper = customerRequestMapper;
    }

    @Autowired
    public void setCustomersRepository(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @Autowired
    public void setProductsRepository(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }



    /**
     * @return all customers wrapped into Page<>CustomerDto</>
     * @param pageable
     *
     * */
    public Page<CustomerResponse> getCustomers(Pageable pageable) {

        Page<Customer> customerPage = customersRepository.findAll(pageable);
        if(customerPage != null){
            Page<CustomerResponse> customerDtoPage = customerPage.map(item -> customerMapper.map(item, CustomerResponse.class));
            return customerDtoPage;
        } else {
            return Page.empty();
        }
    }


    /***
     *
     * @return one customer mapped into CustomerDto
     * @param customerId
     *
     * */
    public CustomerResponse getCustomerById(Long customerId) {

        CustomerResponse customerResponse = new CustomerResponse();
            Optional<Customer> customer = customersRepository.findById(customerId);
            if(customer.isPresent()){
                customerMapper.map(customer.get(), customerResponse);
                log.trace("customerDto found by title : {}", customerResponse);
            } else {
                throwNoEntityFoundException(String.format(CUSTOMER_BY_ID_NOT_FOUND, customerId));
            }
        return customerResponse;
    }


    /***
     *
     * @return list of products for customer by
     * @param customerId
     *
     * */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCustomerId(Long customerId, Pageable pageable) {

        Page<ProductResponse> productDtoPage;

        Page<Product> productPage= productsRepository.findProductsByCustomer_Id(customerId, pageable);
        if(productPage != null){
            productDtoPage = productPage.map(product -> productMapper.map(product, ProductResponse.class));
            log.trace("productDtoList found by customerId : {}", productDtoPage.getContent());
            return productDtoPage;
        } else {
            log.warn(String.format(PRODUCTS_BY_CUSTOMER_ID_NOT_FOUND, customerId));
            return Page.empty();
        }
    }

    /**
     * Find product one by
     * @param productId
     * @return productDto found
     *
     * */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {

        ProductResponse productResponse = new ProductResponse();
        Optional<Product> product = this.productsRepository.findById(productId);
        if(product.isPresent()){
            Product realProduct = product.get();
            productMapper.map(realProduct, productResponse);
            log.trace("productDto found by id : {}", productResponse);
        } else {
            throwNoEntityFoundException(String.format(PRODUCT_BY_ID_NOT_FOUND, productId));
        }
        return productResponse;
    }

    /**
     * New customer created
     * @param customerRequest input dto object to map from
     * @return customerDto created, including id
     *
     * */
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {

        Customer customer = getCustomerFromRequest(customerRequest);
        customer = customersRepository.save(customer);
        log.trace("customer created : {} ", customer);
        return customerMapper.map(customer, CustomerResponse.class);

    }

    /**
     * Updates customer by
     * @param customerId
     *
     * */
    public Long updateCustomer(Long customerId, CustomerRequest customerRequest) {

        Long failedUpdateValue = -1L;
        if(customerId == null || customerRequest == null){
            throwInvalidInputException(String.format(CUSTOMER_INPUT_INVALID, customerRequest, customerId));
            return failedUpdateValue;
        } else {
            Optional<Customer> existingCustomer = customersRepository.findById(customerId);
            Customer customerToUpdate;
            if(existingCustomer.isPresent() ){
                customerToUpdate = customersRepository.save(
                    getCustomerFromCustomerRequest(customerRequest, existingCustomer.get())
                );
                return customerToUpdate.getId();
            } else {
                throwNoEntityFoundException(String.format(CUSTOMER_BY_ID_NOT_FOUND, customerId));
                return failedUpdateValue;
            }
        }
    }

    /**
     * removes customer by
     * @param customerId
     *
     *
     * */
    public void deleteCustomer(Long customerId) {
         customersRepository.deleteById(customerId);
    }

    /**
     * New product created
     * @param productRequest input dto object to map from
     * @return productResponse created, including id
     *
     * */
    public ProductResponse createProduct(Long customerId, ProductRequest productRequest) {

        Optional<Customer> customer = customersRepository.findById(customerId);

        if(customer.isPresent()){
            Product product = getProductFrom(productRequest);
                product.setCustomer(customer.get());
                product = productsRepository.save(product);
                log.trace("product created : {} for customer : {} ", product, customer.get().getId());
                return productMapper.map(product, ProductResponse.class);
        } else {
            log.warn("product cannot be created as customer is not found by id {} ", customerId);
            return new ProductResponse();
        }
    }


    /**
     * updates product by
     * @param productId
     * @param productRequest
     * @return updated productId
     *
     * */

    public Long updateProduct(Long productId, ProductRequest productRequest) {
        Long failedUpdateValue = -1L;
        if(productId == null || productRequest == null){
            throwInvalidInputException(String.format(PRODUCTS_INPUT_INVALID, productRequest, productId));
            return failedUpdateValue;
        } else {
            Optional<Product> existingProduct = productsRepository.findById(productId);
            Product productToUpdate;
            if(existingProduct.isPresent() ){
                productToUpdate = getProductFromRequest(productRequest, existingProduct.get());
                productToUpdate = productsRepository.save(
                    productToUpdate
                );
                return productToUpdate.getId();
            } else {
                throwNoEntityFoundException(String.format(PRODUCT_BY_ID_NOT_FOUND, productId));
                return failedUpdateValue;
            }
        }
    }

    /**
     * removes product by
     * @param productId
     *
     * */
    public void deleteProduct(Long productId) {
        productsRepository.deleteById(productId);
    }



    protected void throwNoEntityFoundException(final String infoMessage) {
        log.trace(infoMessage);
        throw new NoSuchElementException(infoMessage);
    }

    protected void throwInvalidInputException(final String infoMessage) {
        log.trace(infoMessage);
        throw new RuntimeException(infoMessage);
    }

    private Customer getCustomerFromRequest(CustomerRequest customerRequest) {
        return customerMapper.map(customerRequest, Customer.class);
    }

    private Customer getCustomerFromCustomerRequest(CustomerRequest customerRequest, Customer customer)  {
        customerRequestMapper.map(customerRequest, customer);
        return customer;
    }


    private Product getProductFromRequest(ProductRequest productRequest, Product product)  {
        if(productRequest != null && product != null){
            if(StringUtils.isNotBlank(productRequest.getTitle())){
                product.setTitle(productRequest.getTitle());
            }
            if(productRequest.getCreatedAt() != null){
                product.setCreatedAt(productRequest.getCreatedAt());
            }
            if(productRequest.getPrice() != null){
                product.setPrice(productRequest.getPrice());
            }
            if(productRequest.getModifiedAt() != null){
                product.setModifiedAt(productRequest.getModifiedAt());
            }
        }
        return product;
    }


    private Product getProductFrom(ProductRequest productRequest) {
        return productMapper.map(productRequest, Product.class);
    }
}
