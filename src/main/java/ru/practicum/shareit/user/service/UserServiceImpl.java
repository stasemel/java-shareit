package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto create(UserCreateDto userCreateDto) {
        User user = mapper.toModel(userCreateDto);
        validate(user);
        return mapper.toDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UserUpdateDto userUpdateDto) {
        User user = mapper.toModel(userUpdateDto);
        Optional<User> optUser = repository.getUserById(user.getId());

        if (optUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", user.getId()));
        }
        User savedUser = optUser.get();
        validate(user);
        if ((user.getEmail() != null) && (!user.getEmail().isBlank())) {
            savedUser.setEmail(user.getEmail());
        }
        if ((user.getName() != null) && (!user.getName().isBlank())) {
            savedUser.setName(user.getName());
        }
        return mapper.toDto(repository.save(savedUser));
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> optUser = repository.getUserById(userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        }
        User user = optUser.get();
        return mapper.toDto(user);
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    private void validate(User user) {
        Optional<User> optUser = repository.findUserByEmailIgnoreCase(user.getEmail());
        if (optUser.isEmpty()) return;
        if (!optUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException(String.format("Уже есть пользователь с email %s", user.getEmail()));
        }
    }
}
