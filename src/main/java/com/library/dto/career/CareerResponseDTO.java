package com.library.dto.career;

import com.library.model.Career;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CareerResponseDTO {
    private Long id;
    private String name;
    public CareerResponseDTO(Career career){
        this.id = career.getId();
        this.name = career.getName();
    }
}
