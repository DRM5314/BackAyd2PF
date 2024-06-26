package com.library.dto.loan;

import com.library.enums.LoanEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LoanCreateRequestDTO {
    private String bookCode;
    private String carnet;
    private LocalDate laonDate;
    private LoanEnum status;
}





