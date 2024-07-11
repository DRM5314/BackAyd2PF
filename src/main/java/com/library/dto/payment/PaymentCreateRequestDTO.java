package com.library.dto.payment;

import com.library.enums.PaymentEnum;
import com.library.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class PaymentCreateRequestDTO {
    private Long loan;
    private PaymentEnum type;
}
