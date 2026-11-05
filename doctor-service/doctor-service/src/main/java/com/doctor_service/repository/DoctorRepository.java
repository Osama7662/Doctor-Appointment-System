package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByUserId(Long userId);
    Optional<Doctor> findByUserId(Long userId);
}