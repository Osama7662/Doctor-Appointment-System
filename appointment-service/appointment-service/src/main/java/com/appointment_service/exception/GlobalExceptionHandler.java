package com.appointment_service.exception;

import com.appointment_service.Dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorDto> handleAppointmentNotFound(
            AppointmentNotFoundException ex,
            WebRequest req
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest req
    ){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now(),
                 "Failed To Validate Inputs!!"+errors,
                req.getDescription(false)
        );
        return new ResponseEntity<>(errorDto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGlobalException(
           Exception ex,
           WebRequest req
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                "Something Went Wrong!!",
                req.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppointmentCancellationException.class)
    public ResponseEntity<ErrorDto> handleCancelledAppointments(
            AppointmentCancellationException ex,
            WebRequest req
            ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
