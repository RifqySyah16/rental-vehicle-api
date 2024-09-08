package com.devland.rental_vehicle_api.borrowing_record;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devland.rental_vehicle_api.borrowing_record.model.BorrowingRecord;
import com.devland.rental_vehicle_api.borrowing_record.model.dto.BorrowingRecordUpdateRequestDTO;
import com.devland.rental_vehicle_api.car.CarService;
import com.devland.rental_vehicle_api.car.model.Car;
import com.devland.rental_vehicle_api.customer.CustomerService;
import com.devland.rental_vehicle_api.customer.model.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final CarService carService;
    private final CustomerService customerService;

    public BorrowingRecord findById(int id) {
        return this.borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new BorrowingRecordNotFoundException("BorrowingRecord ID Not Found"));
    }

    public Page<BorrowingRecord> getAll(Pageable pageable) {
        return this.borrowingRecordRepository.findAll(pageable);
    }

    public BorrowingRecord create(BorrowingRecord newRecord) {
        boolean alreadyBorrowing = borrowingRecordRepository.existsByCustomerIdAndCarIdAndStatus(
            newRecord.getCustomer().getId(), newRecord.getCar().getId(), "RENTED");
        
        if (alreadyBorrowing) {
            throw new BorrowingRecordAlreadyExistsException("Customer is already renting this car");
        }
    
        if (newRecord.getRentDate() == null) {
            newRecord.setRentDate(LocalDateTime.now());
        }
    
        return this.borrowingRecordRepository.save(newRecord);
    }

    public BorrowingRecord update(int id, BorrowingRecordUpdateRequestDTO requestDTO) {
        BorrowingRecord existingRecord = this.borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new BorrowingRecordNotFoundException("Record not found with id " + id));
    
        if ("RETURNED".equals(existingRecord.getStatus())) {
            throw new IllegalStateException("Cannot update a record that has already been returned");
        }

        Car car = carService.getOneById(requestDTO.getCarId());
        Customer customer = customerService.findById(requestDTO.getCustomerId());
    
        boolean alreadyBorrowing = borrowingRecordRepository.existsByCustomerIdAndCarIdAndStatus(
            requestDTO.getCustomerId(), requestDTO.getCarId(), "RENTED");
    
        if (alreadyBorrowing && !existingRecord.getId().equals(id)) {
            throw new BorrowingRecordAlreadyExistsException("Customer is already renting this car");
        }
    
        existingRecord.setCar(car);
        existingRecord.setCustomer(customer);
    
        if (requestDTO.getReturnDate() != null) {
            existingRecord.setReturnDate(requestDTO.getReturnDate());

            if (requestDTO.getReturnDate().isBefore(LocalDateTime.now()) || 
                requestDTO.getReturnDate().isEqual(LocalDateTime.now())) {
                existingRecord.setStatus("RETURNED");
            } else {
                existingRecord.setStatus("RENTED");
            }
        } else {
            throw new IllegalArgumentException("Return date cannot be null");
        }
        
        return this.borrowingRecordRepository.save(existingRecord);
    }
    

    public BorrowingRecord update(BorrowingRecord borrowingRecord) {
        return this.borrowingRecordRepository.save(borrowingRecord);
    }

    public void deleteById(int id) {
        this.findById(id);
        this.borrowingRecordRepository.deleteById(id);
    }

    public boolean isCarAlreadyRentedByCustomer(int customerId, Integer carId) {
        return borrowingRecordRepository.existsByCustomerIdAndCarIdAndStatus(customerId, carId, "RENTED");
    }
}
