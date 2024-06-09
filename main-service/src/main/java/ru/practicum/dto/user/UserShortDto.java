package ru.practicum.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {

    Long id;
    String name;
}
