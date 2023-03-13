package com.example.hazelcast.config.hazel;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelConfig {

  private final String customerCacheName = "customer";

/*
  @Bean
  public IMap<String, Customer> cache(HazelcastInstance instance) {
    return instance.getMap("customer");
  }

 */

  @Bean
  public IMap<String, HazelcastJsonValue> customerCache(HazelcastInstance instance) {
    return instance.getMap(customerCacheName);
  }

  @Bean
  public HazelcastInstance hazelcastInstance() {
    Config config = Config.load();

    /*
    // Central config or you can put IMap put tttl
    MapConfig customerMapConfig = new MapConfig();
    customerMapConfig.setTimeToLiveSeconds(30);
    config.getMapConfigs().put("customer", customerMapConfig);
     */
    return Hazelcast.newHazelcastInstance(config);

  }
}
