package com.devland.rental_vehicle_api.borrowing_record.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecordResponseDTO {

    private int id;
    private int carId;
    private String carName; 
    private int customerId;
    private String customerName; 
    private LocalDateTime rentDate;
    private LocalDateTime returnDate;
    private String status; 
}