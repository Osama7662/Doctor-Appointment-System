package com.doctor_service.exception;

public class DoctorAlreadyExistsException extends RuntimeException{
    public DoctorAlreadyExistsException(String message){
        super(message);
    }
}
