/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.ServiceComment;
import housemate.mappers.CommentMapper;
import housemate.models.CommentDTO;
import housemate.repositories.CommentRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    public ResponseEntity<List<ServiceComment>> getAllCommentByServiceId(int serviceId) {
        List<ServiceComment> listComment = commentRepository.getAllCommentByServiceId(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(listComment);
    }

    public ResponseEntity<String> addComment(HttpServletRequest request, CommentDTO.Add commentAdd) {
        commentAdd.setUserId(authorizationUtil.getUserIdFromAuthorizationHeader(request));
        commentAdd.setDate(LocalDateTime.now());
        ServiceComment serviceComment = commentMapper.mapDTOtoEntity(commentAdd);
        commentRepository.save(serviceComment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created");
    }

    @Transactional
    public ResponseEntity<String> removeComment(HttpServletRequest request, int commentId) {
        if (commentRepository.deleteComment(commentId, authorizationUtil.getUserIdFromAuthorizationHeader(request)) == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment not found");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment removed");
    }
}
