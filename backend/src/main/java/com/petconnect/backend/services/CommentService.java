//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.CommentDTO;
//import com.petconnect.backend.dto.user.UserDTO;
//import com.petconnect.backend.entity.Comment;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.mappers.CommentMapper;
//import com.petconnect.backend.mappers.UserMapper;
//import com.petconnect.backend.repositories.CommentRepository;
//import com.petconnect.backend.repositories.ForumRepository;
//import com.petconnect.backend.repositories.LikeRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class CommentService {
//
//    private final UserRepository userRepository;
//    private final CommentRepository commentRepository;
//    private final CommentMapper commentMapper;
//    private final ForumRepository forumRepository;
//    private final LikeRepository likeRepository;
//    private final UserMapper userMapper;
//
//    @Autowired
//    public CommentService(UserRepository userRepository, CommentRepository commentRepository, CommentMapper commentMapper, ForumRepository forumRepository, LikeRepository likeRepository, UserMapper userMapper) {
//        this.userRepository = userRepository;
//        this.commentRepository = commentRepository;
//        this.commentMapper = commentMapper;
//        this.forumRepository = forumRepository;
//        this.likeRepository = likeRepository;
//        this.userMapper = userMapper;
//    }
//
//    public Page<CommentDTO> getAllCommentsByForumId(String forumId, int page, int size) {
//        Page<Comment> commentsPage = commentRepository.findByForumId(forumId, PageRequest.of(page, size));
//        Set<Long> userIds = commentsPage.getContent().stream().map(Comment::getUserId).collect(Collectors.toSet());
//        Map<Long, UserDTO> userSummaries = userRepository.findAllById(userIds).stream()
//                .collect(Collectors.toMap(User::getUserId, userMapper::toDTO));
//
//        return commentsPage.map(comment -> {
//            CommentDTO commentDTO = commentMapper.toDTO(comment);
//            UserDTO userDTO = userSummaries.get(comment.getUserId());
//            if (userDTO != null) {
//                commentDTO.setFirstName(userDTO.getFirstName());
//                commentDTO.setLastName(userDTO.getLastName());
//                commentDTO.setEmail(userDTO.getEmail());
//            }
//            return commentDTO;
//        });
//    }
//
//    @Transactional
//    public CommentDTO createComment(String email, String forumId, CommentDTO commentDTO) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//        forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
//
//        Comment comment = commentMapper.toEntity(commentDTO);
//        comment.setForumId(forumId);
//        comment.setUserId(user.getUserId());
//        comment = commentRepository.save(comment);
//
//        return mapCommentWithRepliesToDTO(comment);
//    }
//
//    @Transactional
//    public CommentDTO getCommentByIdWithSubcomments(String commentId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID " + commentId));
//        return mapCommentWithSubcommentsToDTO(comment);
//    }
//
//    private CommentDTO mapCommentWithSubcommentsToDTO(Comment comment) {
//        CommentDTO commentDTO = commentMapper.toDTO(comment);
//        Set<CommentDTO> repliesDTOs = commentRepository.findByParentComment(comment).stream()
//                .map(this::mapCommentWithSubcommentsToDTO)
//                .collect(Collectors.toSet());
//        commentDTO.setReplies(repliesDTOs);
//        return commentDTO;
//    }
//
//    @Transactional
//    public Optional<CommentDTO> updateComment(String email, String commentId, CommentDTO commentDTO) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//
//        return commentRepository.findById(commentId)
//                .filter(comment -> comment.getUserId().equals(user.getUserId()))
//                .map(existingComment -> {
//                    existingComment.setText(commentDTO.getText());
//                    Comment updatedComment = commentRepository.save(existingComment);
//                    return commentMapper.toDTO(updatedComment);
//                });
//    }
//
//    @Transactional
//    public boolean deleteComment(String email, String commentId) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//
//        return commentRepository.findById(commentId)
//                .filter(comment -> comment.getUserId().equals(user.getUserId()))
//                .map(comment -> {
//                    if (!comment.getReplies().isEmpty()) {
//                        throw new IllegalArgumentException("Cannot delete comment with replies.");
//                    }
//                    likeRepository.deleteByCommentId(commentId);
//                    commentRepository.delete(comment);
//                    return true;
//                }).orElse(false);
//    }
//
//    @Transactional
//    public CommentDTO replyToComment(String forumId, String email, CommentDTO commentDTO, String parentId) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
//        forumRepository.findById(forumId)
//                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
//        Comment parentComment = commentRepository.findById(parentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id " + parentId));
//
//        Comment comment = commentMapper.toEntity(commentDTO);
//        comment.setForumId(forumId);
//        comment.setUserId(user.getUserId());
//        comment.setParentComment(parentComment);
//        comment = commentRepository.save(comment);
//
//        return mapCommentWithRepliesToDTO(comment);
//    }
//
//    private CommentDTO mapCommentWithRepliesToDTO(Comment comment) {
//        CommentDTO commentDTO = commentMapper.toDTO(comment);
//        User user = userRepository.findById(comment.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + comment.getUserId()));
//
//        commentDTO.setFirstName(user.getFirstName());
//        commentDTO.setLastName(user.getLastName());
//        commentDTO.setEmail(user.getEmail());
//
//        if (comment.getParentComment() != null) {
//            commentDTO.setParentId(comment.getParentComment().getCommentId());
//        }
//
//        Set<CommentDTO> repliesDTOs = commentRepository.findByParentComment(comment).stream()
//                .map(this::mapCommentWithRepliesToDTO)
//                .collect(Collectors.toSet());
//        commentDTO.setReplies(repliesDTOs);
//
//        return commentDTO;
//    }
//
//    public long getCommentsCountByForumId(String forumId) {
//        return commentRepository.countByForumId(forumId);
//    }
//
//    // ADMIN SERVICES
//    public Page<Comment> getAllComments(PageRequest pageRequest) {
//        return commentRepository.findAll(pageRequest);
//    }
//
//    public Optional<Comment> getCommentById(String commentId) {
//        return commentRepository.findById(commentId);
//    }
//
//    @Transactional
//    public Optional<CommentDTO> updateCommentByIdAdmin(String commentId, CommentDTO commentDTO) {
//        return commentRepository.findById(commentId)
//                .map(existingComment -> {
//                    existingComment.setText(commentDTO.getText());
//                    Comment updatedComment = commentRepository.save(existingComment);
//                    return commentMapper.toDTO(updatedComment);
//                });
//    }
//
//    @Transactional
//    public boolean deleteCommentById(String commentId) {
//        return commentRepository.findById(commentId)
//                .map(comment -> {
//                    likeRepository.deleteByCommentId(commentId);
//                    commentRepository.delete(comment);
//                    return true;
//                }).orElse(false);
//    }
//}
//

