package com.sashurman.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DuplicateException extends AppException{
    public DuplicateException(Class<?> tClass, UUID id){
        super(String.format("%s with id %s not found", tClass.getName(), id), HttpStatus.CONFLICT);
    }
    public DuplicateException(String massage){
        super(massage, HttpStatus.CONFLICT);
    }
}
