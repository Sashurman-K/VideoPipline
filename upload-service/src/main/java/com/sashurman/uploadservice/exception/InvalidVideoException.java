package com.sashurman.uploadservice.exception;

import com.sashurman.common.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidVideoException extends AppException {
    public InvalidVideoException(){
        super("Invalid video file", HttpStatus.BAD_REQUEST);
    }
    public  InvalidVideoException(String massage){
        super(massage, HttpStatus.BAD_REQUEST);
    }
}
