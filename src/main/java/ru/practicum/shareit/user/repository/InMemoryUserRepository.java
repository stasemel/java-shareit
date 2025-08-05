package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private static Long ID_COUNT = 0L;

    private Long getNextId() {
        return (++ID_COUNT);
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<Long> getUserIdByEmail(String email) {
        List<User> list = users.values().stream().filter(user -> user.getEmail().equals(email)).toList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.getFirst().getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }
}
