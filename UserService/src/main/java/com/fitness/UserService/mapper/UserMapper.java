package com.fitness.UserService.mapper;

import com.fitness.UserService.dto.RegisterRequest;
import com.fitness.UserService.dto.UserResponse;
import com.fitness.UserService.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User userMapper(RegisterRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return user;
    }

    public UserResponse userResponseMapper(User savedUser){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        userResponse.setRole(savedUser.getRole());
        return userResponse;
    }
}
