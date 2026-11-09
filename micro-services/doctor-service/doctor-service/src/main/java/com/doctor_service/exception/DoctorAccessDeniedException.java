package com.doctor_service.exception;

public class DoctorAccessDeniedException extends RuntimeException{
    public DoctorAccessDeniedException(String msg){
        super(msg);
    }
}
