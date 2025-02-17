package com.petconnect.backend.exceptions;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Consistent Error Response (Using ApiResponse)
    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, T details, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(message, details), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("MethodArgumentNotValidException: {}", errors, ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Validation Failed", errors); // Use Map directly
        return createErrorResponse("Validation Failed", errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.NOT_FOUND); // Consistent response
    }



    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("AuthenticationException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.UNAUTHORIZED); // Consistent response
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        logger.error("ConstraintViolationException: {}", errorMessage, ex);
        return createErrorResponse("Constraint Violations: " + errorMessage, null, HttpStatus.BAD_REQUEST); // Consistent response
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return createErrorResponse("Access Denied", null, HttpStatus.FORBIDDEN); // Consistent response
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException e) {
        logger.error("User not found: {}", e.getMessage(), e);
        return createErrorResponse("User not found", null, HttpStatus.NOT_FOUND); // Consistent response
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ApiResponse<Object>> handleImageUploadException(ImageUploadException ex) {
        return createErrorResponse(ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Exception: {}", ex.getMessage(), ex);
        return createErrorResponse("An error occurred: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR); // Consistent response
    }
}