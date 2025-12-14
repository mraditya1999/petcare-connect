package com.petconnect.backend.services;

import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LikeService {
    
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);
    
    private final LikeRepository likeRepository;
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, ForumRepository forumRepository, UserRepository userRepository, CommentRepository commentRepository) {
        if (likeRepository == null) {
            throw new IllegalArgumentException("LikeRepository cannot be null");
        }
        if (forumRepository == null) {
            throw new IllegalArgumentException("ForumRepository cannot be null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        if (commentRepository == null) {
            throw new IllegalArgumentException("CommentRepository cannot be null");
        }
        this.likeRepository = likeRepository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Retrieves all likes for a forum.
     *
     * @param forumId the forum ID (must not be null or blank)
     * @return a list of likes for the forum
     * @throws IllegalArgumentException if forumId is null or blank
     */
    public List<Like> getAllLikesByForumId(String forumId) {
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        return likeRepository.findByForumId(forumId);
    }

    /**
     * Toggles a like on a forum for a user.
     *
     * @param forumId the forum ID (must not be null or blank)
     * @param email the user's email (must not be null or blank)
     * @return a map with a message indicating the action taken
     * @throws IllegalArgumentException if forumId or email is null or blank
     * @throws ResourceNotFoundException if user or forum is not found
     */
    @Transactional
    public Map<String, String> toggleLikeOnForum(String forumId, String email) {
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        
        try {
            // Fetch the user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            // Fetch the forum by ID
            Forum forum = forumRepository.findById(forumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

            Long userId = user.getUserId();

            // Check if the user already liked the forum
            Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);

            Map<String, String> response = new HashMap<>();
            if (existingLike.isPresent()) {
                // If the user already liked the forum, remove the like
                likeRepository.delete(existingLike.get());
                logger.info("User {} unliked forum {}", email, forumId);
                response.put("message", "You have unliked the forum successfully.");
            } else {
                // If the user has not liked the forum, add a new like
                Like like = new Like();
                like.setForumId(forumId);
                like.setUserId(userId);
                likeRepository.save(like);
                logger.info("User {} liked forum {}", email, forumId);
                response.put("message", "You have liked the forum successfully.");
            }

            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error toggling like on forum {} for user {}", forumId, email, e);
            throw new RuntimeException("Failed to toggle like on forum", e);
        }
    }

    public List<Like> getAllLikesByCommentId(String commentId) {
        return likeRepository.findByCommentId(commentId);
    }

    /**
     * Toggles a like on a comment for a user.
     *
     * @param commentId the comment ID (must not be null or blank)
     * @param email the user's email (must not be null or blank)
     * @return a map with a message indicating the action taken
     * @throws IllegalArgumentException if commentId or email is null or blank
     * @throws ResourceNotFoundException if user or comment is not found
     */
    @Transactional
    public Map<String, String> toggleLikeOnComment(String commentId, String email) {
        if (commentId == null || commentId.isBlank()) {
            throw new IllegalArgumentException("Comment ID cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        
        try {
            // Fetch the user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            // Fetch the comment by ID
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));

            Long userId = user.getUserId();

            // Check if the user already liked the comment
            Optional<Like> existingLike = likeRepository.findByUserIdAndCommentId(userId, commentId);

            Map<String, String> response = new HashMap<>();
            if (existingLike.isPresent()) {
                // If the user already liked the comment, remove the like
                likeRepository.delete(existingLike.get());
                logger.info("User {} unliked comment {}", email, commentId);
                response.put("message", "You have unliked the comment successfully.");
            } else {
                // If the user has not liked the comment, add a new like
                Like like = new Like();
                like.setCommentId(commentId);
                like.setUserId(userId);
                if (comment.getForumId() != null) {
                    like.setForumId(comment.getForumId()); // Set the forumId
                }
                likeRepository.save(like);
                logger.info("User {} liked comment {}", email, commentId);
                response.put("message", "You have liked the comment successfully.");
            }

            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error toggling like on comment {} for user {}", commentId, email, e);
            throw new RuntimeException("Failed to toggle like on comment", e);
        }
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Optional<Like> getLikeById(String likeId) {
        return likeRepository.findById(likeId);
    }

    /**
     * Deletes a like by its ID.
     *
     * @param likeId the like ID (must not be null or blank)
     * @throws IllegalArgumentException if likeId is null or blank
     */
    @Transactional
    public void deleteLikeById(String likeId) {
        if (likeId == null || likeId.isBlank()) {
            throw new IllegalArgumentException("Like ID cannot be null or blank");
        }
        try {
            likeRepository.deleteById(likeId);
            logger.debug("Deleted like with ID: {}", likeId);
        } catch (Exception e) {
            logger.error("Error deleting like with ID: {}", likeId, e);
            throw new RuntimeException("Failed to delete like", e);
        }
    }

    /**
     * Checks if a user has liked a forum.
     *
     * @param userId the user ID (must not be null)
     * @param forumId the forum ID (must not be null or blank)
     * @return true if the user has liked the forum, false otherwise
     * @throws IllegalArgumentException if userId is null or forumId is null/blank
     */
    public boolean checkIfUserLikedForum(Long userId, String forumId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        return likeRepository.existsByUserIdAndForumId(userId, forumId);
    }

    /**
     * Gets the count of likes for a forum.
     *
     * @param forumId the forum ID (must not be null or blank)
     * @return the count of likes
     * @throws IllegalArgumentException if forumId is null or blank
     */
    public long getLikesCountForForum(String forumId) {
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        return likeRepository.countByForumId(forumId);
    }

    /**
     * Toggles a like on a forum for a user (internal method).
     *
     * @param userId the user ID (must not be null)
     * @param forumId the forum ID (must not be null or blank)
     * @throws IllegalArgumentException if userId is null or forumId is null/blank
     */
    @Transactional
    public void toggleLike(Long userId, String forumId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (forumId == null || forumId.isBlank()) {
            throw new IllegalArgumentException("Forum ID cannot be null or blank");
        }
        
        try {
            Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);

            if (existingLike.isPresent()) {
                likeRepository.delete(existingLike.get());
                logger.debug("User {} unliked forum {}", userId, forumId);
            } else {
                Like newLike = new Like();
                newLike.setUserId(userId);
                newLike.setForumId(forumId);
                likeRepository.save(newLike);
                logger.debug("User {} liked forum {}", userId, forumId);
            }
        } catch (Exception e) {
            logger.error("Error toggling like for user {} on forum {}", userId, forumId, e);
            throw new RuntimeException("Failed to toggle like", e);
        }
    }


}