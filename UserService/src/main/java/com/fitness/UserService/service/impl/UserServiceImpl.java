package com.fitness.UserService.service.impl;

import com.fitness.UserService.dto.RegisterRequest;
import com.fitness.UserService.dto.UserResponse;
import com.fitness.UserService.mapper.UserMapper;
import com.fitness.UserService.model.User;
import com.fitness.UserService.repository.UserRepository;
import com.fitness.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email Already Exists");
        }
        User user = userMapper.userMapper(request);
        User savedUser = userRepository.save(user);
        UserResponse userResponse = userMapper.userResponseMapper(savedUser);
        return userResponse;
    }

    @Override
    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("USER not found"));
        return userMapper.userResponseMapper(user);
    }
}
