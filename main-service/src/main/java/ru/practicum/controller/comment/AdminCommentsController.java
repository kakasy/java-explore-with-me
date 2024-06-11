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
@RequestMapping(path = "/admin/comments")
public class AdminCommentsController {

    private final CommentService commentService;

    @PatchMapping("/{comId}")
    public CommentDtoResponse moderateCommentById(@PathVariable Long comId,
                                                  @RequestBody @Valid NewCommentDto updatingDto) {

        log.info("PATCH-запрос на модерацию комментария {}: {}", comId, updatingDto);
        return commentService.moderateCommentById(comId, updatingDto);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void admDeleteComment(@PathVariable Long comId) {
        log.info("DELETE-запрос на удаление комментария с id: {}", comId);
        commentService.adminDeleteComment(comId);
    }
}
