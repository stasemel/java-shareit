package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService service;
    private final EntityManager em;

    @Test
    void create() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");

        service.create(userCreateDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userCreateDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userCreateDto.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void createWithRepeatedEmailMustThrownException() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        service.create(userCreateDto);
        UserCreateDto userCreateDtoRepeat = new UserCreateDto("User1 Useroff1", "useroff@yandex.ru");

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.create(userCreateDtoRepeat));
    }

    @Test
    void update() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        UserDto userDto = service.create(userCreateDto);
        UserUpdateDto userUpdateDto = new UserUpdateDto(userDto.getId(), "Yandex Yandexoff", "");

        service.update(userUpdateDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userUpdateDto.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void getUserById() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        UserDto userDto = service.create(userCreateDto);

        UserDto user = service.getUserById(userDto.getId());

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void delete() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        UserDto userDto = service.create(userCreateDto);

        service.delete(userDto.getId());

        Assertions.assertThrows(NoResultException.class, () -> {
                    TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
                    query.setParameter("id", userDto.getId()).getSingleResult();
                }
        );
    }
}