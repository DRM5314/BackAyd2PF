package com.library.service.career;


import com.library.dto.career.CareerResponseDTO;
import com.library.dto.career.CareerUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.model.Career;

import java.util.List;
import java.util.Optional;

public interface CareerService {
    CareerResponseDTO save(String career) throws ServiceException;
    CareerResponseDTO findByIdDto(Long id) throws ServiceException;
    CareerResponseDTO update(Long id, CareerUpdateRequestDTO update) throws ServiceException;
    List<CareerResponseDTO> findAll();
    Career findByIdNoDto(Long id)throws ServiceException;
    Boolean existByName(String name);
    Boolean existByNameUpdate(String name,Long id);
}
