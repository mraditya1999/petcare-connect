package com.petconnect.backend.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for pagination operations.
 * Provides common pagination patterns and validation.
 */
public final class PaginationUtils {

    private PaginationUtils() {
        // Utility class
    }

    /**
     * Default page size.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Maximum allowed page size.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Create Pageable with validation.
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size, null, null);
    }

    /**
     * Create Pageable with sorting.
     */
    public static Pageable createPageable(int page, int size, String sortBy, Sort.Direction direction) {
        ValidationUtils.requireTrue(page >= 0, "Page number must be non-negative");
        ValidationUtils.requireTrue(size > 0, "Page size must be positive");
        ValidationUtils.requireTrue(size <= MAX_PAGE_SIZE, "Page size cannot exceed " + MAX_PAGE_SIZE);

        PageRequest pageRequest = PageRequest.of(page, size);

        if (sortBy != null && !sortBy.isBlank()) {
            Sort sort = direction != null ? Sort.by(direction, sortBy) : Sort.by(sortBy);
            pageRequest = pageRequest.withSort(sort);
        }

        return pageRequest;
    }

    /**
     * Create Pageable with default size.
     */
    public static Pageable createPageable(int page) {
        return createPageable(page, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get pagination info from Page.
     */
    public static <T> PaginationInfo getPaginationInfo(Page<T> page) {
        return new PaginationInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    /**
     * Pagination information record.
     */
    public record PaginationInfo(
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious,
            boolean isFirst,
            boolean isLast
    ) {}
}