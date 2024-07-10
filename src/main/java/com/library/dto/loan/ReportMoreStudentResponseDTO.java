package com.library.dto.loan;

import com.library.model.Loan;
import com.library.model.Payment;
import com.library.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
@Getter
public class ReportMoreStudentResponseDTO {
    private final Student carnet;
    private final List<Payment> paymens;
    private final Double totalCash;
    public ReportMoreStudentResponseDTO(List<Payment> paymens){
        this.carnet = paymens.get(0).getLoan().getCarnet();;
        this.paymens = paymens;
        this.totalCash = paymens.stream().mapToDouble(Payment::getTotal).sum();;
    }

}
