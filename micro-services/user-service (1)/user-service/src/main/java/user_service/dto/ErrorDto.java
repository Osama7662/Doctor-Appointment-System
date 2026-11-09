package user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorDto {

    private LocalDateTime timeStamp;
    private String message;
    private String uri;
}
