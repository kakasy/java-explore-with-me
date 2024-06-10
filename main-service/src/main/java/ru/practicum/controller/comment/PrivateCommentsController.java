package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDtoResponse;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentsController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse createComment(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @Valid @RequestBody NewCommentDto creatingDto) {

        log.info("POST-запрос на добавление нового комментария {} от пользователя {} к событию {}",
                creatingDto, userId, eventId);
        return commentService.createComment(userId, eventId, creatingDto);
    }

    @PatchMapping("/{comId}")
    public CommentDtoResponse updateCommentById(@PathVariable Long userId,
                                                @PathVariable Long comId,
                                                @RequestBody @Valid NewCommentDto updatingDto) {

        log.info("PATCH-запрос на изменение комментария {} от пользователя {}: {}", comId, userId, updatingDto);
        return commentService.updateCommentById(userId, comId, updatingDto);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long comId) {

        log.info("DELETE-запрос на удаление комментария с id: {}", comId);
        commentService.deleteComment(userId, comId);
    }
}
