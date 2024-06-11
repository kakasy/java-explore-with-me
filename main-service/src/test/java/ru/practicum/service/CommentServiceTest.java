package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.entity.Comment;
import ru.practicum.entity.User;
import ru.practicum.exception.ConflictException;
import ru.practicum.service.impl.CommentServiceImpl;
import ru.practicum.storage.CommentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    private Comment testComment;

    @BeforeEach
    void startUp() {
        testComment = Comment.builder()
                .id(1L)
                .text("test comment")
                .created(LocalDateTime.now().minusHours(1))
                .eventId(1L)
                .author(User.builder()
                        .id(1L)
                        .name("Dude01")
                        .email("mikey@turtle.com")
                        .build())
                .build();
    }

    @Test
    void updateCommentById_whenTimeIsOver_thenReturnError() {

        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(testComment));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> commentService.updateCommentById(1L, 1L, any(NewCommentDto.class)));

        assertEquals(String.format("Время для изменения комментария вышло"), exception.getMessage());
        verify(commentRepository, times(0)).save(testComment);
    }

    @Test
    void deleteComment_whenTimeIsOver_thenReturnError() {

        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(testComment));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> commentService.deleteComment(1L, 1L));

        assertEquals(String.format("Время для изменения комментария вышло"), exception.getMessage());
        verify(commentRepository, times(0)).deleteById(testComment.getId());
    }
}