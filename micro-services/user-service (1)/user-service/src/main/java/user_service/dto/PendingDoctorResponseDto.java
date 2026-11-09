package user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingDoctorResponseDto {

    private Long id;

    private String name;

    private String email;

    private String mobile;

    private String role;

    private String approvalStatus;

}
