package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<Long> getUserIdByEmail(String email);

    User update(User user);

    User getUserById(Long id);

    void delete(Long userId);
}
