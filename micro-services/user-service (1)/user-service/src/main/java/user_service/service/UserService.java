package user_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user_service.dto.*;
import user_service.entity.AppUser;
import user_service.exception.InvalidCredentialsException;
import user_service.exception.UserAlreadyExistsException;
import user_service.repository.AppUserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private AppUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserDataDto createUser(UserRequestDto requestDto) {

        String requestedRole = requestDto.getRole();
        if (requestedRole == null ||
                (!requestedRole.equalsIgnoreCase("PATIENT")) &&
                        (!requestedRole.equalsIgnoreCase("DOCTOR"))
        ){
            throw new InvalidCredentialsException(
                    "Role Must Be PATIENT or DOCTOR!!"
            );
        }
        AppUser user = new AppUser();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setMobile(requestDto.getMobile());
        String role = requestedRole.toUpperCase();
        user.setRole(role);
        if (role.equalsIgnoreCase("DOCTOR")){
            user.setApprovalStatus("PENDING");
        }else {
            user.setApprovalStatus("APPROVED");
        }

        AppUser saved = userRepository.save(user);
        UserDataDto savedRequestDto = new UserDataDto();
        BeanUtils.copyProperties(saved,savedRequestDto);
        return savedRequestDto;
    }

    public LoginDataDto loginUser(LoginRequestDto loginDto){
        String loginId = loginDto.getLoginId().trim();
        AppUser user;
        if (loginId.contains("@")){
            user = userRepository.findByEmail(loginDto.getLoginId()).
                    orElseThrow(() -> new InvalidCredentialsException("Invalid email/mobile or Password"));
        } else {
            user = userRepository.findByMobile(loginDto.getLoginId()).
                    orElseThrow(()-> new InvalidCredentialsException("Invalid email/mobile or password"));
        }
        if (!user.isActive()) {
            throw new InvalidCredentialsException("Your account is deactivated");
        }

        if (!passwordEncoder.matches(  //--> agr if condition true hoga to logic run krega to password
                loginDto.getPassword(), //-> match krega to true hoga iseliye isko Explicitely !true kiya gya h
                user.getPassword()       //--> taki match krne ke baad !ture=false ho jaye to logic run na kre
                //-->agr match nhi kega to !false=true hoga to logic=Invalid Password run hoga.
        )){
            throw new InvalidCredentialsException("Invalid Password!!");
        }
        UserDataDto loginResponseDto = new UserDataDto();
        BeanUtils.copyProperties(user,loginResponseDto);

        String Token = jwtService.generateToken(  //--> these email,mobile,role arguments will be passed to generateToken Method in JwtService.
                user.getId(),
                user.getEmail(),
                user.getMobile(),
                user.getRole()
        );
        LoginDataDto loginData = new LoginDataDto();
        loginData.setToken(Token);
        loginData.setUserDataDto(loginResponseDto);
        return loginData;
    }

    public UserDataDto updateUserProfile(
            String currentEmail,
            UpdateUserRequestDto updateDto
    ){
        AppUser user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User Not Found!!"));

        String newEmail = updateDto.getEmail().trim();

        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)
        ){
            throw new UserAlreadyExistsException("Email Is Already Registered: "+newEmail);
        }
        String newMobile = updateDto.getMobile().trim();

        if (!user.getMobile().equals(newMobile)
            && userRepository.existsByMobile(newMobile)
        ){
            throw new UserAlreadyExistsException("Mobile Number Is Already Registered: "+newMobile);
        }

        user.setName(updateDto.getName().trim());
        user.setEmail(newEmail);
        user.setMobile(newMobile);

        AppUser updatedUser = userRepository.save(user);

        UserDataDto updatedUserDto = new UserDataDto();
        BeanUtils.copyProperties(updatedUser,updatedUserDto);
        return updatedUserDto;
    }

    public void deactivateUser(String currentEmail) {
        AppUser user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User Not Found!!"));

        if (!user.isActive()){
            throw new InvalidCredentialsException("Account is Already Deactivated!!");
        }
        user.setActive(false);
        userRepository.save(user);
    }


    public List<PendingDoctorResponseDto> getPendingDoctors() {

        List<AppUser> pendingDoctors =
                userRepository.findByRoleAndApprovalStatus(
                        "DOCTOR",
                        "PENDING"
                );

        return pendingDoctors.stream()
                .map(user -> {
                    PendingDoctorResponseDto dto =
                            new PendingDoctorResponseDto();

                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setMobile(user.getMobile());
                    dto.setRole(user.getRole());
                    dto.setApprovalStatus(user.getApprovalStatus());

                    return dto;
                })
                .toList();
    }

    public PendingDoctorResponseDto approveDoctor(Long doctorId){
        AppUser doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Doctor Not Found!!"
                ));
        if (!doctor.getRole().equalsIgnoreCase("DOCTOR")){
            throw new InvalidCredentialsException(
                    "User Is Not A Doctor!!"
            );
        }
        if (!doctor.getApprovalStatus().equalsIgnoreCase("PENDING")){
            throw new InvalidCredentialsException(
                    "Nothing Is Pending For Approval!!"
            );
        }

        doctor.setApprovalStatus("APPROVED");

        AppUser savedDoctor = userRepository.save(doctor);

        PendingDoctorResponseDto approvedDoctorDto = new PendingDoctorResponseDto();

        BeanUtils.copyProperties(savedDoctor,approvedDoctorDto);
        return approvedDoctorDto;
    }

    public PendingDoctorResponseDto rejectDoctor(Long doctorId){
        AppUser doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Doctor Not Found!!"
                ));
        if (!doctor.getRole().equalsIgnoreCase("DOCTOR")){
            throw new InvalidCredentialsException(
                    "User Is Not A Doctor"
            );
        }
        if (!doctor.getApprovalStatus().equalsIgnoreCase("PENDING")){
            throw new InvalidCredentialsException(
                    "Nothing Is Pending To Reject!!"
            );
        }
        doctor.setApprovalStatus("REJECTED");
        AppUser savedDoctor = userRepository.save(doctor);

        PendingDoctorResponseDto rejectedDoctorDto = new PendingDoctorResponseDto();
        BeanUtils.copyProperties(savedDoctor,rejectedDoctorDto);
        return rejectedDoctorDto;
    }

    public UserValidationDto deactivateUserById(Long userId){

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new InvalidCredentialsException("User Not Found!!"));

        if (!user.getRole().equalsIgnoreCase("DOCTOR")
                && !user.getRole().equalsIgnoreCase("PATIENT"))
        {
            throw new InvalidCredentialsException( "Only Patient and Doctor Accounts Can Be Deactivated!!");
        }
        if (!user.isActive()){
            throw new InvalidCredentialsException("This User's Account Is Already Deactivated!!");
        }
        user.setActive(false);
        AppUser savedUser = userRepository.save(user);

        UserValidationDto userDto = new UserValidationDto();
        BeanUtils.copyProperties(savedUser,userDto);
        return userDto;
    }

    public UserValidationDto activateUserById(Long userId){

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new InvalidCredentialsException("User Not Found!!")
                );
        if (!user.getRole().equalsIgnoreCase("DOCTOR")
                  && !user.getRole().equalsIgnoreCase("PATIENT"))
        {
            throw new InvalidCredentialsException("User's Account Is Not A Doctor's Account");
        }
        if (user.isActive()){
            throw new InvalidCredentialsException("This User's Account Is Already Activated!!");
        }
        user.setActive(true);
        AppUser savedUser = userRepository.save(user);

        UserValidationDto userValidationDto = new UserValidationDto();
        BeanUtils.copyProperties(savedUser,userValidationDto);
        return userValidationDto;
    }
}
