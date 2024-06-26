package com.library.service.student;

import com.library.dto.student.StudentCreateRequestDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.dto.student.StudentUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.model.Student;

import java.util.List;

public interface StudentService {
    StudentResponseDTO save(StudentCreateRequestDTO student) throws ServiceException;
    StudentResponseDTO findStudentByCarnet(String carne) throws ServiceException;
    Boolean existsByName(String name);
    Boolean existsByCarnet(String carne);
    Boolean isActive(String carne) throws ServiceException;
    StudentResponseDTO update(String carne, StudentUpdateRequestDTO update) throws ServiceException;
    Student findStudentByCarnetNotDto(String carnet) throws ServiceException;
    List<StudentResponseDTO> findAll();
}
