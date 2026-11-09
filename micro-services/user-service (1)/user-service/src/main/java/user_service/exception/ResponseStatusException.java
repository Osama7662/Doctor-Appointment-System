package user_service.exception;

public class ResponseStatusException extends RuntimeException{
    public ResponseStatusException(String msg){
        super(msg);
    }
}
