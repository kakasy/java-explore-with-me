package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoResponse {

    Long id;
    String text;
    Long eventId;
    UserShortDto author;
    LocalDateTime created;
}
