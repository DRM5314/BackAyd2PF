package com.library.controller.exceptionhandler;

import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.LimitBookLoanStudent;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.StudentInactive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handlerNotFoundException(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(DuplicatedEntityException.class)
    public ResponseEntity<String> handlerNotFoundException(DuplicatedEntityException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(StudentInactive.class)
    public ResponseEntity<String> handlerStudentInactive(StudentInactive ex){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }
    @ExceptionHandler(LimitBookLoanStudent.class)
    public ResponseEntity<String> handlerLimitBookLoan(LimitBookLoanStudent ex){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

}
