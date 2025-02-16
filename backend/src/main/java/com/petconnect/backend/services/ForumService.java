package com.petconnect.backend.services;

import com.petconnect.backend.dto.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private static final Logger logger = LoggerFactory.getLogger(ForumService.class);

    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ForumMapper forumMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final LikeService likeService;
    private final CommentService commentService;

    @Autowired
    public ForumService(ForumRepository forumRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, ForumMapper forumMapper, LikeMapper likeMapper, CommentMapper commentMapper, LikeService likeService, CommentService commentService) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.forumMapper = forumMapper;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> getAllForums(Pageable pageable) {
        Page<Forum> forums = forumRepository.findAll(pageable);

        return forums.map(forum -> {
            ForumDTO forumDTO = forumMapper.toDTO(forum);

            User user = userRepository.findById(forum.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));

            forumDTO.setFirstName(user.getFirstName());
            forumDTO.setLastName(user.getLastName());
            forumDTO.setEmail(user.getEmail());

            forumDTO.setLikesCount(likeService.getLikesCountForForum(forum.getForumId()));
            forumDTO.setCommentsCount(commentService.getCommentsCountByForumId(forum.getForumId()));

            return forumDTO;
        });
    }


    @Transactional(readOnly = true)
    public ForumDTO getForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        ForumDTO forumDTO = forumMapper.toDTO(forum);

        User user = userRepository.findById(forum.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));

        forumDTO.setFirstName(user.getFirstName());
        forumDTO.setLastName(user.getLastName());
        forumDTO.setEmail(user.getEmail());

//        // Fetch likes for the forum and add to ForumDTO
//        List<LikeDTO> likes = likeService.getLikesByForumId(forumId).stream()
//                .map(likeMapper::toDTO)
//                .collect(Collectors.toList());
//        forumDTO.setLikes(likes);

        // Fetch comments for the forum and add to ForumDTO
//        List<CommentDTO> comments = commentService.getAllCommentsByForumId(forumId).stream()
//                .map(commentMapper::toDTO)
//                .collect(Collectors.toList());
//        forumDTO.setComments(comments);

        return forumDTO;
    }

    @Transactional
    public ForumDTO createForum(String email, ForumCreateDTO forumCreateDTO) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Forum forum = new Forum();
        forum.setUserId(user.getUserId());
        forum.setTitle(forumCreateDTO.getTitle());
        forum.setContent(forumCreateDTO.getContent());
        forum.setTags(forumCreateDTO.getTags());

        forum = forumRepository.save(forum);
        ForumDTO result = forumMapper.toDTO(forum);

        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setEmail(user.getEmail());
        result.setLikesCount(0L);
        result.setCommentsCount(0L);
        result.setCreatedAt(forum.getCreatedAt());
        result.setUpdatedAt(forum.getUpdatedAt());

        return result;
    }

    @Transactional
    public ForumDTO updateForum(String forumId, String email, UpdateForumDTO updateForumDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        if (!forum.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can only update your own forums.");
        }

        if (updateForumDTO.getTitle() != null) {
            forum.setTitle(updateForumDTO.getTitle());
        }
        if (updateForumDTO.getContent() != null) {
            forum.setContent(updateForumDTO.getContent());
        }
        if (updateForumDTO.getTags() != null) {
            forum.setTags(updateForumDTO.getTags());
        }

        forum = forumRepository.save(forum);
        return forumMapper.toDTO(forum);
    }

    @Transactional
    public void deleteForum(String forumId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userDetails.getUsername()));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        if (!forum.getUserId().equals((user.getUserId()))) {
            throw new IllegalArgumentException("You can only delete your own forums.");
        }

        // Delete all associated likes
        likeRepository.deleteByForumId(forumId);

        // Delete all associated comments
        commentRepository.deleteByForumId(forumId);

        // Delete the forum itself
        forumRepository.delete(forum);
    }

    @Transactional
    public void deleteComment(String forumId, String commentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));

        System.out.println("Authenticated User ID: " + user.getUserId());
        System.out.println("Comment User ID: " + comment.getUserId());

        if (!comment.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can only delete your own comments.");
        }

        commentRepository.delete(comment);
    }


    @Transactional(readOnly = true)
    public List<ForumDTO> getMyForums(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        List<Forum> forums = forumRepository.findByUserId(user.getUserId());

        return forums.stream().map(forum -> {
            ForumDTO forumDTO = forumMapper.toDTO(forum);
            forumDTO.setFirstName(user.getFirstName());
            forumDTO.setLastName(user.getLastName());
            forumDTO.setEmail(user.getEmail());
            forumDTO.setLikesCount(likeService.getLikesCountForForum(forum.getForumId()));
            forumDTO.setCommentsCount(commentService.getCommentsCountByForumId(forum.getForumId()));
            return forumDTO;
        }).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ForumDTO> searchForums(String keyword) {
        List<Forum> forums = forumRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
        return forums.stream().map(forum -> {
            ForumDTO forumDTO = forumMapper.toDTO(forum);

            User user = userRepository.findById(forum.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));

            forumDTO.setFirstName(user.getFirstName());
            forumDTO.setLastName(user.getLastName());
            forumDTO.setEmail(user.getEmail());

            return forumDTO;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> sortForums(String field) {
        List<Forum> forums = forumRepository.findAll(Sort.by(Sort.Direction.DESC, field));
        return forums.stream().map(forum -> {
            ForumDTO forumDTO = forumMapper.toDTO(forum);

            User user = userRepository.findById(forum.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));

            forumDTO.setFirstName(user.getFirstName());
            forumDTO.setLastName(user.getLastName());
            forumDTO.setEmail(user.getEmail());

            return forumDTO;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> getTopFeaturedForums() {
        Pageable topThree = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "likes.size"));
        Page<Forum> forums = forumRepository.findAll(topThree);
        return forums.stream().map(forum -> {
            ForumDTO forumDTO = forumMapper.toDTO(forum);

            User user = userRepository.findById(forum.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));

            forumDTO.setFirstName(user.getFirstName());
            forumDTO.setLastName(user.getLastName());
            forumDTO.setEmail(user.getEmail());

            return forumDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, String> toggleLikeOnForum(String forumId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        Long userId = user.getUserId();

        // Find existing like (using a query in the repository)
        Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);

        Map<String, String> response = new HashMap<>();
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            response.put("message", "Forum unliked successfully");
        } else {
            Like like = new Like();
            like.setForumId(forumId);
            like.setUserId(userId);
            likeRepository.save(like);
            response.put("message", "Forum liked successfully");
        }

        return response;
    }

    @Transactional
    public CommentDTO commentOnForum(String forumId, String email, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        Comment comment = new Comment();
        comment.setForumId(forumId);
        comment.setUserId(user.getUserId());
        comment.setText(commentDTO.getText());
        comment = commentRepository.save(comment);

        CommentDTO savedCommentDTO = commentMapper.toDTO(comment);
        savedCommentDTO.setFirstName(user.getFirstName());
        savedCommentDTO.setLastName(user.getLastName());
        savedCommentDTO.setEmail(user.getEmail());

        return savedCommentDTO;
    }


//    ADMIN SERVICES

    @Transactional
    public ForumDTO updateForumById(String forumId, ForumDTO forumDTO) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

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

    @Transactional
    public void deleteForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        // Delete all associated likes
        likeRepository.deleteByForumId(forumId);

        // Delete all associated comments
        commentRepository.deleteByForumId(forumId);

        // Delete the forum itself
        forumRepository.delete(forum);
    }

}
