package com.library.service.payment;

import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.PaymentEnum;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Payment;
import com.library.repository.PaymentRepository;
import com.library.service.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{
    private PaymentRepository paymentRepository;
    private LoanService loanService;
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, LoanService loanService){
        this.paymentRepository = paymentRepository;
        this.loanService = loanService;
    }

    @Override
    public PaymentResponseDto save(PaymentCreateRequestDTO create) throws ServiceException {
        Payment paymentNew = new Payment();
        paymentNew.setLoan(loanService.findByCodeNotDto(create.getLoan()));
        paymentNew.setType(create.getType());
        paymentNew.setTotal(create.getTotal());
        paymentNew.setDatePayment(create.getDatePayment());

        paymentNew = paymentRepository.save(paymentNew);
        return new PaymentResponseDto(paymentNew);
    }

    @Override
    public PaymentResponseDto findById(Long id) throws ServiceException {
        Payment returns = paymentRepository.findById(id).orElseThrow(()->
                new NotFoundException(String.format("This payment with id: %s, dont exist", id))
                );
        return new PaymentResponseDto(returns);
    }

    @Override
    public List<PaymentResponseDto> findAllByType(PaymentEnum type) {
        List<Payment> payments = paymentRepository.findAllByType(type);
        return payments.stream().map(PaymentResponseDto::new).toList();
    }
    @Override
    public List<PaymentResponseDto> findAllByTypeAndDate(PaymentEnum type, LocalDate init, LocalDate end) {
        List<Payment> payments = paymentRepository.findAllByTypeAndDatePaymentBetween(type,init,end);
        return payments.stream().map(PaymentResponseDto::new).toList();
    }
}
