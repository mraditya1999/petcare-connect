package com.spring.petcareConnect.repositories.mongo;

import com.spring.petcareConnect.entities.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findAllByForumId(String forumId);
    void deleteAllByForumId(String forumId);
    List<Like> findAllByCommentId(String commentId);
    void deleteAllByCommentId(String commentId);
    List<Like> findAllByUserId(Long userId);

    Optional<Like> findByForumIdAndUserId(String forumId, Long userId);
}
