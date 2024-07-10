package com.library.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ReportDatesAndCarnetRequestDTO {
    private final String carnet;
    private final LocalDate init;
    private final LocalDate end;
}
