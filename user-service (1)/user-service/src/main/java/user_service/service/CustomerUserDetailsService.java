package user_service.service;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import user_service.entity.AppUser;
import user_service.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomerUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not found With Email: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String role = user.getRole();
        if ("PATIENT".equalsIgnoreCase(role)){
            authorities.add(new SimpleGrantedAuthority("ROLE_PATIENT"));
        } else if ("DOCTOR".equalsIgnoreCase(role)
                && "APPROVED".equalsIgnoreCase(user.getApprovalStatus()))
        {
            authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
        } else if ("ADMIN".equalsIgnoreCase(role)){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),  // enabled: true or false
                true,             // accountNonExpired
                true,             // credentialsNonExpired
                true,             // accountNonLocked
                authorities       //Roles collection
        );

    }

    public AppUser loadUserByEmailOrMobile(String identifier){
        return userRepository.findByEmail(identifier)
                .or(()->userRepository.findByMobile(identifier))
                .orElseThrow(()->new UsernameNotFoundException("User Not Found: "+identifier));
    }
}
