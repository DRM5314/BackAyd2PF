package com.library.controller;

import com.library.dto.loan.ReportDatesAndCarnetRequestDTO;
import com.library.dto.loan.ReportMoreStudentResponseDTO;
import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.exceptions.ServiceException;
import com.library.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@PreAuthorize("hasAuthority('ADMIN')")
public class PaymentController {
    private PaymentService paymentService;
    @Autowired
    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }
    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(@RequestBody PaymentCreateRequestDTO create) throws ServiceException{
        return ResponseEntity.ok(paymentService.save(create));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> findById(@PathVariable Long id) throws ServiceException{
        return ResponseEntity.ok(paymentService.findById(id));
    }
    @PostMapping("/more-student")
    public ResponseEntity<ReportMoreStudentResponseDTO> findMoreStudentPaymenst(@RequestBody ReportDatesAndCarnetRequestDTO request) throws ServiceException{
        return ResponseEntity.ok(paymentService.findMoreStudent(request));
    }
}
