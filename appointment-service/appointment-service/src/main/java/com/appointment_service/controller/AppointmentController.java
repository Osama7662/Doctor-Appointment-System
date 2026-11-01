package com.appointment_service.controller;

import com.appointment_service.Dto.ApiResponseDto;
import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.Dto.PageResponseDto;
import com.appointment_service.entity.Appointment;
import com.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponseDto<PageResponseDto<AppointmentResponseDto>>>getAllAppointments(
            @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageResponseDto<AppointmentResponseDto> allAppointments =
                appointmentService.getAllAppointments(pageNo, pageSize, sortBy, sortDir);
        ApiResponseDto<PageResponseDto<AppointmentResponseDto>> response = new ApiResponseDto<>();
        response.setMessage("All Appointments Fetched Successfully");
        response.setStatus(200);
        response.setData(allAppointments);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponseDto<List<AppointmentResponseDto>>> getAppointmentByPatientId(
            @PathVariable Long patientId
    ){
        List<AppointmentResponseDto> appointmentsByPatientId = appointmentService.getAppointmentsByPatientId(patientId);
        ApiResponseDto<List<AppointmentResponseDto>> response = new ApiResponseDto<>();
        response.setMessage("Patient Appointments Fetched Successfully");
        response.setStatus(HttpStatus.OK.value());
        response.setData(appointmentsByPatientId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponseDto<List<AppointmentResponseDto>>> getAppointmentByDoctorId(
            @PathVariable Long doctorId
    ){
        List<AppointmentResponseDto> appointmentByDoctorId = appointmentService.getAppointmentByDoctorId(doctorId);
        ApiResponseDto<List<AppointmentResponseDto>> response = new ApiResponseDto<>();
        response.setMessage("Doctor Appointment Fetched Successfully");
        response.setStatus(HttpStatus.OK.value());
        response.setData(appointmentByDoctorId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{appointmentId}")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> cancelAppointment(
            @PathVariable Long appointmentId
    ){
        AppointmentResponseDto cancelledAppointmentDto = appointmentService.cancelAppointment(appointmentId);
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Appointment Has Been Cancelled With AppointmentID: "+appointmentId);
        response.setStatus(HttpStatus.OK.value());
        response.setData(cancelledAppointmentDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
