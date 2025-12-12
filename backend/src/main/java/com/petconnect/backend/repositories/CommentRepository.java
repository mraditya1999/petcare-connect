package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByParentComment(Comment parentComment);
    List<Comment> findByForumId(String forumId);
    Page<Comment> findByForumId(String forumId, Pageable pageable);
    void deleteByForumId(String forumId);
    long countByForumId(String forumId);
    // Assuming you have a list of parent comment IDs:
    List<Comment> findByParentCommentIn(List<Comment> parentComments);
}


