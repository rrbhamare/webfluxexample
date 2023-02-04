package com.example.demowebflux.integration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Service
public class UserIntegrationImpl implements UserIntegration {

  private WebClient webClientIntegration;

  public UserIntegrationImpl(WebClient webClientIntegration) {
    this.webClientIntegration = webClientIntegration;
  }

  @Override
  public Mono<String> getUser() {
    return webClientIntegration
        .get()
        .uri("/rest/user")
        .retrieve()
        .bodyToMono(String.class);
  }
}
