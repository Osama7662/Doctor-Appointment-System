package com.appointment_service.service;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.Dto.PageResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentService {

    AppointmentResponseDto bookAppointment(AppointmentRequestDto requestDto);
    AppointmentResponseDto getAppointmentById(Long id);
    PageResponseDto<AppointmentResponseDto> getAllAppointments(
            int pageNo, int pageSize, String sortBy, String sortDir
    );
    List<AppointmentResponseDto> getAppointmentsByPatientId(Long patientId);
    List<AppointmentResponseDto> getAppointmentByDoctorId(Long doctorId);
    AppointmentResponseDto cancelAppointment(Long appointmentId);
}
