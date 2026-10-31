package user_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto{

    @NotBlank(message = "login Via Email Or Mobile")
    private String loginId;

    @NotBlank(message = "password Is Required")
    private String password;
}
