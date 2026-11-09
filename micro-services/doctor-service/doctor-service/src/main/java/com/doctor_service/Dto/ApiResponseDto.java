package com.doctor_service.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDto<T> {

    private String message;
    private int status;
    private T data;
}

