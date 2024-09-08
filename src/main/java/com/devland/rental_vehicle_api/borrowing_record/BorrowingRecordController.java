package com.devland.rental_vehicle_api.borrowing_record;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devland.rental_vehicle_api.borrowing_record.model.BorrowingRecord;
import com.devland.rental_vehicle_api.borrowing_record.model.dto.BorrowingRecordCreateRequestDTO;
import com.devland.rental_vehicle_api.borrowing_record.model.dto.BorrowingRecordResponseDTO;
import com.devland.rental_vehicle_api.borrowing_record.model.dto.BorrowingRecordUpdateRequestDTO;
import com.devland.rental_vehicle_api.car.CarService;
import com.devland.rental_vehicle_api.car.model.Car;
import com.devland.rental_vehicle_api.customer.CustomerService;
import com.devland.rental_vehicle_api.customer.model.Customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/borrowing-records")
@RequiredArgsConstructor
public class BorrowingRecordController {
    private final BorrowingRecordService borrowingRecordService;
    private final CarService carService; 
    private final CustomerService customerService; 

    @GetMapping
    public ResponseEntity<Page<BorrowingRecordResponseDTO>> findAll(
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        Sort sort = Sort.by(Sort.Direction.valueOf(sortString), orderBy);
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);
        Page<BorrowingRecord> pageRecords = this.borrowingRecordService.getAll(pageable);
        Page<BorrowingRecordResponseDTO> responseDTOs = pageRecords.map(BorrowingRecord::convertToResponse);

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingRecordResponseDTO> getOneRecord(@PathVariable("id") int id) {
        BorrowingRecord existingRecord = this.borrowingRecordService.findById(id);
        BorrowingRecordResponseDTO responseDTO = existingRecord.convertToResponse();

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<BorrowingRecordResponseDTO> create(
        @RequestBody @Valid BorrowingRecordCreateRequestDTO requestDTO) {

        Car car = carService.getOneById(requestDTO.getCarId());
        Customer customer = customerService.findById(requestDTO.getCustomerId());
        
        BorrowingRecord newRecord = BorrowingRecord.builder()
                .customer(customer)
                .car(car)
                .rentDate(LocalDateTime.now())
                .returnDate(requestDTO.getReturnDate())
                .status("RENTED")
                .build();

        BorrowingRecord savedRecord = borrowingRecordService.create(newRecord);
        BorrowingRecordResponseDTO responseDTO = savedRecord.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<BorrowingRecordResponseDTO> update(
    //         @PathVariable("id") Integer id,
    //         @RequestBody @Valid BorrowingRecordUpdateRequestDTO requestDTO) {
        
    //     if (requestDTO.getReturnDate() == null) {
    //         return ResponseEntity.badRequest().body(null);
    //     }
    //     BorrowingRecord existingRecord = borrowingRecordService.findById(id);

    //     if (existingRecord.getReturnDate() != null) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT)
    //                 .body(null);
    //     }
    //     Car car = carService.getOneById(requestDTO.getCarId());
    //     Customer customer = customerService.findById(requestDTO.getCustomerId());

    //     boolean alreadyBorrowing = borrowingRecordService.isCarAlreadyRentedByCustomer(requestDTO.getCustomerId(), requestDTO.getCarId());

    //     if (alreadyBorrowing && !existingRecord.getId().equals(id)) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT)
    //                 .body(null); 
    //     }

    //     existingRecord.setCar(car);
    //     existingRecord.setCustomer(customer);
    //     existingRecord.setReturnDate(requestDTO.getReturnDate());
        
    //     if (requestDTO.getReturnDate().isBefore(LocalDateTime.now())) {
    //         existingRecord.setStatus("RETURNED");
    //     } else {
    //         existingRecord.setStatus("RENTED");
    //     }

    //     BorrowingRecord updatedRecord = borrowingRecordService.update(existingRecord);
    //     BorrowingRecordResponseDTO responseDTO = updatedRecord.convertToResponse();

    //     return ResponseEntity.ok(responseDTO);
    // }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable("id") Integer id,
            @RequestBody @Valid BorrowingRecordUpdateRequestDTO requestDTO) {

        Map<String, String> response = new HashMap<>();
        try {
            BorrowingRecord existingRecord = borrowingRecordService.findById(id);

            if ("RETURNED".equals(existingRecord.getStatus())) {
                response.put("messa", "Record has already been returned and cannot be updated.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); 
            }

            Car car = carService.getOneById(requestDTO.getCarId());
            Customer customer = customerService.findById(requestDTO.getCustomerId());

            boolean alreadyBorrowing = borrowingRecordService.isCarAlreadyRentedByCustomer(requestDTO.getCustomerId(), requestDTO.getCarId());

            if (alreadyBorrowing && !existingRecord.getId().equals(id)) {
                response.put("error", "Conflict with an existing rental.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            existingRecord.setCar(car);
            existingRecord.setCustomer(customer);

            if (requestDTO.getReturnDate() != null) {
                if (requestDTO.getReturnDate().isBefore(existingRecord.getRentDate())) {
                    response.put("error", "Return date cannot be before rent date.");
                    return ResponseEntity.badRequest().body(response); 
                }

                if (requestDTO.getReturnDate().isBefore(LocalDateTime.now())) {
                    existingRecord.setStatus("RETURNED");
                } else {
                    existingRecord.setStatus("RENTED");
                }
                existingRecord.setReturnDate(requestDTO.getReturnDate());
            } else {
                response.put("error", "Return date cannot be null.");
                return ResponseEntity.badRequest().body(response);
            }

            BorrowingRecord updatedRecord = borrowingRecordService.update(existingRecord);
            BorrowingRecordResponseDTO responseDTO = updatedRecord.convertToResponse();

            response.put("message", "Record updated successfully.");
            response.put("data", responseDTO.toString());
            return ResponseEntity.ok(response);

        } catch (BorrowingRecordNotFoundException e) {
            response.put("error", "Data not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (BorrowingRecordAlreadyExistsException e) {
            response.put("error", "Data already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid return date.");
            return ResponseEntity.badRequest().body(response);

        } catch (IllegalStateException e) {
            response.put("error", "Record has already been returned and cannot be updated.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") int id) {
        this.borrowingRecordService.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Success delete record");
        
        return ResponseEntity.ok(response);
    }

}
