package com.fitness.UserService.service;

import com.fitness.UserService.dto.RegisterRequest;
import com.fitness.UserService.dto.UserResponse;
import jakarta.validation.Valid;

public interface UserService {

    public UserResponse register(@Valid RegisterRequest request);

    public UserResponse getUserProfile(String userId);

    public Boolean existsByKeycloakId(String userId);
}
