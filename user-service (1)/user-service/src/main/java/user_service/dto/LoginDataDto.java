package user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDataDto {
    private String Token;
    private UserDataDto userDataDto;
}
