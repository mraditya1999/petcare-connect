package com.spring.petcareConnect.exceptions;

import com.spring.petcareConnect.dtos.CustomApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(new CustomApiResponse<>(false, errorMessage, null));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<String>> handleJsonParseError(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        String message = "Invalid request payload: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.badRequest()
                .body(new CustomApiResponse<>(false, message, null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomApiResponse<String>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CustomApiResponse<String>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity.badRequest()
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<CustomApiResponse<String>> handleAPIException(APIException ex) {
        return ResponseEntity.badRequest()
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<CustomApiResponse<String>> handleEmailSendException(EmailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomApiResponse<String>> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest()
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<CustomApiResponse<String>> handleOtpException(OtpException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<CustomApiResponse<String>> handleTokenException(TokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<CustomApiResponse<String>> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomApiResponse<>(false, "File processing error: " + ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<String>> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // log for debugging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomApiResponse<>(false, "An unexpected error occurred", null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomApiResponse<String>> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomApiResponse<String>> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<CustomApiResponse<String>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CustomApiResponse<>(false, "Access denied: " + ex.getMessage(), null));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<CustomApiResponse<String>> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new CustomApiResponse<>(false, "Database integrity violation: " + ex.getMostSpecificCause().getMessage(), null));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<CustomApiResponse<String>> handleIllegalArgumentOrState(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomApiResponse<>(false, ex.getMessage(), null));
    }

}