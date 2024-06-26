package com.library.dto.editorial;

import com.library.model.Editorial;
import lombok.Getter;

@Getter
public class EditorialResponseDTO {
    private final Long id;
    private final String name;

    public EditorialResponseDTO(Editorial editorial){
        this.id = editorial.getId() ;
        this.name = editorial.getName();
    }
}
