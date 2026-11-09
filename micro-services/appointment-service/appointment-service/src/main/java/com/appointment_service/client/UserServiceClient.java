package com.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appointment_service.Dto.UserValidationDto;
import com.appointment_service.Dto.UserResponseDto;

@FeignClient(
        name = "user-service"
)
public interface UserServiceClient {

    @GetMapping("/api/u1/user/validate/{userId}")
    UserResponseDto<UserValidationDto> validateUser(
            @PathVariable("userId") Long userId
    );
}
