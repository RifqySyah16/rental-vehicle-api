package com.devland.rental_vehicle_api.borrowing_record;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class BorrowingRecordAlreadyExistsException extends RuntimeException{
    public BorrowingRecordAlreadyExistsException(String message) {
        super(message);
    }
}
