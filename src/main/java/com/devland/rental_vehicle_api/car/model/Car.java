package com.devland.rental_vehicle_api.car.model;

import com.devland.rental_vehicle_api.car.model.dto.CarResponseDTO;

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
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String model;
    private String description;

    public CarResponseDTO convertToResponse() {
        return CarResponseDTO.builder()
                .id(this.id)
                .brand(this.model)
                .description(this.description)
                .build();
    }
}
