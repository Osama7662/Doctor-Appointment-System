package com.appointment_service.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorResponseDto {

    private Long doctorId;
    private Long userId;

}
