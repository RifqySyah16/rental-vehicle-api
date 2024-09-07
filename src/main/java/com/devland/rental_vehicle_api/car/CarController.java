package com.devland.rental_vehicle_api.car;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devland.rental_vehicle_api.car.model.Car;
import com.devland.rental_vehicle_api.car.model.dto.CarRequestDTO;
import com.devland.rental_vehicle_api.car.model.dto.CarResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public ResponseEntity<Page<CarResponseDTO>> getAll(
            @RequestParam("title") Optional<String> optionalModel,
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "1") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        Sort sort = Sort.by(Sort.Direction.valueOf(sortString), "id");
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);
        Page<Car> pageCars = this.carService.findAll(optionalModel, pageable);
        Page<CarResponseDTO> carResponseDTOs = pageCars.map(Car::convertToResponse);

        return ResponseEntity.ok(carResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getOne(@PathVariable("id") int id) {
        Car existingCar = this.carService.getOneById(id);
        CarResponseDTO carResponseDTO = existingCar.convertToResponse();

        return ResponseEntity.ok(carResponseDTO);
    }

    @PostMapping
    public ResponseEntity<CarResponseDTO> create(@RequestBody @Valid CarRequestDTO carRequestDTO) {
        Car newCar = carRequestDTO.convertToEntity();

        Car savedCar = this.carService.create(newCar);
        CarResponseDTO carResponseDTO = savedCar.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(carResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDTO> update(@PathVariable("id") int id,
            @RequestBody CarRequestDTO carRequestDTO) {

        Car updatedCar = carRequestDTO.convertToEntity();
        updatedCar.setId(id);
        Car savedCar = this.carService.update(updatedCar);
        CarResponseDTO carResponseDTO = savedCar.convertToResponse();

        return ResponseEntity.ok(carResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        this.carService.deletedBy(id);

        return ResponseEntity.ok().build();
    }

}
