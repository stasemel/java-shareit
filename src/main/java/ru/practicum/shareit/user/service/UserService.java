package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);

    UserDto update(UserUpdateDto userUpdateDto);

    UserDto getUserById(Long userId);

    void delete(Long userId);
}
