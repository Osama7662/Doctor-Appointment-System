package user_service.exception;


import org.springframework.boot.micrometer.metrics.autoconfigure.MetricsProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import user_service.dto.ErrorDto;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedExceptions(  //General Exceptions
            Exception ex,
            WebRequest req
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleDuplicateUsers(
            UserAlreadyExistsException ex ,
            WebRequest req
    ){
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                "Validation Failed: "+errors,
                request.getDescription(false)
        );

        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleInvalidCredentials(
            InvalidCredentialsException ex,
            WebRequest req
    ){
        ErrorDto error = new ErrorDto(
         LocalDateTime.now(),
         ex.getMessage(),
         req.getDescription(false)
        );
       return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

}
