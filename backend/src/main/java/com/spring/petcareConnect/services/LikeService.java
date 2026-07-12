package com.spring.petcareConnect.services;

public interface LikeService {
    void likeComment(String commentId);

    void unlikeForum(String forumId);

    void unlikeComment(String commentId);

    void likeForum(String forumId);
}
