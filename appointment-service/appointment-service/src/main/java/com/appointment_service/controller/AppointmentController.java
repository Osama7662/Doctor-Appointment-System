package com.appointment_service.controller;

import com.appointment_service.Dto.ApiResponseDto;
import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> bookAppointment(
            @Valid @RequestBody AppointmentRequestDto requestDto
            ){
        AppointmentResponseDto savedAppointmentDto = appointmentService.bookAppointment(requestDto);
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Your Appointment Has Been Booked");
        response.setStatus(HttpStatus.CREATED.value());
        response.setData(savedAppointmentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> getAppointmentById(
            @PathVariable Long id
    ){
        AppointmentResponseDto gotAppointmentDto = appointmentService.getAppointmentById(id);
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Appointment Details Fetched Successfully For Appointment Id: "+id);
        response.setStatus(HttpStatus.OK.value());
        response.setData(gotAppointmentDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
