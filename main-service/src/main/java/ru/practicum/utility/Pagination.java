package ru.practicum.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class Pagination {

    public static Pageable withoutSort(Integer from, Integer size) {

        return PageRequest.of(from / size, size);
    }
}
