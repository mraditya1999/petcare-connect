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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private static final String USER_NOT_FOUND = "User not found with email ";
    private static final String FORUM_NOT_FOUND = "Forum not found with id ";
    private static final String COMMENT_NOT_FOUND = "Comment not found with id ";


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
        return forums.map(this::convertToForumDTO);
    }

    @Transactional(readOnly = true)
    public ForumDTO getForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        return convertToForumDTO(forum);
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> getTopFeaturedForums() {
        Pageable topThree = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "likes.size"));
        Page<Forum> forums = forumRepository.findAll(topThree);
        return forums.stream().map(this::convertToForumDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> searchForums(String keyword) {
        List<Forum> forums = forumRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
        return forums.stream().map(this::convertToForumDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> searchForumsByTags(List<String> tags) {
        List<Forum> forums = forumRepository.findByTagsIn(tags);
        return forums.stream().map(this::convertToForumDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> sortForums(String field) {
        List<Forum> forums = forumRepository.findAll(Sort.by(Sort.Direction.DESC, field));
        return forums.stream().map(this::convertToForumDTO).collect(Collectors.toList());
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
        return convertToForumDTO(forum);
    }

    @Transactional
    public ForumDTO updateForum(String email, String forumId, UpdateForumDTO updateForumDTO) {
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
        return convertToForumDTO(forum);
    }

    @Transactional
    public void deleteForum(String email, String forumId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        if (!forum.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can only delete your own forums.");
        }

        // Delete all associated likes
        likeRepository.deleteByForumId(forumId);

        // Delete all associated comments and their sub-comments
        deleteCommentsByForumId(forumId);

        // Delete the forum itself
        forumRepository.delete(forum);
    }

    @Transactional
    public void deleteCommentsByForumId(String forumId) {
        List<Comment> comments = commentRepository.findByForumId(forumId);
        for (Comment comment : comments) {
            deleteCommentAndSubComments(comment);
        }
    }

    public Page<Comment> getCommentsByForumId(String forumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByForumId(forumId, pageable);
    }


    @Transactional
    public void deleteCommentAndSubComments(Comment comment) {
        List<Comment> subComments = commentRepository.findByParentComment(comment);
        for (Comment subComment : subComments) {
            deleteCommentAndSubComments(subComment);
        }
        likeRepository.deleteByCommentId(comment.getCommentId());
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Page<ForumDTO> getMyForums(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Pageable pageable = PageRequest.of(page, size);
        Page<Forum> forums = forumRepository.findByUserId(user.getUserId(), pageable);
        return forums.map(this::convertToForumDTO);
    }


    public ForumDTO convertToForumDTO(Forum forum) {
        ForumDTO forumDTO = forumMapper.toDTO(forum);
        User user = userRepository.findById(forum.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));
        forumDTO.setFirstName(user.getFirstName());
        forumDTO.setLastName(user.getLastName());
        forumDTO.setEmail(user.getEmail());
        forumDTO.setLikesCount(likeService.getLikesCountForForum(forum.getForumId()));
        forumDTO.setCommentsCount(commentService.getCommentsCountByForumId(forum.getForumId()));
        return forumDTO;
    }

//
//    @Transactional
//    public void deleteComment(String forumId, String commentId, String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//
//        forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
//
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
//
//        if (!comment.getUserId().equals(user.getUserId())) {
//            throw new IllegalArgumentException("You can only delete your own comments.");
//        }
//
//        commentRepository.delete(comment);
//    }


//    ADMIN SERVICES

    @Transactional
    public ForumDTO updateForumById(String forumId, UpdateForumDTO updateForumDTO) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));

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
        return convertToForumDTO(forum);
    }

    @Transactional
    public void deleteForumById(String forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));
        // Delete all associated likes and comments
        likeRepository.deleteByForumId(forumId);
        commentRepository.deleteByForumId(forumId);
        forumRepository.delete(forum);
    }

//    @Transactional
//    public Map<String, String> toggleLikeOnForum(String forumId, String email) {
//        User user = getUserByEmail(email);
//        Forum forum = getForum(forumId);
//
//        Long userId = user.getUserId();
//        Optional<Like> existingLike = likeRepository.findByUserIdAndForumId(userId, forumId);
//
//        Map<String, String> response = new HashMap<>();
//        if (existingLike.isPresent()) {
//            likeRepository.delete(existingLike.get());
//            response.put("message", "Forum unliked successfully");
//        } else {
//            Like like = new Like();
//            like.setForumId(forumId);
//            like.setUserId(userId);
//            likeRepository.save(like);
//            response.put("message", "Forum liked successfully");
//        }
//
//        return response;
//    }
//
//    private Forum getForum(String forumId) {
//        return forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException(FORUM_NOT_FOUND + forumId));
//    }
//
//    private Comment getComment(String commentId) {
//        return commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + commentId));
//    }

//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + email));
//    }

//    private void updateForumDetails(Forum forum, UpdateForumDTO updateForumDTO) {
//        if (updateForumDTO.getTitle() != null) {
//            forum.setTitle(updateForumDTO.getTitle());
//        }
//        if (updateForumDTO.getContent() != null) {
//            forum.setContent(updateForumDTO.getContent());
//        }
//        if (updateForumDTO.getTags() != null) {
//            forum.setTags(updateForumDTO.getTags());
//        }
//    }

//    private void deleteAssociatedLikesAndComments(String forumId) {
//        likeRepository.deleteByForumId(forumId);
//        commentRepository.deleteByForumId(forumId);
//    }


//    @Transactional
//    public void deleteAnyComment(String commentId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
//        commentRepository.delete(comment);
//    }
}