package com.doctor_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {

    private LocalDateTime localDateTime;
    private String message;
    private String uri;
}

