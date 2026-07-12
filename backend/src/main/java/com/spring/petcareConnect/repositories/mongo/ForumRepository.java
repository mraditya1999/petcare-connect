package com.spring.petcareConnect.repositories.mongo;

import com.spring.petcareConnect.entities.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {

    Page<Forum> findAllByUserId(Long userId, Pageable pageable);

    Page<Forum> findByIsFeaturedTrue(Pageable pageable);

    Optional<Forum> findByForumIdAndUserId(String forumId, Long userId);

    Page<Forum> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable);

    Page<Forum> findByTagsIn(List<String> tags, Pageable pageable);

    Page<Forum> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndTagsIn(
            String titleKeyword, String contentKeyword, List<String> tags, Pageable pageable);
}