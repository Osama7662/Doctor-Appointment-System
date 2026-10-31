package com.appointment_service.exception;


public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(String msg){
        super(msg);
    }
}
