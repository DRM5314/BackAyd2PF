package com.library.repository;

import com.library.model.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment,Long> {
    @Override
    List<Payment> findAll();
    Optional<Payment> findById(Long id);
}
