package com.devland.rental_vehicle_api.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devland.rental_vehicle_api.customer.model.Customer;
import com.devland.rental_vehicle_api.customer.model.dto.CustomerRequestDTO;
import com.devland.rental_vehicle_api.customer.model.dto.CustomerResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> findAll(
            @RequestParam("name") Optional<String> optionalName,
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "1") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        System.out.println("Page: " + page + ", Limit: " + limit);
        System.out.println("Sort Direction: " + sortString + ", Order By: " + orderBy);

        // Sort sort = Sort.by(Sort.Direction.valueOf(sortString), "id");
        Sort sort = Sort.by(Sort.Direction.valueOf(sortString), orderBy);
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);
        Page<Customer> pageCustomers = this.customerService.getAll(optionalName, pageable);
        
        System.out.println("Total Elements: " + pageCustomers.getTotalElements());
        Page<CustomerResponseDTO> customerResponseDTOs = pageCustomers.map(Customer::convertToResponse);

        return ResponseEntity.ok(customerResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getOneCustomer(@PathVariable("id") int id) {
        Customer existingCustomer = this.customerService.findById(id);
        CustomerResponseDTO customerResponseDTO = existingCustomer.convertToResponse();

        return ResponseEntity.ok(customerResponseDTO);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerRequestDTO customerRequestDTO) {
        Customer newCustomer = customerRequestDTO.convertToEntity();

        Customer savedCustomer = this.customerService.create(newCustomer);
        CustomerResponseDTO customerResponseDTO = savedCustomer.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable("id") int id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {

        Customer updatedCustomer = customerRequestDTO.convertToEntity();
        updatedCustomer.setId(id);
        Customer savedCustomer = this.customerService.update(updatedCustomer);
        CustomerResponseDTO customerResponseDTO = savedCustomer.convertToResponse();

        return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        this.customerService.deleteBy(id);

        return ResponseEntity.ok().build();
    }

}
