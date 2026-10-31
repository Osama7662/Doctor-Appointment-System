package com.doctor_service.controller;

import com.doctor_service.Dto.*;
import com.doctor_service.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<DoctorResponseDto>> registerDoctor(
            @Valid @RequestBody DoctorRequestDto requestDto
            ){
        DoctorResponseDto savedDoctorDto = doctorService.registerDoctor(requestDto);
        ApiResponseDto<DoctorResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Doctor Registered Successfully");
        response.setStatus(200);
        response.setData(savedDoctorDto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/read/{doctorId}")
    public ResponseEntity<ApiResponseDto<DoctorResponseDto>> getDoctorById(@PathVariable Long doctorId){
        DoctorResponseDto foundDoctorDto = doctorService.getDoctorById(doctorId);
        ApiResponseDto<DoctorResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Doctor Fetched Successfully With id: "+doctorId);
        response.setStatus(201);
        response.setData(foundDoctorDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<PageResponseDto<DoctorResponseDto>> getAllDoctors(
            @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageResponseDto<DoctorResponseDto> allDoctorsInPaging = doctorService.getAllDoctors(pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allDoctorsInPaging,HttpStatus.OK);
    }

    @PutMapping("/update/{doctorId}")
    public ResponseEntity<ApiResponseDto<DoctorResponseDto>> updateDoctors(
            @PathVariable Long doctorId,
            @RequestBody UpdateDoctorRequestDto updateDto
            ){
        DoctorResponseDto updatedDoctorDto = doctorService.updateDoctor(doctorId, updateDto);
        ApiResponseDto<DoctorResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Your Profile Has Been Updated Successfully");
        response.setStatus(200);
        response.setData(updatedDoctorDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<ApiResponseDto<String>> deleteDoctorProfile(
            @PathVariable Long doctorId
    ){
        doctorService.deleteDoctorProfile(doctorId);
        ApiResponseDto<String> response = new ApiResponseDto<>();
        response.setMessage("Doctor Profile With DoctorId: "+doctorId+" Has Been Deleted Successfully");
        response.setStatus(200);
        response.setData("Profile Deleted!!");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
