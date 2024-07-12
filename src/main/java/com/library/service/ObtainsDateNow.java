package com.library.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ObtainsDateNow {
    public LocalDate getDateNow(){
        return LocalDate.now();
    }

}
