package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/forum/{forumId}")
    public ResponseEntity<CustomApiResponse<String>> likeForum(@PathVariable String forumId) {
        likeService.likeForum(forumId);
        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, ResponseMessages.FORUM_LIKED, forumId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CustomApiResponse<String>> likeComment(@PathVariable String commentId) {
        likeService.likeComment(commentId);
        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, ResponseMessages.COMMENT_LIKED, commentId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/forum/{forumId}")
    public ResponseEntity<CustomApiResponse<String>> unlikeForum(@PathVariable String forumId) {
        likeService.unlikeForum(forumId);
        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, ResponseMessages.FORUM_UNLIKED, forumId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<CustomApiResponse<String>> unlikeComment(@PathVariable String commentId) {
        likeService.unlikeComment(commentId);
        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, ResponseMessages.COMMENT_UNLIKED, commentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
