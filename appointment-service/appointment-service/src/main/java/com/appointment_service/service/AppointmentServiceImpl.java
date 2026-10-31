package com.appointment_service.service;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.entity.Appointment;
import com.appointment_service.enums.AppointmentStatus;
import com.appointment_service.exception.AppointmentNotFoundException;
import com.appointment_service.repository.AppointmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, ModelMapper modelMapper) {
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto requestDto) {

        Appointment appointment = modelMapper.map(requestDto, Appointment.class);

        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment,AppointmentResponseDto.class);
    }

    @Override
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment Not Found with Appointment Id: "+id
                        ));
        return modelMapper.map(appointment,AppointmentResponseDto.class);
    }
}
