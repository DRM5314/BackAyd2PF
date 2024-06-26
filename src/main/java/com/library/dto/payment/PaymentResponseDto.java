package com.library.dto.payment;

import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.PaymentEnum;
import com.library.model.Loan;
import com.library.model.Payment;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PaymentResponseDto {
    private Long id;
    private LoanResponseDTO loan;
    private PaymentEnum type;
    private Double total;
    private LocalDate datePayment;

    public PaymentResponseDto(Payment payment){
        this.id = payment.getId();
        this.loan = new LoanResponseDTO(payment.getLoan());
        this.type = payment.getType();
        this.total = payment.getTotal();
        this.datePayment = payment.getDatePayment();
    }

}
