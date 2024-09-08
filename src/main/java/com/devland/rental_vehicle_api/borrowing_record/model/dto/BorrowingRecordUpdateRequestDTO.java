package com.devland.rental_vehicle_api.borrowing_record.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecordUpdateRequestDTO {
    @NotNull(message = "Car ID cannot be null")
    private Integer carId;

    @NotNull(message = "Customer ID cannot be null")
    private int customerId;

    @NotNull(message = "Return date cannot be null")
    private LocalDateTime returnDate;
}
