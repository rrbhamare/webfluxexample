package com.example.hazelcast.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public interface CacheInterface {

  long defaultTtl = 3;
  TimeUnit defaultUnit = TimeUnit.MINUTES;

  default <T> Mono<T> find(
      IMap<String, HazelcastJsonValue> cache,
      String key,
      Mono<T> fallback,
      Class<T> cls,
      ObjectMapper om,
      Logger log
  ) {

    return find(cache, key, fallback, cls, om, defaultTtl, defaultUnit, log);
  }

  default <T> Mono<T> find(
      IMap<String, HazelcastJsonValue> cache,
      String key,
      Mono<T> fallback,
      Class<T> cls,
      ObjectMapper om, long ttl, TimeUnit unit, Logger log) {

    return getFromCache(cache, key, om, cls)
        .doOnNext(e -> log.info("Found cache data for {} key {}", cache.getName(), key))
        .switchIfEmpty(fallback
            .flatMap(e -> put(cache, key, e, ttl, unit, om, log).thenReturn(e))
        );
  }

  default <T> Mono<T> put(IMap<String, HazelcastJsonValue> cache, String key, T obj,
      long ttl,
      TimeUnit timeUnit, ObjectMapper om, Logger log) {
    return Mono.fromCompletionStage(() -> {

          try {
            String json = om.writeValueAsString(obj);
            log.info("Setting cache with {} key {}", cache.getName(), key);
            return cache.putAsync(key, new HazelcastJsonValue(json), ttl, timeUnit)
                .thenApply(input -> {
                      return obj;
                    }
                );
          } catch (JsonProcessingException e) {
            //TODO think about this.
            throw new RuntimeException(e);
          }
        }
    );
  }

  private <T> Mono<T> getFromCache(IMap<String, HazelcastJsonValue> cache, String key,
      ObjectMapper om, Class<T> cls) {

    return Mono.fromCompletionStage(() ->

        cache.getAsync(key)
            .thenApply(input -> {
              T customer = null;
              if (input != null) {
                try {
                  customer = om.readValue(input.toString(), cls);
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }
              return customer;
            })
    );
  }
}
