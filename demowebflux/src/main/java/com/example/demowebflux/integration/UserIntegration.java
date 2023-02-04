package com.example.demowebflux.integration;

import reactor.core.publisher.Mono;

public interface UserIntegration {
  Mono<String> getUser();
}
