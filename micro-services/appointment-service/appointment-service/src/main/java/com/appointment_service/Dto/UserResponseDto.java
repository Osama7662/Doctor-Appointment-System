package com.appointment_service.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto<T> {

    private String message;

    private int status;

    private T data;
}
