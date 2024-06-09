package ru.practicum.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUsersController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getUsersByParam(@RequestParam(required = false) List<Long> ids,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос на получение списка пользователей: id - {}, количество пропущенных - {}, " +
                        "элементов в наборе - {}", ids, from, size);
        return service.getUsersByParam(ids, from, size);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest creatingDto) {

        log.info("POST-запрос на создание нового пользователя: {}", creatingDto);
        return service.createUser(creatingDto);
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE-запрос на удаление пользователя с id: {}", userId);
        service.deleteUser(userId);
    }
}