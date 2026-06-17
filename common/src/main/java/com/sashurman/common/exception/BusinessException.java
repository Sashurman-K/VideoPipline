package com.sashurman.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends AppException{
    public BusinessException(String massage){
        super(massage, HttpStatus.BAD_REQUEST);
    }
}
