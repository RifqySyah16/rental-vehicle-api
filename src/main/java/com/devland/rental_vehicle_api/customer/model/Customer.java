package com.devland.rental_vehicle_api.customer.model;

import com.devland.rental_vehicle_api.customer.model.dto.CustomerResponseDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public CustomerResponseDTO convertToResponse() {
        return CustomerResponseDTO.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

}
