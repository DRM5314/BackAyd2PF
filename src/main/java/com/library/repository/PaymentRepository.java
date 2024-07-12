package com.library.repository;

import com.library.enums.PaymentEnum;
import com.library.model.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment,Long> {
    @Override
    List<Payment> findAll();
    List<Payment> findAllByLoan_Carnet_Carnet(String carnet);
    Optional<Payment> findById(Long id);
    List<Payment> findAllByType(PaymentEnum type);
    List<Payment> findAllByTypeAndDatePaymentBetween(PaymentEnum type, LocalDate init, LocalDate end);
    @Query ("SELECT p FROM Payment p where p.loan.carnet.carnet = :carne and p.type = :t and p.datePayment between :i and :e group by p.loan.carnet.carnet ")
    List<Payment> findMoreStudent(@PathVariable String carne,@PathVariable PaymentEnum t ,@PathVariable LocalDate i,@PathVariable LocalDate e);
}
