package com.petconnect.backend.services;

import com.petconnect.backend.dto.forum.ForumCreateDTO;
import com.petconnect.backend.dto.forum.ForumDTO;
import com.petconnect.backend.dto.forum.UpdateForumDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.ForumMapper;
import com.petconnect.backend.mappers.LikeMapper;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private static final Logger logger = LoggerFactory.getLogger(ForumService.class);
    private static final String USER_NOT_FOUND = "User not found with email ";
    private static final String FORUM_NOT_FOUND = "Forum not found with id ";
    private static final String COMMENT_NOT_FOUND = "Comment not found with id ";

    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ForumMapper forumMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final LikeService likeService;
    private final CommentService commentService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ForumService(ForumRepository forumRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, ForumMapper forumMapper, LikeMapper likeMapper, CommentMapper commentMapper, LikeService likeService, CommentService commentService, MongoTemplate mongoTemplate) {
        if (forumRepository == null) {
            throw new IllegalArgumentException("ForumRepository cannot be null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        if (likeRepository == null) {
            throw new IllegalArgumentException("LikeRepository cannot be null");
        }
        if (commentRepository == null) {
            throw new IllegalArgumentException("CommentRepository cannot be null");
        }
        if (forumMapper == null) {
            throw new IllegalArgumentException("ForumMapper cannot be null");
        }
        if (likeService == null) {
            throw new IllegalArgumentException("LikeService cannot be null");
        }
        if (commentService == null) {
            throw new IllegalArgumentException("CommentService cannot be null");
        }
        if (mongoTemplate == null) {
            throw new IllegalArgumentException("MongoTemplate cannot be null");
        }
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.forumMapper = forumMapper;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.likeService = likeService;
        this.commentService = commentService;
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> getAllForums(Pageable pageable) {
        Page<Forum> forums = forumRepository.findAll(pageable);
        return forums.map(this::convertToForumDTO);
    }

    @Transactional(readOnly = true)
    public ForumDTO getForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        return convertToForumDTO(forum);
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> getTopFeaturedForums() {
        List<Forum> forums = forumRepository.findTop3ByLikes();
        return forums.stream()
                .map(this::convertToForumDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> searchForums(String keyword,Pageable pageable) {
        Page<Forum> forums = forumRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword,pageable);
        return forums.map(this::convertToForumDTO);
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> searchForumsByTags(List<String> tags, Pageable pageable) {
        // Normalize incoming tags to lowercase
        List<String> normalizedTags = tags.stream()
                .filter(Objects::nonNull)
                .map(tag -> tag.toLowerCase(Locale.ROOT))
                .toList();

        // Use exact match with $in instead of regex
        Query query = new Query(Criteria.where("tags").in(normalizedTags));

        long count = mongoTemplate.count(query, Forum.class);
        List<Forum> forums = mongoTemplate.find(query.with(pageable), Forum.class);

        return PageableExecutionUtils.getPage(forums, pageable, () -> count)
                .map(this::convertToForumDTO);
    }


    @Transactional(readOnly = true)
    public List<ForumDTO> sortForums(String sortBy, String sortDir) {
        // Default to descending if sortDir is not provided
        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Validate the field to avoid SQL injection / invalid fields
        String field;
        switch (sortBy.toLowerCase()) {
            case "likes":
                field = "likesCount";
                break;
            case "comments":
                field = "commentsCount";
                break;
            case "createdat":
            default:
                field = "createdAt";
                break;
        }

        List<Forum> forums = forumRepository.findAll(Sort.by(direction, field));
        return forums.stream()
                .map(this::convertToForumDTO)
                .collect(Collectors.toList());
    }


    /**
     * Creates a new forum post.
     *
     * @param email the user's email (must not be null or blank)
     * @param forumCreateDTO the forum creation data (must not be null)
     * @return the created forum DTO
     * @throws IllegalArgumentException if email is null/blank or forumCreateDTO is null/invalid
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public ForumDTO createForum(String email, ForumCreateDTO forumCreateDTO) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (forumCreateDTO == null) {
            throw new IllegalArgumentException("ForumCreateDTO cannot be null");
        }
        if (forumCreateDTO.getTitle() == null || forumCreateDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Forum title cannot be null or blank");
        }
        if (forumCreateDTO.getContent() == null || forumCreateDTO.getContent().isBlank()) {
            throw new IllegalArgumentException("Forum content cannot be null or blank");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            Forum forum = new Forum();
            forum.setUserId(user.getUserId());
            forum.setTitle(forumCreateDTO.getTitle().trim());
            forum.setContent(forumCreateDTO.getContent().trim());
            if (forumCreateDTO.getTags() != null) {
                forum.setTags(forumCreateDTO.getTags());
            } else {
                forum.setTags(new ArrayList<>());
            }

            forum = forumRepository.save(forum);
            logger.info("Forum created by user {} with ID: {}", email, forum.getForumId());
            return convertToForumDTO(forum);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating forum for user {}", email, e);
            throw new RuntimeException("Failed to create forum", e);
        }
    }

    @Transactional
    public ForumDTO updateForum(String email, String forumId, UpdateForumDTO updateForumDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        if (!forum.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can only update your own forums.");
        }

        if (updateForumDTO.getTitle() != null) {
            forum.setTitle(updateForumDTO.getTitle());
        }
        if (updateForumDTO.getContent() != null) {
            forum.setContent(updateForumDTO.getContent());
        }
        if (updateForumDTO.getTags() != null) {
            forum.setTags(updateForumDTO.getTags());
        }

        forum = forumRepository.save(forum);
        return convertToForumDTO(forum);
    }

    /**
     * Deletes a forum post if the user is the owner.
     *
     * @param email the user's email (must not be null or blank)
     * @param forumId the forum ID (must not be null or blank)
     * @throws IllegalArgumentException if email/forumId is null/blank or user is not the owner
     * @throws ResourceNotFoundException if user or forum is not found
     */
    @Transactional
    public void deleteForum(String email, String forumId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            Forum forum = forumRepository.findById(forumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

            if (!forum.getUserId().equals(user.getUserId())) {
                logger.warn("User {} attempted to delete forum {} owned by user {}", email, forumId, forum.getUserId());
                throw new IllegalArgumentException("You can only delete your own forums.");
            }

            // Delete all associated likes
            likeRepository.deleteByForumId(forumId);

            // Delete all associated comments and their sub-comments
            deleteCommentsByForumId(forumId);

            // Delete the forum itself
            forumRepository.delete(forum);
            logger.info("Forum {} deleted by user {}", forumId, email);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting forum {} for user {}", forumId, email, e);
            throw new RuntimeException("Failed to delete forum", e);
        }
    }

    @Transactional
    public void deleteCommentsByForumId(String forumId) {
        List<Comment> comments = commentRepository.findByForumId(forumId);
        for (Comment comment : comments) {
            deleteCommentAndSubComments(comment);
        }
    }

    public Page<Comment> getCommentsByForumId(String forumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByForumId(forumId, pageable);
    }


    @Transactional
    public void deleteCommentAndSubComments(Comment comment) {
        List<Comment> subComments = commentRepository.findByParentComment(comment);
        for (Comment subComment : subComments) {
            deleteCommentAndSubComments(subComment);
        }
        likeRepository.deleteByCommentId(comment.getCommentId());
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> getMyForums(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Pageable pageable = PageRequest.of(page, size);
        Page<Forum> forums = forumRepository.findByUserId(user.getUserId(), pageable);
        return forums.map(this::convertToForumDTO);
    }


    public ForumDTO convertToForumDTO(Forum forum) {
        ForumDTO forumDTO = forumMapper.toDTO(forum);
        User user = userRepository.findById(forum.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));
        forumDTO.setFirstName(user.getFirstName());
        forumDTO.setLastName(user.getLastName());
        forumDTO.setEmail(user.getEmail());
        forumDTO.setLikesCount(likeService.getLikesCountForForum(forum.getForumId()));
        forumDTO.setCommentsCount(commentService.getCommentsCountByForumId(forum.getForumId()));
        return forumDTO;
    }

//
//    @Transactional
//    public void deleteComment(String forumId, String commentId, String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//
//        forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
//
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
//
//        if (!comment.getUserId().equals(user.getUserId())) {
//            throw new IllegalArgumentException("You can only delete your own comments.");
//        }
//
//        commentRepository.delete(comment);
//    }


//    ADMIN SERVICES

    @Transactional
    public ForumDTO updateForumById(String forumId, UpdateForumDTO updateForumDTO) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));

        if (updateForumDTO.getTitle() != null) {
            forum.setTitle(updateForumDTO.getTitle());
        }
        if (updateForumDTO.getContent() != null) {
            forum.setContent(updateForumDTO.getContent());
        }
        if (updateForumDTO.getTags() != null) {
            forum.setTags(updateForumDTO.getTags());
        }

        forum = forumRepository.save(forum);
        return convertToForumDTO(forum);
    }

    @Transactional
    public void deleteForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));
        // Delete all associated likes and comments
        likeRepository.deleteByForumId(forumId);
        commentRepository.deleteByForumId(forumId);
        forumRepository.delete(forum);
    }

