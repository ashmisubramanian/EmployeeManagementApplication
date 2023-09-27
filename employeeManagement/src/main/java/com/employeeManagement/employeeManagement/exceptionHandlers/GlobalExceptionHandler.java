package com.employeeManagement.employeeManagement.exceptionHandlers;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        // Create a map to store field names and their corresponding error messages
        Map<String, String> fieldErrors = new HashMap<>();

        // Extract and map error messages for specific fields
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Construct a custom error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Validation error");
        errorResponse.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMessage();

        // Construct a custom error response for HttpMessageNotReadableException
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Request body format error");
        errorResponse.put("error", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resourseNotFoundException(ResourceNotFoundException ex) {
        String errorMessage = ex.getMessage();

        // Construct a custom error response for HttpMessageNotReadableException
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Request body format error");
        errorResponse.put("error", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExceptionUtils.EmployeeIsAssignedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleEmployeeIsAssignedException(ExceptionUtils.EmployeeIsAssignedException ex) {
        String errorMessage = ex.getMessage();

        // Construct a custom error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Employee assignment error");
        errorResponse.put("error", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


