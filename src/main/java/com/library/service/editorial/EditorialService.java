package com.library.service.editorial;


import com.library.dto.editorial.EditorialResponseDTO;
import com.library.dto.editorial.EditorialUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.model.Editorial;

import java.util.List;
import java.util.Optional;

public interface EditorialService {
    EditorialResponseDTO save(String name) throws ServiceException;
    EditorialResponseDTO update(Long id, EditorialUpdateRequestDTO name) throws ServiceException;
    EditorialResponseDTO findByCode(Long code) throws ServiceException;
    List<EditorialResponseDTO> findAll();

    Boolean existDuplicatedName(String name);
    Boolean existDuplicatedNameUpdate(Long id,String name);
    Editorial findByCodeNotDto(Long code) throws ServiceException;

}
