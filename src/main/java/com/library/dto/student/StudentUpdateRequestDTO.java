package com.library.dto.student;

import com.library.model.Career;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@AllArgsConstructor
@Getter
public class StudentUpdateRequestDTO {
    private String name;
    private Long idCareer;
    private LocalDate dteBirth;
    private Integer status;
}
