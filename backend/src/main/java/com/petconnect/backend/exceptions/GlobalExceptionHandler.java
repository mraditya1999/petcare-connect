package com.petconnect.backend.exceptions;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Creates a consistent error response using ApiResponse.
     *
     * @param message the error message
     * @param details additional error details
     * @param status  the HTTP status
     * @return the ResponseEntity containing the ApiResponse
     */
    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, T details, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(message, details), status);
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("MethodArgumentNotValidException: {}", errors, ex);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Validation Failed", errors);
        return createErrorResponse("Validation Failed", errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserAlreadyExistsException.
     *
     * @param e the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        logger.error("User already exists: ", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(e.getMessage()));
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AuthenticationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("AuthenticationException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        logger.error("ConstraintViolationException: {}", errorMessage, ex);
        return createErrorResponse("Constraint Violations: " + errorMessage, null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles AccessDeniedException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return createErrorResponse("Access Denied", null, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles UserNotFoundException.
     *
     * @param e the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException e) {
        logger.error("User not found: {}", e.getMessage(), e);
        return createErrorResponse("User not found", null, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ImageUploadException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ApiResponse<Object>> handleImageUploadException(ImageUploadException ex) {
        logger.error("ImageUploadException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles PetNotFoundException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handlePetNotFoundException(PetNotFoundException ex) {
        logger.error("PetNotFoundException: {}", ex.getMessage(), ex);
        return createErrorResponse("Pet not found", null, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles RoleNotFoundException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRoleNotFoundException(RoleNotFoundException ex) {
        logger.error("RoleNotFoundException: {}", ex.getMessage(), ex);
        return createErrorResponse("Role not found", null, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles SpecialistNotFoundException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(SpecialistNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSpecialistNotFoundException(SpecialistNotFoundException ex) {
        logger.error("SpecialistNotFoundException: {}", ex.getMessage(), ex);
        return createErrorResponse("Specialist not found", null, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ImageDeletionException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ImageDeletionException.class)
    public ResponseEntity<ApiResponse<Object>> handleImageDeletionException(ImageDeletionException ex) {
        logger.error("ImageDeletionException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles InvalidAddressException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAddressException(InvalidAddressException ex) {
        logger.error("InvalidAddressException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles global Exception.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Exception: {}", ex.getMessage(), ex);
        return createErrorResponse("An error occurred: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
