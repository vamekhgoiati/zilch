package com.zilch.common.exception.handler;

import com.zilch.common.exception.InsufficientFundsException;
import com.zilch.common.model.ApiError;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Resource not found");
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler({OptimisticLockingFailureException.class})
    public ResponseEntity<ApiError> handleOptimisticLockException(OptimisticLockingFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, "Resource was changed, please retry");
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler({InsufficientFundsException.class})
    public ResponseEntity<ApiError> handleInsufficientMoneyException(InsufficientFundsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}
