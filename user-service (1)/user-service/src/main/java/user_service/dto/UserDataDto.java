package user_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @JsonPropertyOrder({
            "id",
            "name",
            "email",
            "mobile",
            "role"
    })
    public class UserDataDto {

        private Long id;
        private String name;
        private String email;
        private String mobile;
        private String role;
    }