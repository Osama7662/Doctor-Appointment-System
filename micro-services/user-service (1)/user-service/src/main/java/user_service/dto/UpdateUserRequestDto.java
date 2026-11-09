package user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    private String mobile;
}