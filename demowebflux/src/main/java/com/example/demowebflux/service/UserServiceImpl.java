package com.example.demowebflux.service;

import com.example.demowebflux.integration.UserIntegration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

  private UserIntegration userIntegration;

  public UserServiceImpl(UserIntegration userIntegration) {
    this.userIntegration = userIntegration;
  }

  @Override
  public Mono<String> getUser() {
    return userIntegration.getUser();
  }
}
