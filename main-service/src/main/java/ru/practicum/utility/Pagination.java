package ru.practicum.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Pagination {

    public static Pageable withoutSort(Integer from, Integer size) {

        return PageRequest.of(from / size, size);
    }

    public static Pageable withSort(Integer from, Integer size, Sort sort) {

        return PageRequest.of(from / size, size, sort);
    }
}
