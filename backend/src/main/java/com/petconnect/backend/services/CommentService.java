package com.petconnect.backend.services;

import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.dto.ForumDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ForumRepository forumRepository;

    private final LikeRepository likeRepository;


    @Autowired
    public CommentService(UserRepository userRepository, CommentRepository commentRepository, CommentMapper commentMapper, ForumRepository forumRepository, LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.forumRepository = forumRepository;
        this.likeRepository = likeRepository;
    }

    public Page<Comment> getAllCommentsByForumId(String forumId, int page, int size) {
        return commentRepository.findByForumId(forumId, PageRequest.of(page, size));
    }

    @Transactional
    public CommentDTO createComment(String email, String forumId, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        Comment comment = new Comment();
        comment.setForumId(forumId);
        comment.setUserId(user.getUserId());
        comment.setText(commentDTO.getText());
        comment = commentRepository.save(comment);

        CommentDTO savedCommentDTO = commentMapper.toDTO(comment);
        savedCommentDTO.setFirstName(user.getFirstName());
        savedCommentDTO.setLastName(user.getLastName());
        savedCommentDTO.setEmail(user.getEmail());

        return savedCommentDTO;
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

    @Transactional
    public boolean deleteComment(String email,String commentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();

            if (!comment.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("You can only delete your own comments.");
            }

            List<Comment> replies = commentRepository.findByParentId(commentId);
            if (!replies.isEmpty()) {
                throw new IllegalArgumentException("Cannot delete comment with replies.");
            }

            // Delete all likes associated with this comment
            likeRepository.deleteByCommentId(commentId);

            // Delete the comment
            commentRepository.delete(comment);
            return true;
        } else {
            throw new ResourceNotFoundException("Comment not found with id " + commentId);
        }
    }

    @Transactional
    public CommentDTO replyToComment(String forumId, String email, CommentDTO commentDTO, String parentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        // Fetch the parent comment using the parentId
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id " + parentId));

        Comment comment = new Comment();
        comment.setForumId(forumId);
        comment.setUserId(user.getUserId());
        comment.setText(commentDTO.getText());
        comment.setParentId(parentComment.getCommentId());
        comment = commentRepository.save(comment);

        CommentDTO savedCommentDTO = commentMapper.toDTO(comment);
        savedCommentDTO.setFirstName(user.getFirstName());
        savedCommentDTO.setLastName(user.getLastName());
        savedCommentDTO.setEmail(user.getEmail());

        return savedCommentDTO;
    }

//    @Transactional
//    public void deleteCommentAndReplies(Comment comment) {
//        List<Comment> replies = commentRepository.findByParentId(comment.getCommentId());
//        for (Comment reply : replies) {
//            deleteCommentAndReplies(reply);
//        }
//        likeRepository.deleteByCommentId(comment.getCommentId());
//        commentRepository.delete(comment);
//    }

//    @Transactional
//    public CommentDTO toggleLikeOnComment(String commentId, String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
//
//        if (comment.getLikedByUsers().contains(user.getUserId())) {
//            comment.getLikedByUsers().remove(user.getUserId());
//        } else {
//            comment.getLikedByUsers().add(user.getUserId());
//        }
//
//        Comment updatedComment = commentRepository.save(comment);
//        return convertToCommentDTO(updatedComment);
//    }

//    public CommentDTO convertToCommentDTO(Comment comment) {
//        CommentDTO commentDTO = new CommentDTO();
//        commentDTO.setCommentId(comment.getCommentId());
//        commentDTO.setForumId(comment.getForumId());
//        commentDTO.setUserId(comment.getUserId());
//        commentDTO.setText(comment.getText());
//        commentDTO.setCreatedAt(comment.getCreatedAt());
//        commentDTO.setParentId(comment.getParent() != null ? comment.getParent().getCommentId() : null);
//        commentDTO.setLikedByUsers(comment.getLikedByUsers());
//        return commentDTO;
//    }

    public long getCommentsCountByForumId(String forumId) {
        return commentRepository.countByForumId(forumId);
    }

    // ADMIN SERVICES
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
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();

            // Delete all likes associated with this comment
            likeRepository.deleteByCommentId(commentId);

            // Delete the comment
            commentRepository.delete(comment);
            return true;
        } else {
            return false;
        }
    }

//        public void likeComment(String commentId) {
//        Optional<Comment> commentOptional = commentRepository.findById(commentId);
//        commentOptional.ifPresent(comment -> {
//            comment.addLike();
//            commentRepository.save(comment);
//        });
//    }
//
//    public void unlikeComment(String commentId) {
//        Optional<Comment> commentOptional = commentRepository.findById(commentId);
//        commentOptional.ifPresent(comment -> {
//            comment.removeLike();
//            commentRepository.save(comment);
//        });
//    }
//
}
