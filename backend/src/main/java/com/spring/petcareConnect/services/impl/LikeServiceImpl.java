package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.entities.Comment;
import com.spring.petcareConnect.entities.Forum;
import com.spring.petcareConnect.entities.Like;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.repositories.mongo.CommentRepository;
import com.spring.petcareConnect.repositories.mongo.ForumRepository;
import com.spring.petcareConnect.repositories.mongo.LikeRepository;
import com.spring.petcareConnect.services.LikeService;
import com.spring.petcareConnect.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ForumRepository forumRepository;

    public LikeServiceImpl(UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository, ForumRepository forumRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.forumRepository = forumRepository;
    }


    @Override
    public void likeComment(String commentId) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        Long userId = user.getUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (comment.isLikedByUser(userId)) {
            throw new APIException("You have already liked this comment");
        }

        // Add like
        comment.addLike(userId);
        commentRepository.save(comment);

        logger.info("User {} liked comment {}", userId, commentId);
    }

    @Override
    public void unlikeComment(String commentId) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        Long userId = user.getUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.isLikedByUser(userId)) {
            throw new APIException("You have not liked this comment");
        }

        // Remove like
        comment.removeLike(userId);
        commentRepository.save(comment);

        logger.info("User {} unliked comment {}", userId, commentId);
    }


    @Override
    public void unlikeForum(String forumId) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        Long userId = user.getUserId();

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));

        Like existingLike = likeRepository.findByForumIdAndUserId(forumId, userId)
                .orElseThrow(() -> new APIException("You have not liked this forum"));

        likeRepository.delete(existingLike);
        int currentCount = forum.getLikeCount() != null ? forum.getLikeCount() : 0;
        forum.setLikeCount(Math.max(0, currentCount - 1));
        forumRepository.save(forum);

        logger.info("User {} unliked forum {}", userId, forumId);
    }


    @Override
    public void likeForum(String forumId) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        Long userId = user.getUserId();

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));

        // Check if user already liked this forum
        Optional<Like> existingLike = likeRepository.findByForumIdAndUserId(forumId, userId);
        if (existingLike.isPresent()) {
            throw new APIException("You have already liked this forum");
        }

        // Create new Like entry
        Like like = new Like();
        like.setForumId(forumId);
        like.setUserId(userId);
        like.setCreatedAt(Instant.now());
        like.validateLike(); // ensures forumId is set correctly
        likeRepository.save(like);

        // Update forum like count
        forum.setLikeCount(forum.getLikeCount() + 1);
        forumRepository.save(forum);

        logger.info("User {} liked forum {}", userId, forumId);
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }

}
