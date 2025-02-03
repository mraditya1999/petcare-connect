package com.petconnect.backend.services;

import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> getCommentsByForumId(String forumId) {
        return commentRepository.findByForumId(forumId);
    }

    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public Optional<Comment> updateComment(String commentId, Comment comment) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    existingComment.setText(comment.getText());
                    return commentRepository.save(existingComment);
                });
    }

    @Transactional
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }
}
