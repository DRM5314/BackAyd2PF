package com.library.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ReportDatesRequestDTO {
    private final LocalDate init;
    private final LocalDate end;
}
