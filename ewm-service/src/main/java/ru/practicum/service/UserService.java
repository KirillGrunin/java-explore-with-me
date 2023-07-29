package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    List<UserDto> getUsers(PageRequest page);

    List<UserDto> getUsersById(List<Long> ids);

    void deleteUser(Long userId);
}