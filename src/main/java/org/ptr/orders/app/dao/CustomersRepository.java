package org.ptr.orders.app.dao;

import org.ptr.orders.app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface CustomersRepository extends JpaRepository<Customer, Long> {

}
