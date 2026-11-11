package com.appointment_service.client;

import com.appointment_service.Dto.ApiResponseDto;
import com.appointment_service.Dto.DoctorResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "doctor-service"
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/user/{userId}")
    ApiResponseDto<DoctorResponseDto> getDoctorByUserId(
            @PathVariable("userId") Long userId,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/v1/doctors/read/{doctorId}")
    ApiResponseDto<DoctorResponseDto> getDoctorById(
            @PathVariable("doctorId") Long doctorId
    );

}
