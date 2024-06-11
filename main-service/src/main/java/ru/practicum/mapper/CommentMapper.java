package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.comment.CommentDtoResponse;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.entity.Comment;
import ru.practicum.entity.User;

@UtilityClass
public class CommentMapper {

    public Comment toComment(NewCommentDto creatingDto, Long eventId, User user) {
        return Comment.builder()
                .text(creatingDto.getText())
                .author(user)
                .eventId(eventId)
                .build();
    }

    public CommentDtoResponse toCommentResponse(Comment createdComment) {
        return CommentDtoResponse.builder()
                .id(createdComment.getId())
                .text(createdComment.getText())
                .eventId(createdComment.getEventId())
                .author(UserMapper.toUserShortDto(createdComment.getAuthor()))
                .created(createdComment.getCreated())
                .build();
    }
}
