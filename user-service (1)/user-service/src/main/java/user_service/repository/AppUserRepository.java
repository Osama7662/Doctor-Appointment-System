package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_service.entity.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByMobile(String mobile);
    List<AppUser> findByRoleAndApprovalStatus(
            String role,
            String approvalStatus
    );
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
}