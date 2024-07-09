package com.library.repository;

import com.library.enums.PaymentEnum;
import com.library.model.Payment;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment,Long> {
    @Override
    List<Payment> findAll();
    Optional<Payment> findById(Long id);
    List<Payment> findAllByType(PaymentEnum type);
    List<Payment> findAllByTypeAndDatePaymentBetween(PaymentEnum type, LocalDate init, LocalDate end);
}
