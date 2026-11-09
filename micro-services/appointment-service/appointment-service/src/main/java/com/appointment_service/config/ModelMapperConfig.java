
package com.appointment_service.config;

import com.appointment_service.Dto.AppointmentRequestDto;
import com.appointment_service.Dto.AppointmentResponseDto;
import com.appointment_service.entity.Appointment;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        // Request DTO to Entity mapping
        modelMapper.addMappings(
                new PropertyMap<AppointmentRequestDto, Appointment>() {

                    @Override
                    protected void configure() {

                        // Skip auto-generated ID
                        skip(destination.getId());

                        map().setPatientId(source.getPatientId());
                        map().setDoctorId(source.getDoctorId());
                        map().setAppointmentDate(source.getAppointmentDate());
                        map().setAppointmentTime(source.getAppointmentTime());
                        map().setReason(source.getReason());
                    }
                }
        );

        // Entity to Response DTO mapping
        modelMapper.addMappings(
                new PropertyMap<Appointment, AppointmentResponseDto>() {

                    @Override
                    protected void configure() {

                        map().setAppointmentId(source.getId());
                    }
                }
        );

        return modelMapper;
    }
}

