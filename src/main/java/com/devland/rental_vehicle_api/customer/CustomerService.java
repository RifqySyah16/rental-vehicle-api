package com.devland.rental_vehicle_api.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devland.rental_vehicle_api.customer.model.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer findById(int id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer ID Not Found"));
    }

    public Page<Customer> getAll(Optional<String> optionalName, Pageable pageable) {
        if (optionalName.isPresent()) {
            return this.customerRepository.findAllByNameContainsIgnoreCase(optionalName.get(), pageable);
        }

        return this.customerRepository.findAll(pageable);
    }

    public Customer create(Customer newCustomer) {
        Optional<Customer> existingCustomer = this.customerRepository.findByName(newCustomer.getName());
        if (existingCustomer.isPresent()) {
            throw new CustomerAlreadyExistException("Customer Already Exist");
        }

        return this.customerRepository.save(newCustomer);
    }

    public Customer update(Customer updatedCustomer) {
        Customer existingCustomer = this.findById(updatedCustomer.getId());
        updatedCustomer.setId(existingCustomer.getId());

        return this.customerRepository.save(updatedCustomer);
    }

    public void deleteBy(int id) {
        Customer existingCustomer = this.findById(id);
        this.customerRepository.deleteById(existingCustomer.getId());
    }

}
