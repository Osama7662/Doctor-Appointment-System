package com.doctor_service.service;

import com.doctor_service.Dto.DoctorRequestDto;
import com.doctor_service.Dto.DoctorResponseDto;
import com.doctor_service.Dto.PageResponseDto;
import com.doctor_service.Dto.UpdateDoctorRequestDto;
import com.doctor_service.entity.Doctor;
import com.doctor_service.exception.DoctorAccessDeniedException;
import com.doctor_service.exception.DoctorAlreadyExistsException;
import com.doctor_service.exception.DoctorNotFoundException;
import com.doctor_service.exception.InvalidPaginationException;
import com.doctor_service.repository.DoctorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponseDto registerDoctor(DoctorRequestDto requestDto) {
        if (doctorRepository.existsByUserId(requestDto.getUserId())) {
            throw new DoctorAlreadyExistsException(
                    "A doctor profile already exists for this user."
            );
        }
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(requestDto, doctor);

        if (requestDto.getAvailableForAppointments() == null) {
            doctor.setAvailableForAppointments(true);
        }
        Doctor savedDoctor = doctorRepository.save(doctor);
        DoctorResponseDto savedDoctorDto = new DoctorResponseDto();
        BeanUtils.copyProperties(savedDoctor, savedDoctorDto);
        savedDoctorDto.setDoctorId(savedDoctor.getId()); // creating and returning Doctor id as db id
        return savedDoctorDto;
    }

    public DoctorResponseDto getDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor Not_Found with id: " + doctorId));

        DoctorResponseDto foundDoctorDto = new DoctorResponseDto();
        BeanUtils.copyProperties(doctor, foundDoctorDto);
        foundDoctorDto.setDoctorId(doctor.getId());
        return foundDoctorDto;
    }

    public DoctorResponseDto getDoctorByUserId(Long userId) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor Not Found With UserId: " + userId
                ));

        DoctorResponseDto foundDoctorDto = new DoctorResponseDto();

        BeanUtils.copyProperties(doctor, foundDoctorDto);

        foundDoctorDto.setDoctorId(doctor.getId());

        return foundDoctorDto;
    }

    public PageResponseDto<DoctorResponseDto> getAllDoctors(
            int pageNo, int pageSize, String sortBy, String sortDir
    ) {

        if (pageNo < 0) {
            throw new InvalidPaginationException(
                    "Page number cannot be negative");
        }

        if (pageSize <= 0) {
            throw new InvalidPaginationException(
                    "Page size must be greater than zero");
        }

        if (!sortDir.equalsIgnoreCase("asc")
                && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidPaginationException(
                    "Sort direction must be asc or desc");
        }

        if (!Set.of(
                "id",
                "userId",
                "specialization",
                "qualification",
                "experienceYears",
                "consultationFee"
        ).contains(sortBy)) {
            throw new InvalidPaginationException(
                    "Invalid sorting field: " + sortBy);
        }


        Sort sort;
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<Doctor> doctorsPage = doctorRepository.findAll(pageRequest);
        List<DoctorResponseDto> doctorResponseListDto =
                doctorsPage.getContent()
                        .stream()
                        .map(doctor -> new DoctorResponseDto(
                                doctor.getId(),
                                doctor.getUserId(),
                                doctor.getSpecialization(),
                                doctor.getQualification(),
                                doctor.getExperienceYears(),
                                doctor.getConsultationFee(),
                                doctor.getClinicAddress(),
                                doctor.getBio(),
                                doctor.getAvailableForAppointments()
                        ))
                        .toList();
        return new PageResponseDto<>(
                doctorResponseListDto,
                doctorsPage.getNumber(),
                doctorsPage.getSize(),
                doctorsPage.getTotalElements(),
                doctorsPage.getTotalPages(),
                doctorsPage.isFirst(),
                doctorsPage.isLast()
        );
    }

    public DoctorResponseDto updateDoctor(
            Long doctorId,
            UpdateDoctorRequestDto updateDto
    ) {

        // Find the doctor profile
        Doctor updateDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor Not Found With Id: " + doctorId
                ));

        // Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Get authenticated user's ID from JWT principal
        Long authenticatedUserId =
                (Long) authentication.getPrincipal();

        // Check profile ownership
        if (!updateDoctor.getUserId().equals(authenticatedUserId)) {

            throw new DoctorAccessDeniedException(
                    "You are not allowed to update another doctor's profile"
            );
        }

        // Update doctor details
        updateDoctor.setSpecialization(updateDto.getSpecialization());
        updateDoctor.setQualification(updateDto.getQualification());
        updateDoctor.setExperienceYears(updateDto.getExperienceYears());
        updateDoctor.setConsultationFee(updateDto.getConsultationFee());
        updateDoctor.setClinicAddress(updateDto.getClinicAddress());
        updateDoctor.setBio(updateDto.getBio());
        updateDoctor.setAvailableForAppointments(
                updateDto.getAvailableForAppointments()
        );

        // Save updated profile
        Doctor updatedDoctor = doctorRepository.save(updateDoctor);

        // Convert entity to DTO
        DoctorResponseDto updatedDoctorDto = new DoctorResponseDto();

        BeanUtils.copyProperties(updatedDoctor, updatedDoctorDto);

        updatedDoctorDto.setDoctorId(updatedDoctor.getId());

        return updatedDoctorDto;
    }

    public void deleteDoctorProfile(Long doctorId) {

        // 1. Find doctor profile
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor Not Found With Id: " + doctorId
                        ));

        // 2. Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long authenticatedUserId = (Long) authentication.getPrincipal();

        // 3. Check whether authenticated user is ADMIN
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));


        // 4. Allow only profile owner or ADMIN
        if (!isAdmin &&
                !doctor.getUserId().equals(authenticatedUserId)) {

            throw new DoctorAccessDeniedException(
                    "You are not allowed to delete another doctor's profile"
            );
        }

        // 5. Delete doctor profile
        doctorRepository.delete(doctor);
    }
}
