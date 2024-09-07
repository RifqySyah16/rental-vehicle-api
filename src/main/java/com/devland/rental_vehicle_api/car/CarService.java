package com.devland.rental_vehicle_api.car;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.devland.rental_vehicle_api.car.model.Car;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public Car getOneById(int id) {
        return this.carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car ID Not Found"));
    }

    public Page<Car> findAll(Optional<String> optionalModel, PageRequest pageable) {
        if (optionalModel.isPresent()) {
            return this.carRepository.findAllByModelContainsIgnoreCase(optionalModel.get(), pageable);
        }

        return this.carRepository.findAll(pageable);
    }

    public Car create(Car newCar) {
        Optional<Car> existingCar = this.carRepository.findByModel(newCar.getModel());
        if (existingCar.isPresent()) {
            throw new CarAlreadyExistException("Car Already Exist");
        }

        return this.carRepository.save(newCar);
    }

    public Car update(Car updatedCar) {
        Car existingCar = this.getOneById(updatedCar.getId());
        updatedCar.setId(existingCar.getId());

        return this.carRepository.save(updatedCar);
    }

    public void deletedBy(int id) {
        Car existingCar = this.getOneById(id);
        this.carRepository.deleteById(existingCar.getId());
    }

}
