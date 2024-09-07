package com.devland.rental_vehicle_api.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CarAlreadyExistException extends RuntimeException {

    public CarAlreadyExistException(String message) {
        super(message);
    }

}
