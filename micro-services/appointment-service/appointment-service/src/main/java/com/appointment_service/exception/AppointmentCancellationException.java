package com.appointment_service.exception;

public class AppointmentCancellationException extends RuntimeException{
    public AppointmentCancellationException(String msg){
        super(msg);
    }
}
