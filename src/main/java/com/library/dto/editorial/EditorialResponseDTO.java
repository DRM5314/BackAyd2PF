package com.library.dto.editorial;

import com.library.model.Editorial;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditorialResponseDTO {
    private final Long id;
    private final String name;

    public EditorialResponseDTO(Editorial editorial){
        this.id = editorial.getId() ;
        this.name = editorial.getName();
    }
}
