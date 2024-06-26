package com.library.exceptions;

public class DuplicatedEntityException extends ServiceException{
    public DuplicatedEntityException(String message){
        super(message);
    }
}
