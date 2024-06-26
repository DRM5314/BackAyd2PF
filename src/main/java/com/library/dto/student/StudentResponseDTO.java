package com.library.dto.student;

import com.library.model.Career;
import com.library.model.Student;
import lombok.Getter;

import java.time.LocalDate;
@Getter
public class StudentResponseDTO {
    private Long id;
    private String name;
    private Career idCareer;
    private LocalDate dteBirth;
    private String carnet;
    private Integer status;
    public StudentResponseDTO(Student student){
        this.id = student.getId();
        this.name = student.getName();
        this.idCareer = student.getIdCareer();
        this.dteBirth = student.getDteBirth();
        this.carnet = student.getCarnet();
        this.status = student.getStatus();
    }
}
