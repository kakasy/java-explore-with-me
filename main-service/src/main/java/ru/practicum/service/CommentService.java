package ru.practicum.service;

import ru.practicum.dto.comment.CommentDtoResponse;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDtoResponse createComment(Long userId, Long eventId, NewCommentDto creatingDto);

    CommentDtoResponse updateCommentById(Long userId, Long commentId, NewCommentDto updatingDto);

    CommentDtoResponse moderateCommentById(Long commentId, NewCommentDto updatingDto);

    void deleteComment(Long userId, Long commentId);

    void adminDeleteComment(Long commentId);

    List<CommentDtoResponse> getCommentsByEventId(Long eventId, Boolean isAsc, Integer from, Integer size);

    CommentDtoResponse getCommentById(Long commentId);

}
