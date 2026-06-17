package com.sashurman.uploadservice.exception;

import com.sashurman.common.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class BusinessException extends AppException {
    public BusinessException(String massage){
        super(massage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
