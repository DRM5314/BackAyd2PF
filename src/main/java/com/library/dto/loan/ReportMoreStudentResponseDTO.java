package com.library.dto.loan;

import com.library.model.Payment;
import com.library.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReportMoreStudentResponseDTO {
    private final Student carnet;
    private final List<LaonVsPaymentResponseDTO> loans;
    private final Double totalCash;
    public ReportMoreStudentResponseDTO(List<Payment> paymens){
        this.carnet = paymens.get(0).getLoan().getCarnet();;
        this.loans = paymens.stream().map(payment -> new LaonVsPaymentResponseDTO(payment.getLoan(), payment.getDatePayment(), payment.getType())).collect(Collectors.toList());
        this.totalCash = paymens.stream().mapToDouble(Payment::getTotal).sum();;
    }

}
