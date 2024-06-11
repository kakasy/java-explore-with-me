package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDtoResponse;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.entity.Comment;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.enums.EventStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.service.CommentService;
import ru.practicum.storage.CommentRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.UserRepository;
import ru.practicum.utility.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    private static final String UNCHANGED_COMMENT = "Комментарий не содержит изменений";

    @Override
    public CommentDtoResponse createComment(Long userId, Long eventId, NewCommentDto creatingDto) {

        User author = checkUser(userId);
        checkEvent(eventId);

        Comment comment = CommentMapper.toComment(creatingDto, eventId, author);
        Comment createdComment = commentRepository.save(comment);

        log.info("Создан новый комментарий: {}", createdComment);
        return CommentMapper.toCommentResponse(createdComment);
    }

    @Override
    public CommentDtoResponse updateCommentById(Long userId, Long commentId, NewCommentDto updatingDto) {

        Comment oldComment = checkComment(commentId);

        if (!Objects.equals(userId, oldComment.getAuthor().getId())) {
            throw new ConflictException(String.format("Пользователь c id:{} не является автором комментария", userId));
        }
        if (LocalDateTime.now().isAfter(oldComment.getCreated().plusHours(1))) {
            throw new ConflictException("Время для изменения комментария вышло");
        }
        if (oldComment.getText().equals(updatingDto.getText())) {
            log.info(UNCHANGED_COMMENT);
            return CommentMapper.toCommentResponse(oldComment);
        }

        oldComment.setText(updatingDto.getText());
        Comment newComment = commentRepository.save(oldComment);

        log.info("Комментарий изменен: {}", newComment);
        return CommentMapper.toCommentResponse(newComment);
    }

    @Override
    public CommentDtoResponse moderateCommentById(Long commentId, NewCommentDto updatingDto) {

        Comment oldComment = checkComment(commentId);

        if (oldComment.getText().equals(updatingDto.getText())) {
            log.info(UNCHANGED_COMMENT);
            return CommentMapper.toCommentResponse(oldComment);
        }

        oldComment.setText(updatingDto.getText());
        Comment newComment = commentRepository.save(oldComment);

        log.info("Комментарий изменен: {}", newComment);
        return CommentMapper.toCommentResponse(newComment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

        Comment comment = checkComment(commentId);

        if (!Objects.equals(userId, comment.getAuthor().getId())) {
            throw new ConflictException(String.format("Пользователь с id: {} не является автором комментария", userId));
        }
        if (LocalDateTime.now().isAfter(comment.getCreated().plusHours(1))) {
            throw new ConflictException("Время для изменения комментария вышло");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id {} удален", commentId);

    }

    @Override
    public void adminDeleteComment(Long commentId) {

        checkComment(commentId);

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id {} удален", commentId);

    }

    @Override
    public List<CommentDtoResponse> getCommentsByEventId(Long eventId, Boolean isAsc, Integer from, Integer size) {

        checkEvent(eventId);

        Pageable page;

        if (isAsc) {
            page = Pagination.withSort(from, size, Sort.by(Sort.Direction.ASC, "created"));
        } else {
            page = Pagination.withSort(from, size, Sort.by(Sort.Direction.DESC, "created"));
        }

        List<Comment> comments = commentRepository.getAllByEventId(eventId, page);

        log.info("Комментарии события: {}", comments.size());
        return comments.stream()
                .map(CommentMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse getCommentById(Long commentId) {

        Comment comment = checkComment(commentId);

        return CommentMapper.toCommentResponse(comment);
    }

    private User checkUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с id %d не существует", userId)));
    }

    private Comment checkComment(Long commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Комментария с id %d не существует", commentId)));
    }

    private Event checkEvent(Long eventId) {

        return eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Событие с id %d не существует/не опубликовано", eventId)));
    }
}
