package user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import user_service.dto.PendingDoctorResponseDto;
import user_service.dto.UserResponseDto;
import user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/u1/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/doctors/pending")
    public ResponseEntity<UserResponseDto<List<PendingDoctorResponseDto>>> getPendingDoctors(){
        List<PendingDoctorResponseDto> pendingDoctors = userService.getPendingDoctors();
        UserResponseDto<List<PendingDoctorResponseDto>> response = new UserResponseDto<>();
        response.setMessage("All Pending Doctors Fetched Successfully");
        response.setStatus(HttpStatus.OK.value());
        response.setData(pendingDoctors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/doctors/approve/{doctorId}")
    public ResponseEntity<UserResponseDto<PendingDoctorResponseDto>> approveDoctor(
            @PathVariable Long doctorId
    ){
        PendingDoctorResponseDto approvedDoctorDto = userService.approveDoctor(doctorId);
        UserResponseDto<PendingDoctorResponseDto> response = new UserResponseDto<>();
        response.setMessage("Doctor's Application Has Been Approved For DoctorId: "+doctorId);
        response.setStatus(HttpStatus.OK.value());
        response.setData(approvedDoctorDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/doctors/reject/{doctorId}")
    public ResponseEntity<UserResponseDto<PendingDoctorResponseDto>> rejectDoctor(
            @PathVariable Long doctorId
    ){
        PendingDoctorResponseDto rejectedDoctor = userService.rejectDoctor(doctorId);
        UserResponseDto<PendingDoctorResponseDto> response = new UserResponseDto<>();
        response.setMessage("Doctor's Application Has Been Rejected For DoctorId: "+doctorId);
        response.setStatus(HttpStatus.OK.value());
        response.setData(rejectedDoctor);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}



