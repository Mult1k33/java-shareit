package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserValidate;

import java.util.Collection;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
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
    public UserDto create(UserNewDto userNewDto) {
        UserValidate.validateForCreate(userNewDto);
        checkUniqueEmail(userNewDto.getEmail());
        User user = UserMapper.mapToUser(userNewDto);
        User createdUser = userRepository.save(user);
        return UserMapper.mapToDto(createdUser);
    }

    // Обновление пользователя
    @Override
    public UserDto update(Long userId, UserNewDto userNewDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        UserValidate.validateForUpdate(userNewDto);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        if (userNewDto.getName() != null) {
            existingUser.setName(userNewDto.getName());
        }

        if (userNewDto.getEmail() != null && !userNewDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            checkUniqueEmail(userNewDto.getEmail());
            existingUser.setEmail(userNewDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.mapToDto(updatedUser);
    }

    // Удаление пользователя
    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
           throw new NotFoundException("Пользователь с Id:" + userId + "не найден");
        }

        userRepository.deleteById(userId);
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