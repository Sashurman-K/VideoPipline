package com.sashurman.userservice.exception;

import com.sashurman.common.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException(){
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
