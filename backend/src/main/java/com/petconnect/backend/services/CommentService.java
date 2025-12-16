package com.petconnect.backend.services;

import com.petconnect.backend.dto.forum.CommentDTO;
import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.UserMapper;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ForumRepository forumRepository;
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;

    @Autowired
    public CommentService(UserRepository userRepository, CommentRepository commentRepository, CommentMapper commentMapper, ForumRepository forumRepository, LikeRepository likeRepository, UserMapper userMapper) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        if (commentRepository == null) {
            throw new IllegalArgumentException("CommentRepository cannot be null");
        }
        if (commentMapper == null) {
            throw new IllegalArgumentException("CommentMapper cannot be null");
        }
        if (forumRepository == null) {
            throw new IllegalArgumentException("ForumRepository cannot be null");
        }
        if (likeRepository == null) {
            throw new IllegalArgumentException("LikeRepository cannot be null");
        }
        if (userMapper == null) {
            throw new IllegalArgumentException("UserMapper cannot be null");
        }
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.forumRepository = forumRepository;
        this.likeRepository = likeRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<CommentDTO> getAllCommentsByForumId(String forumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentsPage = commentRepository.findByForumId(forumId, pageable);

        // Fetch all comments for this forum, including replies
        List<Comment> allComments = commentRepository.findByForumId(forumId);

        // Build a map of commentId to CommentDTO for efficient lookup
        Map<String, CommentDTO> commentDtoMap = allComments.stream()
                .map(this::mapCommentWithRepliesToDTO)
                .collect(Collectors.toMap(CommentDTO::getCommentId, Function.identity()));

        // Build the tree structure
        List<CommentDTO> rootComments = new ArrayList<>();
        for (CommentDTO commentDTO: commentDtoMap.values()) {
            if (commentDTO.getParentId()!= null) {
                CommentDTO parentComment = commentDtoMap.get(commentDTO.getParentId());
                if (parentComment!= null) {
                    parentComment.getReplies().add(commentDTO);
                }
            } else {
                rootComments.add(commentDTO);
            }
        }

        // Paginate the root comments
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rootComments.size());
        List<CommentDTO> paginatedRootComments = rootComments.subList(start, end);

        // Create a new Page object with the paginated root comments
        return new PageImpl<>(paginatedRootComments, pageable, rootComments.size());
    }

    /**
     * Creates a new comment on a forum.
     *
     * @param email the user's email (must not be null or blank)
     * @param forumId the forum ID (must not be null or blank)
     * @param commentDTO the comment data (must not be null)
     * @return the created comment DTO
     * @throws IllegalArgumentException if any parameter is null or email/forumId is blank
     * @throws ResourceNotFoundException if user or forum is not found
     */
    @Transactional
    public CommentDTO createComment(String email, String forumId, CommentDTO commentDTO) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        if (commentDTO == null) {
            throw new IllegalArgumentException("CommentDTO cannot be null");
        }
        if (commentDTO.getText() == null || commentDTO.getText().isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be null or blank");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
            forumRepository.findById(forumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

            Comment comment = commentMapper.toEntity(commentDTO);
            comment.setForumId(forumId);
            comment.setUserId(user.getUserId());
            comment = commentRepository.save(comment);

            logger.info("Comment created by user {} on forum {}", email, forumId);
            return mapCommentWithRepliesToDTO(comment);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating comment for user {} on forum {}", email, forumId, e);
            throw new RuntimeException("Failed to create comment", e);
        }
    }



    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CommentDTO getCommentByIdWithSubcomments(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID " + commentId));
        return mapCommentWithRepliesToDTO(comment);
    }


    @Transactional
    public Optional<CommentDTO> updateComment(String email, String commentId, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        return commentRepository.findById(commentId)
                .filter(comment -> comment.getUserId().equals(user.getUserId()))
                .map(existingComment -> {
                    existingComment.setText(commentDTO.getText());
                    Comment updatedComment = commentRepository.save(existingComment);
                    return commentMapper.toDTO(updatedComment);
                });
    }

    /**
     * Deletes a comment if the user is the owner.
     *
     * @param email the user's email (must not be null or blank)
     * @param commentId the comment ID (must not be null or blank)
     * @return true if the comment was deleted, false if not found or user is not the owner
     * @throws IllegalArgumentException if email or commentId is null or blank
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public boolean deleteComment(String email, String commentId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (commentId == null || commentId.isBlank()) {
            throw new IllegalArgumentException("Comment ID cannot be null or blank");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            return commentRepository.findById(commentId)
                    .filter(comment -> comment.getUserId().equals(user.getUserId()))
                    .map(comment -> {
                        likeRepository.deleteByCommentId(commentId); // Delete likes first
                        commentRepository.delete(comment); // Then delete comment
                        logger.info("Comment {} deleted by user {}", commentId, email);
                        return true;
                    }).orElse(false);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting comment {} for user {}", commentId, email, e);
            throw new RuntimeException("Failed to delete comment", e);
        }
    }

    /**
     * Creates a reply to a comment.
     *
     * @param forumId the forum ID (must not be null or blank)
     * @param email the user's email (must not be null or blank)
     * @param commentDTO the reply comment data (must not be null)
     * @param parentId the parent comment ID (must not be null or blank)
     * @return the created reply comment DTO
     * @throws IllegalArgumentException if any parameter is null or forumId/email/parentId is blank
     * @throws ResourceNotFoundException if user, forum, or parent comment is not found
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CommentDTO replyToComment(String forumId, String email, CommentDTO commentDTO, String parentId) {
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (commentDTO == null) {
            throw new IllegalArgumentException("CommentDTO cannot be null");
        }
        if (commentDTO.getText() == null || commentDTO.getText().isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be null or blank");
        }
        if (parentId == null || parentId.isBlank()) {
            throw new IllegalArgumentException("Parent comment ID cannot be null or blank");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
            forumRepository.findById(forumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id " + parentId));

            Comment comment = commentMapper.toEntity(commentDTO);
            comment.setForumId(forumId);
            comment.setUserId(user.getUserId());
            comment.setParentComment(parentComment);

            comment = commentRepository.save(comment);

            if (parentComment.getReplies() == null) {
                parentComment.setReplies(new HashSet<>());
            }
            parentComment.getReplies().add(comment);
            commentRepository.save(parentComment);

            logger.info("Reply created by user {} to comment {} on forum {}", email, parentId, forumId);
            return mapCommentWithRepliesToDTO(comment);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating reply for user {} to comment {} on forum {}", email, parentId, forumId, e);
            throw new RuntimeException("Failed to create reply", e);
        }
    }

    private CommentDTO mapCommentWithRepliesToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO(); // Create a new DTO directly

        // Manually map the necessary fields (without the mapper for replies):
        commentDTO.setCommentId(comment.getCommentId());
        commentDTO.setForumId(comment.getForumId());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setText(comment.getText());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setParentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
        commentDTO.setLikedByUsers(new HashSet<>(comment.getLikedByUsers())); // Create a copy

        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + comment.getUserId()));
        mapUserDetails(commentDTO, userMapper.toDTO(user)); // Use helper method for user details

        Set<CommentDTO> repliesDTOs = commentRepository.findByParentComment(comment).stream()
                .map(this::mapCommentWithRepliesToDTO) // Recursive call
                .collect(Collectors.toSet());
        commentDTO.setReplies(repliesDTOs);

        return commentDTO;
    }

    private void mapUserDetails(CommentDTO commentDTO, UserDTO userDTO) {
        commentDTO.setFirstName(userDTO.getFirstName());
        commentDTO.setLastName(userDTO.getLastName());
        commentDTO.setEmail(userDTO.getEmail());
    }

    public long getCommentsCountByForumId(String forumId) {
        return commentRepository.countByForumId(forumId);
    }

    // ADMIN SERVICES (These remain mostly the same, but apply the same principles)
    public Page<Comment> getAllComments(PageRequest pageRequest) {
        return commentRepository.findAll(pageRequest);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public Optional<CommentDTO> updateCommentByIdAdmin(String commentId, CommentDTO commentDTO) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    existingComment.setText(commentDTO.getText());
                    Comment updatedComment = commentRepository.save(existingComment);
                    return commentMapper.toDTO(updatedComment);
                });
    }

    @Transactional
    public boolean deleteCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    likeRepository.deleteByCommentId(commentId); // Delete likes
                    commentRepository.delete(comment); // Delete comment
                    return true;
                }).orElse(false);
    }
}