package user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserValidationDto {

    private Long id;

    private String role;

    private String approvalStatus;

    private boolean active;
}

