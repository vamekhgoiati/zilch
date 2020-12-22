package com.zilch.common.model;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class ApiError {
    HttpStatus httpStatus;
    String message;

}
