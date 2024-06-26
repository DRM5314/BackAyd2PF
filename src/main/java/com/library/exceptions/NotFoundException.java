package com.library.exceptions;

public class NotFoundException extends ServiceException {
    public NotFoundException(String message){
        super(message);
    }
}
