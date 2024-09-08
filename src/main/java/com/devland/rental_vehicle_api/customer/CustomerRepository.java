package com.devland.rental_vehicle_api.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devland.rental_vehicle_api.customer.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByName(String name);
    Optional<Customer> findById(Integer id);

    Page<Customer> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

}
