
package com.doctor_service.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "doctorId",
        "userId",
        "specialization",
        "qualification",
        "experienceYears",
        "consultationFee",
        "clinicAddress",
        "bio",
        "availableForAppointments"
})
public class DoctorResponseDto {

    private Long doctorId;
    private Long userId;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String clinicAddress;
    private String bio;
    private Boolean availableForAppointments;
}

