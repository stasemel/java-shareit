package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        User user = mapper.toModel(userCreateDto);
        validate(user);
        return mapper.toDto(repository.save(user));
    }

    @Override
    public UserDto update(UserUpdateDto userUpdateDto) {
        User user = mapper.toModel(userUpdateDto);
        User savedUser = repository.getUserById(user.getId());
        if (savedUser == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", user.getId()));
        }
        validate(user);
        if ((user.getEmail() != null) && (!user.getEmail().isBlank())) {
            savedUser.setEmail(user.getEmail());
        }
        if ((user.getName() != null) && (!user.getName().isBlank())) {
            savedUser.setName(user.getName());
        }
        return mapper.toDto(repository.update(savedUser));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = repository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        }
        return mapper.toDto(user);
    }

    @Override
    public void delete(Long userId) {
        repository.delete(userId);
    }

    private void validate(User user) {
        Optional<Long> optId = repository.getUserIdByEmail(user.getEmail());
        if (optId.isEmpty()) return;
        if (!optId.get().equals(user.getId())) {
            throw new IllegalArgumentException(String.format("Уже есть пользователь с email %s", user.getEmail()));
        }
    }
}
