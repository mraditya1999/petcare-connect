package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Comment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByForumId(String forumId);
    void deleteByForumId(String forumId);
    long countByForumId(String forumId);
}
