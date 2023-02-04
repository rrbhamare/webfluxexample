package com.example.demowebflux.controller;

import com.example.demowebflux.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/V1")
@Slf4j
public class InfoController {

  private UserService userService;

  public InfoController(UserService userService) {
    this.userService = userService;
  }


  @GetMapping("/user")
  Mono<String> getUser() {
    log.info("in Get User controller.");
    return userService.getUser();
  }

  @GetMapping("")
  Mono<String> getInfo() {
    log.info("In getInfo");
    Mono<String> ret = Mono.just(getTextForMono());
    log.info("Return from controller.");
    return ret;
  }

  private String getTextForMono() {
    log.info("In getTextForMono");
    return "Rakesh";
  }
}
