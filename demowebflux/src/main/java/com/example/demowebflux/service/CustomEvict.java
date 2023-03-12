package com.example.demowebflux.service;

import com.example.demowebflux.service.dto.CustomEvictKeyInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class CustomEvict {

  private static final long MINUTE_SCALE = 60L * 1000L;
  private static Map<String, Map<String, Long>> trackEvictMap = new HashMap<>();

  private LocalCacheInterface localCacheInterface;

  public CustomEvict(LocalCacheInterface localCacheInterface) {
    this.localCacheInterface = localCacheInterface;
  }

  private static long calculateExpireAt(long ttl, TimeUnit unit) {

    long currentTime = System.currentTimeMillis();

    if (unit == TimeUnit.MINUTES) {
      return (ttl * MINUTE_SCALE) + currentTime;
    }
    return ttl + currentTime;
  }

  public static void addEvictTracking(String cacheName, String key, long ttl, TimeUnit unit) {
    // For now given support only for Minutes.
    if (unit == TimeUnit.MINUTES) {
      long expireAt = calculateExpireAt(ttl, unit);
      if (trackEvictMap.containsKey(cacheName)) {
        trackEvictMap.get(cacheName).put(key, expireAt);
      } else {
        Map<String, Long> entry = new HashMap<>();
        entry.put(key, expireAt);
        trackEvictMap.put(cacheName, entry);
      }
    }
  }

  @Scheduled(fixedRate = 11, timeUnit = TimeUnit.MINUTES, initialDelay = 10)
  public void removeCache() {
    log.info("Started cron job");

    List<CustomEvictKeyInfo> customEvictKeyInfo = new ArrayList<>();
    trackEvictMap
        .forEach((cacheName, cacheValue) -> {
          if (cacheValue != null) {
            cacheValue
                .forEach((cacheNameKey, timeToLive) -> {
                  log.info(" Current {} and Expire at {}", System.currentTimeMillis(), timeToLive);
                  if (System.currentTimeMillis() > timeToLive) {
                    log.info("We need to clear this cache --> Cache {} with key {}", cacheName,
                        cacheNameKey);
                    customEvictKeyInfo.add(new CustomEvictKeyInfo(cacheName, cacheNameKey));
                  }
                });
          }
        });

    //TODO remove this log entry.
    log.info("These many entry need to delete {}", customEvictKeyInfo);
    log.info("WE have these many entry for tracking {}", trackEvictMap);

    Flux.fromIterable(customEvictKeyInfo)
        .map(e -> {
          removeFromTracking(e.getCacheName(), e.getKeyName());
          return e;
        })
        .flatMap(e -> localCacheInterface.evict(e.getCacheName(), e.getKeyName()))
        .subscribe(e -> {
          log.info("Evict keys");
        });
  }

  private void removeFromTracking(String cacheName, String key) {
    log.info("In removeFromTracking -->");
    if (trackEvictMap.containsKey(cacheName)) {
      if (trackEvictMap.get(cacheName).containsKey(key)) {
        trackEvictMap.get(cacheName).remove(key);
      }
    }
  }

}
