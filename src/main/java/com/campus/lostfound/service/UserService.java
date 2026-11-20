package com.campus.lostfound.service;

import com.campus.lostfound.dto.UserRegistrationDto;
import com.campus.lostfound.entity.User;

import java.util.List;

public interface UserService {
    User registerNewUser(UserRegistrationDto dto);
    User findByEmail(String email);
    User findById(Long id);
    List<User> findAllUsers();
    void toggleUserStatus(Long userId);
    long getTotalUsersCount();
}
