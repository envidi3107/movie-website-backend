package com.example.MovieWebsiteProject.Exception;

import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Enum.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = RuntimeException.class)
  ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(9001);
    apiResponse.setMessage(e.getMessage());
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> handleAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(4001);
    apiResponse.setMessage("Validation failed: " + String.join(", ", errors));
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(4002);
    apiResponse.setMessage("Validation failed: " + ex.getMessage());
    return ResponseEntity.badRequest().body(apiResponse);
  }
}