package com.petconnect.backend.services;

import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.UserMapper;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.ForumRepository;
import com.petconnect.backend.repositories.LikeRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ForumRepository forumRepository;
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;

    @Autowired
    public CommentService(UserRepository userRepository, CommentRepository commentRepository, CommentMapper commentMapper, ForumRepository forumRepository, LikeRepository likeRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.forumRepository = forumRepository;
        this.likeRepository = likeRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<CommentDTO> getAllCommentsByForumId(String forumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentsPage = commentRepository.findByForumId(forumId, pageable);

        // Fetch all comments for this forum, including replies
        List<Comment> allComments = commentRepository.findByForumId(forumId);

        // Build a map of commentId to CommentDTO for efficient lookup
        Map<String, CommentDTO> commentDtoMap = allComments.stream()
                .map(this::mapCommentWithRepliesToDTO)
                .collect(Collectors.toMap(CommentDTO::getCommentId, Function.identity()));

        // Build the tree structure
        List<CommentDTO> rootComments = new ArrayList<>();
        for (CommentDTO commentDTO: commentDtoMap.values()) {
            if (commentDTO.getParentId()!= null) {
                CommentDTO parentComment = commentDtoMap.get(commentDTO.getParentId());
                if (parentComment!= null) {
                    parentComment.getReplies().add(commentDTO);
                }
            } else {
                rootComments.add(commentDTO);
            }
        }

        // Paginate the root comments
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rootComments.size());
        List<CommentDTO> paginatedRootComments = rootComments.subList(start, end);

        // Create a new Page object with the paginated root comments
        return new PageImpl<>(paginatedRootComments, pageable, rootComments.size());
    }

    @Transactional
    public CommentDTO createComment(String email, String forumId, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setForumId(forumId);
        comment.setUserId(user.getUserId());
        comment = commentRepository.save(comment);

        return mapCommentWithRepliesToDTO(comment);
    }



    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CommentDTO getCommentByIdWithSubcomments(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID " + commentId));
        return mapCommentWithRepliesToDTO(comment);
    }


    @Transactional
    public Optional<CommentDTO> updateComment(String email, String commentId, CommentDTO commentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        return commentRepository.findById(commentId)
                .filter(comment -> comment.getUserId().equals(user.getUserId()))
                .map(existingComment -> {
                    existingComment.setText(commentDTO.getText());
                    Comment updatedComment = commentRepository.save(existingComment);
                    return commentMapper.toDTO(updatedComment);
                });
    }

    @Transactional
    public boolean deleteComment(String email, String commentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        return commentRepository.findById(commentId)
                .filter(comment -> comment.getUserId().equals(user.getUserId()))
                .map(comment -> {
                    likeRepository.deleteByCommentId(commentId); // Delete likes first
                    commentRepository.delete(comment); // Then delete comment
                    return true;
                }).orElse(false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CommentDTO replyToComment(String forumId, String email, CommentDTO commentDTO, String parentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with id " + forumId));
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id " + parentId));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setForumId(forumId);
        comment.setUserId(user.getUserId());
        comment.setParentComment(parentComment);

        comment = commentRepository.save(comment);

        parentComment.getReplies().add(comment);
        commentRepository.save(parentComment);

        return mapCommentWithRepliesToDTO(comment);
    }

    private CommentDTO mapCommentWithRepliesToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO(); // Create a new DTO directly

        // Manually map the necessary fields (without the mapper for replies):
        commentDTO.setCommentId(comment.getCommentId());
        commentDTO.setForumId(comment.getForumId());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setText(comment.getText());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setParentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
        commentDTO.setLikedByUsers(new HashSet<>(comment.getLikedByUsers())); // Create a copy

        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + comment.getUserId()));
        mapUserDetails(commentDTO, userMapper.toDTO(user)); // Use helper method for user details

        Set<CommentDTO> repliesDTOs = commentRepository.findByParentComment(comment).stream()
                .map(this::mapCommentWithRepliesToDTO) // Recursive call
                .collect(Collectors.toSet());
        commentDTO.setReplies(repliesDTOs);

        return commentDTO;
    }

    private void mapUserDetails(CommentDTO commentDTO, UserDTO userDTO) {
        commentDTO.setFirstName(userDTO.getFirstName());
        commentDTO.setLastName(userDTO.getLastName());
        commentDTO.setEmail(userDTO.getEmail());
    }

    public long getCommentsCountByForumId(String forumId) {
        return commentRepository.countByForumId(forumId);
    }

    // ADMIN SERVICES (These remain mostly the same, but apply the same principles)
    public Page<Comment> getAllComments(PageRequest pageRequest) {
        return commentRepository.findAll(pageRequest);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public Optional<CommentDTO> updateCommentByIdAdmin(String commentId, CommentDTO commentDTO) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    existingComment.setText(commentDTO.getText());
                    Comment updatedComment = commentRepository.save(existingComment);
                    return commentMapper.toDTO(updatedComment);
                });
    }

    @Transactional
    public boolean deleteCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    likeRepository.deleteByCommentId(commentId); // Delete likes
                    commentRepository.delete(comment); // Delete comment
                    return true;
                }).orElse(false);
    }
}