package com.petconnect.backend.services;

import com.petconnect.backend.entity.Like;
import com.petconnect.backend.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Optional<Like> getLikeById(String likeId) {
        return likeRepository.findById(likeId);
    }

    public List<Like> getLikesByForumId(String forumId) {
        return likeRepository.findByForumId(forumId);
    }

    @Transactional
    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    @Transactional
    public void deleteLike(String likeId) {
        likeRepository.deleteById(likeId);
    }


    public boolean checkIfUserLikedForum(Long userId, String forumId) {
        return likeRepository.existsByUserIdAndForumId(userId, forumId);
    }

    public long getLikesCountForForum(String forumId) {
        return likeRepository.countByForumId(forumId);
    }

    @Transactional
    public void toggleLike(Long userId, String forumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like newLike = new Like();
            newLike.setUserId(userId);
            newLike.setForumId(forumId);
            likeRepository.save(newLike);
        }
    }


}