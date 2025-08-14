package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserValidate;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Получение всех пользователей
    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    // Получение пользователя по id
    @Override
    public UserDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    // Добавление пользователя
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        UserValidate.validateForCreate(user);
        checkUniqueEmail(userDto.getEmail());

        User createdUser = userRepository.create(user);
        return UserMapper.mapToDto(createdUser);
    }

    // Обновление пользователя
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        if (userDto == null) {
            throw new ValidationException("Пользователь не может быть null");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            checkUniqueEmail(userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }

        UserValidate.validateForEdit(existingUser);

        User updatedUser = userRepository.update(userId, existingUser);
        return UserMapper.mapToDto(updatedUser);
    }

    // Удаление пользователя
    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    // Проверки уникальности email
    private void checkUniqueEmail(String email) {
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));

        if (emailExists) {
            throw new DuplicateException("Email " + email + " уже занят");
        }
    }
}