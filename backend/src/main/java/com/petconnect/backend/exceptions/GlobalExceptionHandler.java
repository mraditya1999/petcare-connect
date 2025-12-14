package com.petconnect.backend.exceptions;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
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
    private <T> ResponseEntity<ApiResponseDTO<T>> createErrorResponse(String message, T details, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponseDTO<>(message, details), status);
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param ex the exception
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
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
    public ResponseEntity<ApiResponseDTO<String>> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        logger.error("User already exists: ", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO<>(e.getMessage()));
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleUserNotFoundException(UserNotFoundException e) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleImageUploadException(ImageUploadException ex) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handlePetNotFoundException(PetNotFoundException ex) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleRoleNotFoundException(RoleNotFoundException ex) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleSpecialistNotFoundException(SpecialistNotFoundException ex) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleImageDeletionException(ImageDeletionException ex) {
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
    public ResponseEntity<ApiResponseDTO<Object>> handleInvalidAddressException(InvalidAddressException ex) {
        logger.error("InvalidAddressException: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ApiException (base for custom API errors).
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleApiException(ApiException ex, WebRequest request) {
        logger.error("ApiException: {}", ex.getMessage(), ex);
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST;
        return createErrorResponse(ex.getMessage(), null, status);
    }

    /**
     * Handles global Exception.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        // Return a generic message to avoid leaking internal details to clients
        return createErrorResponse("An internal server error occurred. Please contact support.", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles DuplicatePetNameException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the ResponseEntity containing the exception message
     */
    @ExceptionHandler(DuplicatePetNameException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleDuplicatePetNameException(DuplicatePetNameException ex, WebRequest request) {
        logger.error("DuplicatePetNameException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(ex.getMessage(), null));
    }

     /**
      * Handles FileValidationException and returns a user-friendly error response.
      *
      * @param ex the FileValidationException instance
      * @return a ResponseEntity containing the error message and HTTP status code
      */
     @ExceptionHandler(FileValidationException.class)
     public ResponseEntity<ApiResponseDTO<String>> handleFileValidationException(FileValidationException ex) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(ex.getMessage(), null));
     }

    /**
     * Handles general ValidationException thrown from custom validators.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleValidationException(ValidationException ex) {
        logger.error("ValidationException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(ex.getMessage(), null));
    }

     /**
      * Handles UnauthorizedAccessException and returns a user-friendly error response.
      *
      * @param ex the UnauthorizedAccessException instance
      * @return a ResponseEntity containing the error message and HTTP status code
      */
     @ExceptionHandler(UnauthorizedAccessException.class)
     public ResponseEntity<ApiResponseDTO<String>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO<>(ex.getMessage(), null));
     }

    /**
     * Handles HttpMessageNotReadableException thrown when the request body is not readable or cannot be deserialized.
     *
     * @param ex the HttpMessageNotReadableException exception
     * @return ResponseEntity containing an ApiResponseDTO with an error message and HTTP status code BAD_REQUEST (400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("message", "Invalid input: " + ex.getMostSpecificCause().getMessage());
        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>("Invalid input", errorData);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles InvalidRoleNameException thrown when an invalid role name is provided.
     *
     * @param ex the InvalidRoleNameException exception
     * @return ResponseEntity containing an ApiResponseDTO with an error message and HTTP status code BAD_REQUEST (400)
     */
    @ExceptionHandler(InvalidRoleNameException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleInvalidRoleNameException(InvalidRoleNameException ex) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("message", ex.getMessage());
        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>("Invalid input", errorData);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleInvalidOtp(InvalidOtpException ex) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("message", ex.getMessage());
        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>("Invalid OTP", errorData);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleOtpExpired(OtpExpiredException ex) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("message", ex.getMessage());
        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>("OTP expired", errorData);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles JwtTokenException and its subclasses.
     */
    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleJwtTokenException(JwtTokenException ex, WebRequest request) {
        logger.error("JWT Token error: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), null, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JwtTokenExpiredException.
     */
    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleJwtTokenExpiredException(JwtTokenExpiredException ex, WebRequest request) {
        logger.warn("JWT Token expired: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), null, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JwtTokenInvalidException.
     */
    @ExceptionHandler(JwtTokenInvalidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleJwtTokenInvalidException(JwtTokenInvalidException ex, WebRequest request) {
        logger.warn("JWT Token invalid: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), null, HttpStatus.UNAUTHORIZED);
    }

     @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, WebRequest request) {
        logger.error("IO Exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
            "FILE_PROCESSING_ERROR",
            "An error occurred while processing the file: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
