package user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "mobile", nullable = false, unique = true)
    private String mobile;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "approval_status", nullable = false)
    private String approvalStatus;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

}
