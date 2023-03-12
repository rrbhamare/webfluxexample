package com.example.demowebflux.service;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LocalCacheInterfaceImp implements LocalCacheInterface {

  private final CacheManager cacheManager;

  public LocalCacheInterfaceImp(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Override
  public Mono<Boolean> evict(String name, String key) {
    return Mono.create(sink -> {
      Cache cache = this.cacheManager.getCache(name);
      Boolean isSucess = Boolean.FALSE;
      if (cache != null) {
        cache.evict(key);
        isSucess = Boolean.TRUE;
      }
      sink.success(isSucess);
    });
  }

  @Override
  public <T> Mono<T> find(String name, String key, Mono<T> fallback, long ttl, TimeUnit unit) {
    return getFromCache(name, key)
        .switchIfEmpty(fallback)
        .flatMap(e -> fastPut(name, key, e, ttl, unit)
            .thenReturn((T) e));
  }

  @Override
  public <T> Mono<T> find(String cacheName, String key, Mono<T> fallback) {
    return find(cacheName, key, fallback, 10, TimeUnit.MINUTES);
  }

  private <T> Mono<T> getFromCache(String cacheName, String key) {
    return Mono.create(sink -> {
      T retrunObj = null;
      Cache cache = this.cacheManager.getCache(cacheName);
      if (cache != null) {
        ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
          retrunObj = (T) valueWrapper.get();
        }
      }
      if (retrunObj != null) {
        log.info("Found data for cache Name {} with key {}",cacheName, key);
        sink.success(retrunObj);
      } else {
        log.info("We do not have data in cache for key {}", key);
        sink.success();
      }
    });
  }

  private <T> Mono<Boolean> fastPut(String name, String key, T value, long ttl, TimeUnit unit) {
    return Mono.create(sink -> {
      Cache cache = this.cacheManager.getCache(name);
      if (cache != null) {
        cache.put(key, value);
        // Add in case you would like to remove it by own.
        CustomEvict.addEvictTracking(name, key, ttl, unit);
        sink.success(true);
      } else {
        sink.success();
      }
    });
  }

}
