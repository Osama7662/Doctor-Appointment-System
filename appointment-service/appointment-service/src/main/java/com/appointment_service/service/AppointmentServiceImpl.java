package com.appointment_service.service;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.Dto.PageResponseDto;
import com.appointment_service.entity.Appointment;
import com.appointment_service.enums.AppointmentStatus;
import com.appointment_service.exception.AppointmentCancellationException;
import com.appointment_service.exception.AppointmentNotFoundException;
import com.appointment_service.repository.AppointmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public PageResponseDto<AppointmentResponseDto> getAllAppointments(int pageNo, int pageSize, String sortBy, String sortDir) {
       Sort sort;
       if (sortDir.equalsIgnoreCase("desc")){
           sort = Sort.by(sortBy).ascending();
       } else {
           sort = Sort.by(sortBy).descending();
       }
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<Appointment> allAppointments = appointmentRepository.findAll(pageRequest);
        List<AppointmentResponseDto> appointmentListDto = allAppointments.getContent()
                .stream().map(appointment -> modelMapper.map(
                        appointment,AppointmentResponseDto.class
                )).toList();
        return new PageResponseDto<>(
                appointmentListDto,
                allAppointments.getNumber(),
                allAppointments.getSize(),
                allAppointments.getTotalElements(),
                allAppointments.getTotalPages(),
                allAppointments.isFirst(),
                allAppointments.isLast()
        );
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByPatientId(Long patientId) {
        List<Appointment> appointmentsByPatientId = appointmentRepository.findByPatientId(patientId);

//        if (appointments.isEmpty()){
//            throw new AppointmentNotFoundException(
//                    "No appointments found for Patient ID: " + patientId
//            );
//        }
        return appointmentsByPatientId.stream()
                .map(appointment -> modelMapper.map(
                        appointment,AppointmentResponseDto.class
                )).toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentByDoctorId(Long doctorId) {
        List<Appointment> appointmentsByDoctorid = appointmentRepository.findByDoctorId(doctorId);

        return appointmentsByDoctorid.stream()
                .map(appointment -> modelMapper.map(
                        appointment,AppointmentResponseDto.class
                )).toList();
    }

    @Override
    public AppointmentResponseDto cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
        if (
                appointment.getStatus()==AppointmentStatus.REJECTED ||
                        appointment.getStatus()==AppointmentStatus.CANCELLED
        ){
            throw new AppointmentCancellationException(
                    "This appointment cannot be cancelled. Current status: " +appointment.getStatus()
            );
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        return modelMapper.map(cancelledAppointment,AppointmentResponseDto.class);
    }


}
