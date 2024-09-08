package com.devland.rental_vehicle_api.borrowing_record.model;

import java.time.LocalDateTime;

import com.devland.rental_vehicle_api.borrowing_record.model.dto.BorrowingRecordResponseDTO;
import com.devland.rental_vehicle_api.car.model.Car;
import com.devland.rental_vehicle_api.customer.model.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "borrowing_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime rentDate;
    private LocalDateTime returnDate;
    private String status;

    public BorrowingRecordResponseDTO convertToResponse() {
        return BorrowingRecordResponseDTO.builder()
                .id(this.id)
                .carId(this.car.getId())
                .carName(this.car.getModel())
                .customerId(this.customer.getId())
                .customerName(this.customer.getName())
                .rentDate(this.rentDate)
                .returnDate(this.returnDate)
                .status(this.status)
                .build();
    }
}
