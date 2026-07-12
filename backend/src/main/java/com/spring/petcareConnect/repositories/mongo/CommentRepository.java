package com.spring.petcareConnect.repositories.mongo;

import com.spring.petcareConnect.entities.Comment;
import com.spring.petcareConnect.entities.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findAllByForumId(String forumId, Pageable pageable);
    void deleteAllByForumId(String forumId);
    List<Comment> findAllByUserId(Long userId);
}