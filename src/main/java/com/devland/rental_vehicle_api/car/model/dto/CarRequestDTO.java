package com.devland.rental_vehicle_api.car.model.dto;

import com.devland.rental_vehicle_api.car.model.Car;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarRequestDTO {
    private int id;
    @NotBlank(message = "Name is required")
    private String model;
    @NotBlank(message = "Description is required")
    private String description;

    public Car convertToEntity() {
        return Car.builder()
                .id(this.id)
                .model(this.model)
                .description(this.description)
                .build();
    }
}
