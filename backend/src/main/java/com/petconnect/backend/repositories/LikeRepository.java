package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Like;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    boolean existsByUserIdAndForumId(Long userId, String forumId);
    long countByForumId(String forumId);
    Optional<Like> findByUserIdAndForumId(Long userId, String forumId);
    List<Like> findByForumId(String forumId);
    void deleteByForumId(String forumId);
}
