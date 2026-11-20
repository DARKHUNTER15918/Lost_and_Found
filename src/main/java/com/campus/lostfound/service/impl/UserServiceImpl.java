package com.campus.lostfound.service.impl;

import com.campus.lostfound.dto.UserRegistrationDto;
import com.campus.lostfound.entity.Role;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with email " + dto.getEmail() + " already exists.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStudentId(dto.getStudentId() != null ? dto.getStudentId().trim() : null);
        user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = findById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }
}
