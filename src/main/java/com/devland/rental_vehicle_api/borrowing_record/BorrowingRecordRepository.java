package com.devland.rental_vehicle_api.borrowing_record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devland.rental_vehicle_api.borrowing_record.model.BorrowingRecord;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {
    boolean existsByCustomerIdAndCarIdAndStatus(int customerId, int carId, String status);
}
