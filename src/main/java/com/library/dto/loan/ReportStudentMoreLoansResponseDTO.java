package com.library.dto.loan;

import com.library.dto.student.StudentResponseDTO;
import com.library.model.Loan;
import com.library.model.Student;
import lombok.Getter;
import java.util.List;
@Getter
public class ReportStudentMoreLoansResponseDTO {
    private final StudentResponseDTO student;
    private final List<LoanResponseDTO> loans;
    private final Integer totalLoans;
    public ReportStudentMoreLoansResponseDTO(Student student, List<Loan> loans){
        this.student = new StudentResponseDTO(student);
        this.loans = loans.stream().map(LoanResponseDTO::new).toList();
        this.totalLoans = loans.size();
    }
}
