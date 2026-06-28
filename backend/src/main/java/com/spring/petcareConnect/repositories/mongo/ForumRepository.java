package com.spring.petcareConnect.repositories.mongo;

import com.spring.petcareConnect.entities.Forum;
import com.spring.petcareConnect.enums.ForumTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {

    Page<Forum> findAllByUserId(Long userId, Pageable pageable);

    Page<Forum> findByCategory(ForumTag category, Pageable pageable);

    Page<Forum> findByCategoryAndPublishedTrueAndIsDeletedFalse(ForumTag category, Pageable pageable);

    Page<Forum> findByIsFeaturedTrue(Pageable pageable);

    Optional<Forum> findByForumIdAndUserId(String forumId, Long userId);
}
