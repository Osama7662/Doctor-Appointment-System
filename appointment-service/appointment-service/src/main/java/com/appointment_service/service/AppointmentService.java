package com.appointment_service.service;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;

public interface AppointmentService {

    AppointmentResponseDto bookAppointment(AppointmentRequestDto requestDto);
    AppointmentResponseDto getAppointmentById(Long id);

}
