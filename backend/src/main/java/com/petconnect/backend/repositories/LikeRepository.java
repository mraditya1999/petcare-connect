package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Like;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findByForumId(String forumId);
    List<Like> findByUserId(String userId);
    boolean existsByForumIdAndUserId(@NotNull String forumId, @NotNull String userId);
    Like findByForumIdAndUserId(@NotNull String forumId, @NotNull String userId);
    Like findByLikeIdAndUserId(String likeId, @NotNull String userId);
}
