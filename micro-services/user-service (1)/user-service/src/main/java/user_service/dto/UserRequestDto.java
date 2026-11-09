package user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserRequestDto {

    @NotBlank(message = "Name Is Required!!")
    private String name;

    @NotBlank(message = "Email Is Required!!")
    @Email(message = "Invalid Email!!")
    private String email;

    @NotBlank(message = "Password Is Required!!")
    @Size(min = 8, message = "Password must contains at least 8 Characters!!")
    private String password;

    @NotBlank(message = "Mobile Number Is Required!!")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    private String mobile;

    @NotBlank(message = "Patient or Doctor role is required!!")
    @Pattern(
            regexp = "PATIENT|DOCTOR",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Role must be PATIENT or DOCTOR"
    )
    private String role;


}
