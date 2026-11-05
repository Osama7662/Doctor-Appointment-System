package com.appointment_service.service;

import com.appointment_service.Dto.*;
import com.appointment_service.client.DoctorClient;
import com.appointment_service.entity.Appointment;
import com.appointment_service.enums.AppointmentStatus;
import com.appointment_service.exception.AppointmentCancellationException;
import com.appointment_service.exception.AppointmentNotFoundException;
import com.appointment_service.repository.AppointmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final DoctorClient doctorClient;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    public AppointmentServiceImpl(DoctorClient doctorClient, AppointmentRepository appointmentRepository, ModelMapper modelMapper) {
        this.doctorClient = doctorClient;
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto requestDto,Long authenticatedUserId) {

        if (!requestDto.getPatientId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to book an appointment for another patient"
            );
        }
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
    public List<AppointmentResponseDto> getAppointmentsByPatientId(Long patientId, Long authenticatedUserId) {
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
    public List<AppointmentResponseDto> getAppointmentByDoctorId(
            Long doctorId,
            Long authenticatedUserId,
            boolean isAdmin
    ) {

        // Step 1: Validate doctor ownership
        // Admin can access any doctor's appointments
        if (!isAdmin) {

            // Step 2: Fetch doctor using authenticated user ID
            String authorization =
                    (String) SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getCredentials();

            ApiResponseDto<DoctorResponseDto> doctorResponse =
                    doctorClient.getDoctorByUserId(
                            authenticatedUserId,
                            authorization
                    );

            // Step 3: Check whether doctor data exists
            if (doctorResponse == null || doctorResponse.getData() == null) {
                throw new AccessDeniedException(
                        "Authenticated user is not a registered doctor"
                );
            }

            // Step 4: Extract doctor details
            DoctorResponseDto doctorData = doctorResponse.getData();

            // Step 5: Check ownership
            if (!doctorData.getDoctorId().equals(doctorId)) {
                throw new AccessDeniedException(
                        "You are not authorized to view appointments for this doctor"
                );
            }
        }

        // Step 6: Fetch appointments for the requested doctor
        List<Appointment> appointments =
                appointmentRepository.findByDoctorId(doctorId);

        // Step 7: Convert Entity into DTO and return
        return appointments.stream()
                .map(appointment ->
                        modelMapper.map(
                                appointment,
                                AppointmentResponseDto.class
                        ))
                .toList();
    }


    @Override
    public AppointmentResponseDto cancelAppointment(Long appointmentId, Long authenticatedUserId)
    {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
        if (!appointment.getPatientId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to cancel another patient's appointment"
            );
        }

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

    @Override
    public List<AppointmentResponseDto> getPendingAppointments() {
        List<Appointment> pendingAppointments =
                appointmentRepository.findByStatus(AppointmentStatus.PENDING);

        return pendingAppointments.stream()
                .map(appointment -> modelMapper.map(
                        appointment,AppointmentResponseDto.class
                )).toList();
    }

    @Override
    public AppointmentResponseDto confirmAppointment(Long appointmentId) {
        // Step 1: Find appointment by ID
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
        // Step 2: Check appointment status
        if (appointment.getStatus() != AppointmentStatus.PENDING){
            throw new AppointmentCancellationException(
                    "Only pending appointments can be confirmed. Current status: "
                            + appointment.getStatus()
            );
        }
        // Step 3: Change status
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        // Step 4: Save updated appointment
        Appointment appointmentConfirmed = appointmentRepository.save(appointment);
        // Step 5: Convert Entity into DTO
        return modelMapper.map(appointmentConfirmed,AppointmentResponseDto.class);
    }

    @Override
    public AppointmentResponseDto rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
        if (appointment.getStatus() != AppointmentStatus.PENDING){
            throw new AppointmentCancellationException(
                    "Only pending appointments can be Rejected. Current status: "
                            + appointment.getStatus()
            );
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        Appointment rejectedAppointment = appointmentRepository.save(appointment);
        return modelMapper.map(rejectedAppointment,AppointmentResponseDto.class);
    }


}
