package com.library.dto.loan;

import com.library.model.Loan;
import lombok.Getter;
import java.util.*;
@Getter
public class ReportTotalCashResponseDTO {
    private final List<LoanResponseDTO> loans;
    private final Double totalCashSanction;
    private final Double totalCashNormal;
    private final Double totalPenalized;

    public ReportTotalCashResponseDTO(List<Loan> loans, Double totalCashSanction, Double totalCashNormal, Double totalPenalized){
        this.loans = loans.stream().map(LoanResponseDTO::new).toList();
        this.totalCashSanction = totalCashSanction;
        this.totalCashNormal = totalCashNormal;
        this.totalPenalized = totalPenalized;

    }
}
