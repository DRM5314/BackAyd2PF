package com.library.dto.loan;

import com.library.dto.career.CareerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
@AllArgsConstructor
@Getter
public class ReportMoreCareerResponseDTO {
    private final CareerResponseDTO career;
    private final List<LoanResponseDTO> loans;
}
