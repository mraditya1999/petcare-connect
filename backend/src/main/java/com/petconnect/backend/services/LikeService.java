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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, ForumRepository forumRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public List<Like> getAllLikesByForumId(String forumId) {
        return likeRepository.findByForumId(forumId);
    }

    @Transactional
    public Map<String, String> toggleLikeOnForum(String forumId, String email) {
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
            response.put("message", "You have unliked the forum successfully.");
        } else {
            // If the user has not liked the forum, add a new like
            Like like = new Like();
            like.setForumId(forumId);
            like.setUserId(userId);
            likeRepository.save(like);
            response.put("message", "You have liked the forum successfully.");
        }

        return response;
    }

    public List<Like> getAllLikesByCommentId(String commentId) {
        return likeRepository.findByCommentId(commentId);
    }

    @Transactional
    public Map<String, String> toggleLikeOnComment(String commentId, String email) {
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
            response.put("message", "You have unliked the comment successfully.");
        } else {
            // If the user has not liked the comment, add a new like
            Like like = new Like();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setForumId(comment.getForumId()); // Set the forumId
            likeRepository.save(like);
            response.put("message", "You have liked the comment successfully.");
        }

        return response;
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Optional<Like> getLikeById(String likeId) {
        return likeRepository.findById(likeId);
    }

    @Transactional
    public void deleteLikeById(String likeId) {
        likeRepository.deleteById(likeId);
    }


    public boolean checkIfUserLikedForum(Long userId, String forumId) {
        return likeRepository.existsByUserIdAndForumId(userId, forumId);
    }

    public long getLikesCountForForum(String forumId) {
        return likeRepository.countByForumId(forumId);
    }

    @Transactional
    public void toggleLike(Long userId, String forumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like newLike = new Like();
            newLike.setUserId(userId);
            newLike.setForumId(forumId);
            likeRepository.save(newLike);
        }
    }


}