package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.utils.PaginationUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Base controller class providing common REST operations.
 * Controllers can extend this class to inherit standard CRUD endpoints.
 */
public abstract class BaseController {

    protected final Logger logger;

    protected BaseController(Logger logger) {
        this.logger = logger;
    }

    /**
     * Get pagination parameters from request.
     */
    protected PaginationUtils.PaginationInfo getPaginationInfo(Page<?> page) {
        return PaginationUtils.getPaginationInfo(page);
    }

    /**
     * Create pageable from request parameters.
     */
    protected org.springframework.data.domain.Pageable createPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {

        org.springframework.data.domain.Sort.Direction direction = null;
        if (sortDir != null) {
            direction = "desc".equalsIgnoreCase(sortDir) ?
                    org.springframework.data.domain.Sort.Direction.DESC :
                    org.springframework.data.domain.Sort.Direction.ASC;
        }

        return PaginationUtils.createPageable(page, size, sortBy, direction);
    }

    /**
     * Handle successful operations with data.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> ok(T data) {
        return ResponseEntityUtil.ok(data);
    }

    /**
     * Handle successful operations with message and data.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntityUtil.ok(message, data);
    }

    /**
     * Handle successful creation.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> created(T data) {
        return ResponseEntityUtil.created(data);
    }

    /**
     * Handle successful creation with message.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> created(String message, T data) {
        return ResponseEntityUtil.created(message, data);
    }

    /**
     * Handle not found responses.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> notFound(String message) {
        return ResponseEntityUtil.notFound(message);
    }

    /**
     * Handle bad request responses.
     */
    protected <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String message) {
        return ResponseEntityUtil.badRequest(message);
    }

    /**
     * Handle paginated responses.
     */
    protected <T> ResponseEntity<ApiResponseDTO<Page<T>>> page(Page<T> page) {
        return ResponseEntityUtil.page(page);
    }

    /**
     * Log and handle errors.
     */
    protected void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * Log info messages.
     */
    protected void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Log debug messages.
     */
    protected void logDebug(String message) {
        logger.debug(message);
    }
}