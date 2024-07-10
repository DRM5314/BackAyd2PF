package com.library.dto.payment;

import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.PaymentEnum;
import com.library.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@Getter
public class LaonVsPaymentResponseDTO {
    private LoanResponseDTO loan;
    private LocalDate datePayment;
    private PaymentEnum type;
    public LaonVsPaymentResponseDTO(Loan loan, LocalDate datePayment, PaymentEnum type) {
        this.loan = new LoanResponseDTO(loan);
        this.datePayment = datePayment;
        this.type = type;
    }
}