//    @Transactional
//    public Map<String, String> toggleLikeOnForum(String forumId, String email) {
//        User user = getUserByEmail(email);
//        Forum forum = getForum(forumId);
//
//        Long userId = user.getUserId();
//        Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);
//
//        Map<String, String> response = new HashMap<>();
//        if (existingLike.isPresent()) {
//            likeRepository.delete(existingLike.get());
//            response.put("message", "Forum unliked successfully");
//        } else {
//            Like like = new Like();
//            like.setForumId(forumId);
//            like.setUserId(userId);
//            likeRepository.save(like);
//            response.put("message", "Forum liked successfully");
//        }
//
//        return response;
//    }
//
//    private Forum getForum(String forumId) {
//        return forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));
//    }
//
//    private Comment getComment(String commentId) {
//        return commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + commentId));
//    }

//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + email));
//    }

//    private void updateForumDetails(Forum forum, UpdateForumDTO updateForumDTO) {
//        if (updateForumDTO.getTitle() != null) {
//            forum.setTitle(updateForumDTO.getTitle());
//        }
//        if (updateForumDTO.getContent() != null) {
//            forum.setContent(updateForumDTO.getContent());
//        }
//        if (updateForumDTO.getTags() != null) {
//            forum.setTags(updateForumDTO.getTags());
//        }
//    }

//    private void deleteAssociatedLikesAndComments(String forumId) {
//        likeRepository.deleteByForumId(forumId);
//        commentRepository.deleteByForumId(forumId);
//    }


//    @Transactional
//    public void deleteAnyComment(String commentId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
//        commentRepository.delete(comment);
//    }
}