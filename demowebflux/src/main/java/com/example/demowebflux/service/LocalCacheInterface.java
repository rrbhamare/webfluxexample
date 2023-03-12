package com.example.demowebflux.service;

import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Mono;

public interface LocalCacheInterface {

  <T> Mono<T> find(String cacheName, String key, Mono<T> fallback);

  <T> Mono<T> find(String name, String key, Mono<T> fallback, long ttl, TimeUnit unit);

  Mono<Boolean> evict(String name, String key);

}
