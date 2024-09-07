package com.devland.rental_vehicle_api.car;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devland.rental_vehicle_api.car.model.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {

    Optional<Car> findByModel(String model);

    Page<Car> findAllByModelContainsIgnoreCase(String model, Pageable pageable);
}
