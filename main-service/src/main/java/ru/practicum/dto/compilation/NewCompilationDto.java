package ru.practicum.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    Set<Long> events = new HashSet<>();

    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    String title;
}
