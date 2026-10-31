package user_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user_service.dto.*;
import user_service.entity.AppUser;
import user_service.exception.InvalidCredentialsException;
import user_service.exception.UserAlreadyExistsException;
import user_service.repository.AppUserRepository;

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
        if (role.equalsIgnoreCase("Doctor")){
            user.setApprovalStatus("Pending");
        }else {
            user.setApprovalStatus("Approved");
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
}
