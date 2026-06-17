package com.sashurman.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotFoundException extends AppException {
    public NotFoundException(Class<?> tClass, UUID id){
        super(String.format("%s with id %s not found", tClass.getName(), id), HttpStatus.NOT_FOUND);
    }
    public NotFoundException(String massage){
        super(massage, HttpStatus.NOT_FOUND);
    }
}
