package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.forum.request.ForumCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.ForumUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.ForumListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.ForumResponseDto;
import com.spring.petcareConnect.entities.Forum;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.repositories.mongo.CommentRepository;
import com.spring.petcareConnect.repositories.mongo.ForumRepository;
import com.spring.petcareConnect.repositories.mongo.LikeRepository;
import com.spring.petcareConnect.services.ForumService;
import com.spring.petcareConnect.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {

    private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ForumRepository forumRepository;
    private final ModelMapper modelMapper;

    public ForumServiceImpl(UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository, ForumRepository forumRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.forumRepository = forumRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public ForumListResponseDto getAllForumsOfUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.info("Fetching all pets for user with pagination page={} size={} sortBy={} sortOrder={}",
                pageNumber, pageSize, sortBy, sortOrder);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet list retrieval");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Forum> forumPage = forumRepository.findAllByUserId(user.getUserId(), pageable);
        logger.debug("Found {} forums for user {}", forumPage.getTotalElements(), email);
        return buildResponse(forumPage);
    }

    @Override
    public ForumListResponseDto getAllForums(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Forum> forumPage = forumRepository.findAll(pageable);
        logger.debug("Found {} forums", forumPage.getTotalElements());
        return buildResponse(forumPage);
    }

    @Override
    public ForumResponseDto createForumForUser(ForumCreateRequestDto forumCreateRequestDto) {
        logger.info("Creating forum for user with email lookup...");

        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during forum creation");
            return new APIException("No logged-in user");
        });

        User user = getUserByEmailOrThrow(email);
        Forum forum = getForum(forumCreateRequestDto, user);

        forum = forumRepository.save(forum);
        logger.info("Forum created by user {} with ID: {}", email, forum.getForumId());

        return convertToForumDTO(forum);
    }

    @Override
    public ForumResponseDto updateForumForUser(String forumId, ForumUpdateRequestDto dto) {
        logger.info("Creating forum for user with email lookup...");

        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during forum creation");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Forum forum = forumRepository.findByForumIdAndUserId(forumId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));

        if (dto.getTitle() != null) forum.setTitle(dto.getTitle().trim());
        if (dto.getContent() != null) forum.setContent(dto.getContent().trim());
        if (dto.getCategory() != null) forum.setCategory(dto.getCategory());
        if (dto.getTags() != null) forum.setTags(dto.getTags());
        if (dto.getIsPinned() != null) forum.setIsPinned(dto.getIsPinned());
        if (dto.getPublished() != null) forum.setPublished(dto.getPublished());

        forum.setUpdatedAt(Instant.now());

        forum = forumRepository.save(forum);
        logger.info("Forum updated by user {} with ID: {}", email, forum.getForumId());

        return convertToForumDTO(forum);
    }

    @Override
    public ForumResponseDto getForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));
        return convertToForumDTO(forum);
    }

    @Override
    public void deleteForumForUser(String forumId) {
        logger.info("Creating forum for user with email lookup...");

        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during forum creation");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Forum forum = forumRepository.findByForumIdAndUserId(forumId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));

        commentRepository.deleteAllByForumId(forumId);
        likeRepository.deleteAllByForumId(forumId);
        forumRepository.delete(forum);
        logger.info("Successfully deleted forum with id {} and its related comments/likes", forum.getForumId());
    }

    @Override
    public ForumListResponseDto searchForums(String keyword, List<String> tags, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Forum> forumPage;

        if (keyword != null && !keyword.isBlank() && tags != null && !tags.isEmpty()) {
            // Case: keyword + tags
            forumPage = forumRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndTagsIn(
                            keyword.trim(), keyword.trim(), tags, pageable);

        } else if (keyword != null && !keyword.isBlank()) {
            // Case: keyword only
            forumPage = forumRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                            keyword.trim(), keyword.trim(), pageable);

        } else if (tags != null && !tags.isEmpty()) {
            // Case: tags only
            forumPage = forumRepository.findByTagsIn(tags, pageable);

        } else {
            // Fallback: all forums
            forumPage = forumRepository.findAll(pageable);
        }

        return buildResponse(forumPage);
    }

    @Override
    public ForumListResponseDto getTopFeaturedForums(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt")));

        Page<Forum> forumPage = forumRepository.findByIsFeaturedTrue(pageable);
        logger.debug("Found {} featured forums", forumPage.getTotalElements());
        return buildResponse(forumPage);
    }


    private static Forum getForum(ForumCreateRequestDto forumCreateRequestDto, User user) {
        Forum forum = new Forum();
        forum.setUserId(user.getUserId());
        forum.setTitle(forumCreateRequestDto.getTitle().trim());
        forum.setContent(forumCreateRequestDto.getContent().trim());

        forum.setCategory(forumCreateRequestDto.getCategory());

        if (forumCreateRequestDto.getTags() != null) {
            forum.setTags(forumCreateRequestDto.getTags());
        } else {
            forum.setTags(new HashSet<>());
        }

        forum.setPublished(true);
        forum.setIsFeatured(false);
        forum.setIsPinned(false);
        forum.setIsLocked(false);
        forum.setIsDeleted(false);
        forum.setViewCount(0L);
        forum.setLikeCount(0);
        forum.setCommentCount(0);
        return forum;
    }

    public ForumResponseDto convertToForumDTO(Forum forum) {
        ForumResponseDto forumDTO = modelMapper.map(forum, ForumResponseDto.class);

        User user = userRepository.findById(forum.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", forum.getUserId()));

        forumDTO.setFirstName(user.getFirstName());
        forumDTO.setLastName(user.getLastName());
        forumDTO.setEmail(user.getEmail());

        forumDTO.setLikeCount(forum.getLikeCount());
        forumDTO.setCommentCount(forum.getCommentCount());

        forumDTO.setLikedByCurrentUser(forum.isLikedByUser(user.getUserId()));

        return forumDTO;
    }

    private ForumListResponseDto buildResponse(Page<Forum> forumPage) {
        List<ForumResponseDto> forums = forumPage.getContent().stream().map(this::convertToForumDTO).toList();
        return new ForumListResponseDto(forums, forumPage.getNumber(), forumPage.getSize(), forumPage.getTotalElements(), forumPage.getTotalPages(), forumPage.isLast());
    }

    private Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }
}
