package com.appointment_service.service;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.Dto.PageResponseDto;
import java.util.List;

public interface AppointmentService {

    AppointmentResponseDto bookAppointment(
            AppointmentRequestDto requestDto,
            Long authenticatedUserId
    );    AppointmentResponseDto getAppointmentById(Long id);
    PageResponseDto<AppointmentResponseDto> getAllAppointments(
            int pageNo, int pageSize, String sortBy, String sortDir
    );
    List<AppointmentResponseDto> getAppointmentsByPatientId(
            Long patientId,
            Long authenticatedUserId
    );    List<AppointmentResponseDto> getAppointmentByDoctorId(Long doctorId, Long authenticatedUserId, boolean isAdmin);
    AppointmentResponseDto cancelAppointment(
            Long appointmentId,
            Long authenticatedUserId
    );
    List<AppointmentResponseDto> getPendingAppointments();
    AppointmentResponseDto confirmAppointment(Long appointmentId);
    AppointmentResponseDto rejectAppointment(Long appointmentId);
}
