package com.example.hazelcast.config.hazel;

import com.example.hazelcast.config.hazel.serializer.CustomerSerializer;
import com.example.hazelcast.dto.Customer;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class CacheClient {

  public static final String CUSTOMER = "customer";

  private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(
      createConfig());

  public Customer put(String number, Customer customer) {
    IMap<String, Customer> map = hazelcastInstance.getMap(CUSTOMER);
    return map.putIfAbsent(number, customer);
  }

  public CompletionStage<Customer> putAsync(String key, Customer customer) {
    log.info("in putAsync");
    IMap<String, Customer> map = hazelcastInstance.getMap(CUSTOMER);
    return map.putAsync(key, customer);
  }

  public Customer get(String key) {
    IMap<String, Customer> map = hazelcastInstance.getMap(CUSTOMER);
    return map.get(key);
  }

  public CompletionStage<Customer> getAsync(String key) {
    log.info("in getAsync");
    IMap<String, Customer> map = hazelcastInstance.getMap(CUSTOMER);
    return map.getAsync(key);
  }

  public Config createConfig() {
    Config config = new Config();
    config.addMapConfig(mapConfig());
    config.getSerializationConfig().addSerializerConfig(serializerConfig());
    return config;
  }

  private SerializerConfig serializerConfig() {
    return new SerializerConfig()
        .setImplementation(new CustomerSerializer())
        .setTypeClass(Customer.class);
  }

  private MapConfig mapConfig() {
    MapConfig mapConfig = new MapConfig(CUSTOMER);
    mapConfig.setTimeToLiveSeconds(30);
    mapConfig.setMaxIdleSeconds(20);
    return mapConfig;
  }
}
