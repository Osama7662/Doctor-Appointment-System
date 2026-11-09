package com.appointment_service.Dto;

import com.appointment_service.enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentResponseDto {

    private Long appointmentId;

    private Long patientId;

    private Long doctorId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String reason;

    private AppointmentStatus status;

    private LocalDateTime createdAt;
}
