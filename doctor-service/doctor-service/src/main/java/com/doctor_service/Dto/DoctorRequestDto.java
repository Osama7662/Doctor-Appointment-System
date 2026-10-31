package com.doctor_service.Dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DoctorRequestDto {

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Long userId;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(
            value = "0.0",
            message = "Consultation fee cannot be negative"
    )
    private BigDecimal consultationFee;

    @NotBlank(message = "Clinic address is required")
    @Size(
            max = 1000,
            message = "Clinic address cannot exceed 1000 characters"
    )
    private String clinicAddress;

    @Size(
            max = 1000,
            message = "Bio cannot exceed 1000 characters"
    )
    private String bio;

    private Boolean availableForAppointments;
}

