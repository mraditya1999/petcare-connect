package com.petconnect.backend.services;

import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.repositories.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ForumService {
    private final ForumRepository forumRepository;

    @Autowired
    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    public List<Forum> getAllForums() {
        return forumRepository.findAll();
    }

    public Optional<Forum> getForumById(String forumId) {
        return forumRepository.findById(forumId);
    }

    public List<Forum> getForumsByUserId(String userId) {
        return forumRepository.findByUserId(userId);
    }

    @Transactional
    public Forum createForum(Forum forum) {
        return forumRepository.save(forum);
    }

    @Transactional
    public Optional<Forum> updateForum(String forumId, Forum forum) {
        return forumRepository.findById(forumId)
                .map(existingForum -> {
                    existingForum.setTitle(forum.getTitle());
                    existingForum.setContent(forum.getContent());
                    existingForum.setTags(forum.getTags());
                    return forumRepository.save(existingForum);
                });
    }

    @Transactional
    public void deleteForum(String forumId) {
        forumRepository.deleteById(forumId);
    }
}
