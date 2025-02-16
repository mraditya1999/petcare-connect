package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Forum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {
    List<Forum> findByUserId(Long userId);
    List<Forum> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    @NotNull List<Forum> findAll(Sort sort);
    @NotNull Page<Forum> findAll(Pageable pageable);
    List<Forum> findByTagsIn(Collection<List<String>> tags);
}
