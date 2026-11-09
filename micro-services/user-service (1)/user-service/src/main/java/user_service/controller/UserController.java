package user_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import user_service.dto.*;
import user_service.entity.AppUser;
import user_service.exception.UserAlreadyExistsException;
import user_service.repository.AppUserRepository;
import user_service.service.JwtService;
import user_service.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("api/u1/user")
public class UserController {

    private UserService userService;
    private AppUserRepository userRepository;

    public UserController(UserService userService, AppUserRepository userRepository, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto<UserDataDto>> signUp(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto<UserDataDto> response = new UserResponseDto<>();
        if (userRepository.existsByEmail(requestDto.getEmail())){
            throw new UserAlreadyExistsException("User Already Registered With This Email: "+
                    requestDto.getEmail());
        }

        if (userRepository.existsByMobile(requestDto.getMobile())){
            throw new UserAlreadyExistsException("User Already Registered With This Mobile: "+
                    requestDto.getMobile());
        }

        UserDataDto savedRequestDto = userService.createUser(requestDto);
        response.setMessage("User Registration Successful!!");
        response.setStatus(200);
        response.setData(savedRequestDto);

        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto<LoginDataDto>> loginUser(
            @Valid @RequestBody LoginRequestDto loginDto)
    {

        LoginDataDto loginData = userService.loginUser(loginDto);
        UserResponseDto<LoginDataDto> response = new UserResponseDto<>();
        response.setMessage("Login Successful..");
        response.setStatus(200);
        response.setData(loginData);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/readme")
    public ResponseEntity<UserResponseDto<UserDataDto>> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails
            ){
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"User Not Found!!"
                ));
        UserDataDto dataDto = new UserDataDto();
        BeanUtils.copyProperties(user,dataDto);

        UserResponseDto<UserDataDto> response = new UserResponseDto<>();
        response.setMessage("User Profile Fetched Successfully");
        response.setStatus(200);
        response.setData(dataDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/updateme")
    public ResponseEntity<UserResponseDto<UserDataDto>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequestDto updateDto
    ){
        UserDataDto updatedUserDto = userService.updateUserProfile(
                userDetails.getUsername(),
                updateDto
        );
        UserResponseDto<UserDataDto> response = new UserResponseDto<>();
        response.setMessage("User Profile Updated Successfully");
        response.setStatus(200);
        response.setData(updatedUserDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/deleteme")
    public ResponseEntity<UserResponseDto<String>> deactivateUser(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        userService.deactivateUser(userDetails.getUsername());
        UserResponseDto<String> response = new UserResponseDto<>();
        response.setMessage("User Account Deactivated Successfully!!");
        response.setStatus(200);
        response.setData("Your Account Has Been Deactivated!!");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //---> endpoints for microservices Communication..

    @GetMapping("/validate/{userId}")
    public ResponseEntity<UserResponseDto<UserValidationDto>> validateUser(
            @PathVariable Long userId
    ) {

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User Not Found With ID: " + userId
                ));

        UserValidationDto validationDto = new UserValidationDto();

        validationDto.setId(user.getId());
        validationDto.setRole(user.getRole());
        validationDto.setApprovalStatus(user.getApprovalStatus());
        validationDto.setActive(user.isActive());

        UserResponseDto<UserValidationDto> response =
                new UserResponseDto<>();

        response.setMessage("User Validation Details Fetched Successfully");
        response.setStatus(200);
        response.setData(validationDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
