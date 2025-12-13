package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Forum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {
    Page<Forum> findByUserId(Long userId, Pageable pageable);
    Page<Forum> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content,Pageable pageable);
    @NotNull List<Forum> findAll(@NotNull Sort sort);
    @NotNull Page<Forum> findAll(@NotNull Pageable pageable);
    @Query("{ 'tags': { $elemMatch: { $regex: ?0, $options: 'i' } } }")
    Page<Forum> findByTagsRegex(String regex, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $addFields: { likesCount: { $size: { $ifNull: ['$likes', []] } } } }",
            "{ $sort: { likesCount: -1, createdAt: -1 } }",
            "{ $limit: 3 }"
    })
    List<Forum> findTop3ByLikes();

}
