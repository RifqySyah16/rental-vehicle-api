package com.devland.rental_vehicle_api.customer.model.dto;

import com.devland.rental_vehicle_api.customer.model.Customer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private int id;
    @NotBlank(message = "Name is required")
    private String name;

    public Customer convertToEntity() {
        return Customer.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
