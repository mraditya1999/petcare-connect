package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Comment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByParentId(String parentId);
    List<Comment> findByForumId(String forumId);
    Page<Comment> findByForumId(String forumId, Pageable pageable);
    void deleteByForumId(String forumId);
    long countByForumId(String forumId);
}
