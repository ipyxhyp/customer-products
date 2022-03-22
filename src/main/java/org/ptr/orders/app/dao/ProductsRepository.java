package org.ptr.orders.app.dao;

import java.util.List;
import org.ptr.orders.app.model.Customer;
import org.ptr.orders.app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {

    Page<Product> findProductsByCustomer_Id(@Param("customer_id") Long customerId, Pageable pageable);
}
