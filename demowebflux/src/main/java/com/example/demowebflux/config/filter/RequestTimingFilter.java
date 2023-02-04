package com.example.demowebflux.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * To log the total time taken to response to UI.
 * TODO check how this will work in case of produce type of stream.
 */
@Component
@Slf4j
public class RequestTimingFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    long startTime = System.currentTimeMillis();
    log.info("In Web Request filter");
    return chain.filter(exchange)
        .doOnSuccess(aVoid -> logResponseTime(exchange, startTime))
        .doOnError(aVoid -> logResponseTime(exchange, startTime));
  }

  private void logResponseTime(ServerWebExchange exchange, long startTime) {
    log.info("Response  {} ms for {} {}", System.currentTimeMillis() - startTime,
        exchange.getRequest().getMethod(), exchange.getRequest().getPath().value());
  }
}
