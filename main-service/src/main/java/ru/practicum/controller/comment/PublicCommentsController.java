package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDtoResponse;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentsController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public CommentDtoResponse getCommentById(@PathVariable Long id) {

        log.info("GET-запрос на получение комментария с id {}", id);
        return commentService.getCommentById(id);
    }


    @GetMapping("/events/{eventId}")
    public List<CommentDtoResponse> getCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(name = "sort", defaultValue = "true") Boolean isAsc,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос на получение списка комментариев по событию {}, " +
                "группировка сначала новые комментарии: {}", eventId, isAsc);
        return commentService.getCommentsByEventId(eventId, isAsc, from, size);
    }
}
