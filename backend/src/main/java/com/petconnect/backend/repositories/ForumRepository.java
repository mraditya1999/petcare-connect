package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Forum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {
    List<Forum> findByUserId(String userId);
    List<Forum> findByTagsIn(Collection<List<String>> tags);
}

