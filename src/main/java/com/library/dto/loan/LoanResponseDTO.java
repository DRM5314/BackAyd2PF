package com.library.dto.loan;

import com.library.dto.book.BookResponseDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.enums.LoanEnum;
import com.library.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class LoanResponseDTO {
    private Long id;
    private BookResponseDTO bookCode;
    private StudentResponseDTO carnet;
    private LocalDate laonDate;
    private LocalDate returnDate;
    private LoanEnum state;
    private Double loan_fee;
    private Double penalized_fee;
    private Double sanction_fee;

    public LoanResponseDTO(Loan loan){
        this.id = loan.getId();
        this.bookCode = new BookResponseDTO(loan.getBookCode());
        this.carnet = new StudentResponseDTO(loan.getCarnet());
        this.laonDate = loan.getLaonDate();
        this.returnDate = loan.getReturnDate();
        this.state = loan.getState();
        this.loan_fee = loan.getLoan_fee();
        this.penalized_fee = loan.getPenalized_fee();
        this.sanction_fee = loan.getSanction_fee();
    }
}
