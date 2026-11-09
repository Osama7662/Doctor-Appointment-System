package com.doctor_service.exception;

public class InvalidPaginationException extends RuntimeException{
    public InvalidPaginationException(String message){
        super(message);
    }
}
