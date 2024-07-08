package com.library.service.student;

import com.library.dto.student.StudentCreateRequestDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.dto.student.StudentUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Student;
import com.library.repository.StudentRepository;
import com.library.repository.UserRepository;
import com.library.service.career.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService{
    private StudentRepository studentRepository;
    private CareerService careerService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,CareerService careerService
                              ){
        this.studentRepository = studentRepository;
        this.careerService = careerService;
    }

    @Override
    public StudentResponseDTO save(StudentCreateRequestDTO student) throws ServiceException {
        if(existsByName(student.getName())){
            throw new DuplicatedEntityException(String.format("Student with name: %s, already exists",student.getName()));
        }
        if(existsByCarnet(student.getCarnet())){
            throw new DuplicatedEntityException(String.format("Student with carnet: %s, already exists",student.getCarnet()));
        }
        Student studentSave = new Student();
        studentSave.setName(student.getName());
        studentSave.setIdCareer(careerService.findByIdNoDto(student.getIdCareer()));
        studentSave.setDteBirth(student.getDteBirth());
        studentSave.setCarnet(student.getCarnet());
        studentSave.setStatus(1);

        studentSave = studentRepository.save(studentSave);
        return new StudentResponseDTO(studentSave);
    }

    @Override
    public StudentResponseDTO findStudentByCarnet(String carne) throws ServiceException{
        Student student = findStudentByCarnetNotDto(carne);
        return new StudentResponseDTO(student);
    }

    @Override
    public Boolean existsByName(String name) {
        return studentRepository.existsByName(name);
    }

    @Override
    public Boolean existsByCarnet(String carne) {
        return studentRepository.existsByCarnet(carne);
    }

    @Override
    public Boolean isActive(String carne) throws ServiceException {
        Student student = findStudentByCarnetNotDto(carne);
        return student.getStatus() == 1;
    }
    @Override
    public StudentResponseDTO update(String carne, StudentUpdateRequestDTO studentUpdate) throws ServiceException {
        Student student = findStudentByCarnetNotDto(carne);
        if(studentRepository.existsByNameAndCarnetIsNot(studentUpdate.getName(),carne)){
            throw new DuplicatedEntityException(String.format("Student with name: %s, already exists",studentUpdate.getName()));
        }
        student.setName(studentUpdate.getName());
        student.setIdCareer(careerService.findByIdNoDto(studentUpdate.getIdCareer()));
        student.setDteBirth(studentUpdate.getDteBirth());
        student.setStatus(studentUpdate.getStatus());
        student.setStatus(1);
        student = studentRepository.save(student);
        return new StudentResponseDTO(student);
    }

    @Override
    public Student findStudentByCarnetNotDto(String carnet) throws ServiceException {
        return studentRepository.findByCarnet(carnet).orElseThrow(()->
                new NotFoundException(String.format("Student with carne: %s, not exist",carnet))
        );
    }

    @Override
    public List<StudentResponseDTO> findAll() {
        return studentRepository.findAll().stream().map(StudentResponseDTO::new).collect(Collectors.toList());
    }
    @Override
    public Student updateNoDto(Student student) {
        return studentRepository.save(student);
    }
}
