package com.example.demowebflux.controller;

import com.example.demowebflux.dto.graphql.UserGql;
import com.example.demowebflux.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class QueryController {

  private UserService userService;

  public QueryController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping("getUser")
  Mono<UserGql> getUser() {
    log.info("In main query getUser");
    return Mono.just(new UserGql());
  }

  @SchemaMapping(typeName = "User", field = "name")
  Mono<String> getName(UserGql user) {
    log.info("In resolver user -> getName");
    return userService.getUser();
  }

  @SchemaMapping(typeName = "User", field = "lastName")
  Mono<String> getLastName(UserGql user) {
    log.info("In resolver user -> lastName");
    return userService.getUser();
  }

}
