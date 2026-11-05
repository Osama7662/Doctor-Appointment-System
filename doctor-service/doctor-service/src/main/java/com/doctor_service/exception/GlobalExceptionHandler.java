package com.doctor_service.exception;

import com.doctor_service.Dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoctorAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleDuplicateDoctors(
            DoctorAlreadyExistsException ex,
            WebRequest request
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorDto> handleDoctorNotFound(
            DoctorNotFoundException ex,
            WebRequest request
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<ErrorDto> handleInvalidPagination(
            InvalidPaginationException ex,
            WebRequest request
    ){
        ErrorDto error  = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DoctorAccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(
            DoctorAccessDeniedException ex,
            WebRequest request
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
    }
}
