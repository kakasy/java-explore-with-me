package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatsDto {

    String app;

    String uri;

    Long hits;

}
