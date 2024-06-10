package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.service.UserService;
import ru.practicum.storage.UserRepository;
import ru.practicum.utility.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByParam(List<Long> ids, Integer from, Integer size) {

        List<UserDto> foundUsers;

        Pageable page = Pagination.withoutSort(from, size);

        if (ids == null) {
            foundUsers = userRepository.findAll(page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {

            foundUsers = userRepository.findAllByIdIn(ids, page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        log.info("Список из {} пользователей", foundUsers.size());

        return foundUsers;
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {

        User savedUser = userRepository.save(UserMapper.toUser(newUserRequest));
        log.info("Новый пользователь сохранен с id {}", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не существует", userId));
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удален", userId);
    }

}
