package com.example.demowebflux.service;

import reactor.core.publisher.Mono;

public interface UserService {

  Mono<String> getUser();

}
