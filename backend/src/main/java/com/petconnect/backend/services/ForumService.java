package com.petconnect.backend.services;

import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.dto.ForumDTO;
import com.petconnect.backend.dto.LikeDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.ForumMapper;
import com.petconnect.backend.mappers.LikeMapper;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ForumMapper forumMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;

    @Autowired
    public ForumService(ForumRepository forumRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, ForumMapper forumMapper, LikeMapper likeMapper, CommentMapper commentMapper) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.forumMapper = forumMapper;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
    }
    public ForumDTO getForum(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        return forumMapper.toDTO(forum);
    }

    public List<ForumDTO> getAllForums() {
        List<Forum> forums = forumRepository.findAll();
        return forums.stream()
                .map(forum -> {
                    ForumDTO forumDTO = forumMapper.toDTO(forum);
                    forumDTO.setLikes(likeRepository.findByForumId(forum.getForumId()).stream()
                            .map(likeMapper::toDTO)
                            .collect(Collectors.toList()));
                    forumDTO.setComments(commentRepository.findByForumId(forum.getForumId()).stream()
                            .map(commentMapper::toDTO)
                            .collect(Collectors.toList()));
                    return forumDTO;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public ForumDTO createForum(String email, ForumDTO forumDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        Forum forum = new Forum();
        forum.setUserId(String.valueOf(user.getUserId()));
        forum.setTitle(forumDTO.getTitle());
        forum.setContent(forumDTO.getContent());
        forum.setTags(forumDTO.getTags());
        forum = forumRepository.save(forum);
        return forumMapper.toDTO(forum);
    }

    public ForumDTO updateForum(String forumId, String email, ForumDTO forumDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        if (!forum.getUserId().equals(user.getUserId().toString())) {
            throw new IllegalArgumentException("You can only update your own forums.");
        }

        // Check for non-null fields in ForumDTO and update accordingly
        if (forumDTO.getTitle() != null) {
            forum.setTitle(forumDTO.getTitle());
        }
        if (forumDTO.getContent() != null) {
            forum.setContent(forumDTO.getContent());
        }
        if (forumDTO.getTags() != null) {
            forum.setTags(forumDTO.getTags());
        }

        forum = forumRepository.save(forum);
        return forumMapper.toDTO(forum);
    }


    public void deleteForum(String forumId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userDetails.getUsername()));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        if (!forum.getUserId().equals(String.valueOf(user.getUserId()))) {
            throw new IllegalArgumentException("You can only delete your own forums.");
        }
        forumRepository.delete(forum);
    }


    public LikeDTO likeForum(String forumId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        Like like = new Like();
        like.setForumId(forumId);
        like.setUserId(String.valueOf(user.getUserId()));
        like = likeRepository.save(like);
        return likeMapper.toDTO(like);
    }

    public CommentDTO commentOnForum(String forumId, String email, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        Comment comment = new Comment();
        comment.setForumId(forumId);
        comment.setUserId(String.valueOf(user.getUserId()));
        comment.setText(commentDTO.getText());
        comment = commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    public List<ForumDTO> getMyForums(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        List<Forum> forums = forumRepository.findByUserId(String.valueOf(user.getUserId()));
        return forums.stream().map(forumMapper::toDTO).collect(Collectors.toList());
    }
}
