package com.spring.petcareConnect.services;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ServiceMappingSupport {

    private final ModelMapper modelMapper;

    public ServiceMappingSupport(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <D> D mapToDto(Object source, Class<D> dtoClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, dtoClass);
    }

    public <E> E mapToEntity(Object source, Class<E> entityClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, entityClass);
    }

    public Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        int safePageNumber = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        int safePageSize = pageSize == null || pageSize <= 0 ? 20 : Math.min(pageSize, 100);
        String safeSortBy = StringUtils.hasText(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(safePageNumber, safePageSize, Sort.by(new Sort.Order(direction, safeSortBy)));
    }

    public <T, D> List<D> mapToDtoList(List<T> sourceList, Class<D> dtoClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return List.of();
        }
        return sourceList.stream()
                .map(item -> mapToDto(item, dtoClass))
                .toList();
    }
}
