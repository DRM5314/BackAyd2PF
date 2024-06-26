package com.library.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@AllArgsConstructor
@Getter
public class StudentCreateRequestDTO {
    private String name;
    private Long idCareer;
    private LocalDate dteBirth;
    private String carnet;
}
