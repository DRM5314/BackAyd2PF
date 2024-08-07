package com.library.controller;

import com.library.dto.student.StudentCreateRequestDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.dto.student.StudentUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentService studentService;
    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @PostMapping
    public ResponseEntity<StudentResponseDTO> create(@RequestBody StudentCreateRequestDTO requestDTO) throws ServiceException{
        return ResponseEntity.ok(studentService.save(requestDTO));
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @GetMapping("/{carne}")
    public ResponseEntity<StudentResponseDTO> findByCarne(@PathVariable String carne) throws ServiceException{
        return ResponseEntity.ok(studentService.findStudentByCarnet(carne));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @PutMapping("/{carne}")
    public ResponseEntity<StudentResponseDTO> update(@PathVariable String carne,@RequestBody StudentUpdateRequestDTO updateRequestDTO) throws ServiceException{
        return ResponseEntity.ok(studentService.update(carne,updateRequestDTO));
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @GetMapping("/findAll")
    public  ResponseEntity<List<StudentResponseDTO>> findAll(){
        return ResponseEntity.ok(studentService.findAll());
    }
}
