package com.appointment_service.controller;

import com.appointment_service.Dto.ApiResponseDto;
import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.Dto.PageResponseDto;
import com.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            @Valid @RequestBody AppointmentRequestDto requestDto,
            Authentication authentication
            ){
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        AppointmentResponseDto savedAppointmentDto = appointmentService.bookAppointment(requestDto, authenticatedUserId);
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
            @PathVariable Long patientId,
            Authentication authentication
    ){
        //-->get Authenticated user's Id
        Long authenticatedUserId = (Long) authentication.getPrincipal();

        // Step 2: Check whether user is ADMIN
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        // Step 3: Validate ownership for non-admin users
        if (!isAdmin && !authenticatedUserId.equals(patientId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access another patient's appointments"
            );
        }

        List<AppointmentResponseDto> appointmentsByPatientId = appointmentService.getAppointmentsByPatientId(patientId, authenticatedUserId);
        ApiResponseDto<List<AppointmentResponseDto>> response = new ApiResponseDto<>();
        response.setMessage("Patient Appointments Fetched Successfully");
        response.setStatus(HttpStatus.OK.value());
        response.setData(appointmentsByPatientId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponseDto<List<AppointmentResponseDto>>>
    getAppointmentByDoctorId(
            @PathVariable Long doctorId,
            Authentication authentication
    ) {

        // Step 1: Get authenticated user ID
        Long authenticatedUserId =
                (Long) authentication.getPrincipal();

        // Step 2: Check whether authenticated user is ADMIN
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        // Step 3: Call service method
        List<AppointmentResponseDto> appointmentByDoctorId =
                appointmentService.getAppointmentByDoctorId(
                        doctorId,
                        authenticatedUserId,
                        isAdmin
                );

        // Step 4: Prepare response
        ApiResponseDto<List<AppointmentResponseDto>> response =
                new ApiResponseDto<>();

        response.setMessage("Doctor Appointment Fetched Successfully");
        response.setStatus(HttpStatus.OK.value());
        response.setData(appointmentByDoctorId);

        // Step 5: Return response
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/cancel/{appointmentId}")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {

        // Get the authenticated user's ID from the JWT
        Long authenticatedUserId = (Long) authentication.getPrincipal();

        // Pass appointment ID and authenticated user ID to service
        AppointmentResponseDto cancelledAppointmentDto =
                appointmentService.cancelAppointment(
                        appointmentId,
                        authenticatedUserId
                );

        // Prepare response
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();

        response.setMessage(
                "Appointment Has Been Cancelled With AppointmentID: "
                        + appointmentId
        );

        response.setStatus(HttpStatus.OK.value());
        response.setData(cancelledAppointmentDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/admin/confirm/{appointmentId}")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> confirmAppointment(
            @PathVariable Long appointmentId
    ){
        AppointmentResponseDto confirmedAppointment =
                appointmentService.confirmAppointment(appointmentId);
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Appointment Confirmed For AppointmentId: "+appointmentId);
        response.setStatus(HttpStatus.OK.value());
        response.setData(confirmedAppointment);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/admin/reject/{appointmentId}")
    public ResponseEntity<ApiResponseDto<AppointmentResponseDto>> rejectAppointment(
            @PathVariable Long appointmentId
    ){
        AppointmentResponseDto rejectedAppointment = appointmentService.rejectAppointment(appointmentId);
        ApiResponseDto<AppointmentResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Appointment Rejected For AppointmentId: "+appointmentId);
        response.setStatus(HttpStatus.OK.value());
        response.setData(rejectedAppointment);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
