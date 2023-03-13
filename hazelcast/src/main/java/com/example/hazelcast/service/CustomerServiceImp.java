package com.example.hazelcast.service;

import com.example.hazelcast.dto.Customer;
import com.example.hazelcast.dto.CustomerDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerServiceImp implements CustomerService, CacheInterface {

  private final IMap<String, HazelcastJsonValue> customerIMap;
  private ObjectMapper objectMapper;

  public CustomerServiceImp(IMap<String, HazelcastJsonValue> customerIMap,
      ObjectMapper objectMapper) {
    this.customerIMap = customerIMap;
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Customer> getCustomer(String name) {

    Mono<Customer> result = Mono.defer(() -> getLocalCustomer(name));
    return find(customerIMap, name, result, Customer.class, objectMapper, log);
  }


  // Refer CacheInterface below is just sample one
  private Mono<Customer> getFromCache(IMap<String, HazelcastJsonValue> cache, String key,
      ObjectMapper om) {

    return Mono.fromCompletionStage(() ->
    {
      return cache.getAsync(key)
          .thenApply(input -> {
            Customer customer = null;
            if (input != null) {
              try {
                customer = om.readValue(input.toString(), Customer.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            }
            return customer;
          });
    });
  }

  // Refer CacheInterface below is just sample one
  private Mono<Customer> put(IMap<String, HazelcastJsonValue> cache, String key, Customer obj,
      long ttl,
      TimeUnit timeUnit, ObjectMapper om) {
    return Mono.fromCompletionStage(() -> {

          try {
            String json = om.writeValueAsString(obj);
            return cache.putAsync(key, new HazelcastJsonValue(json), ttl, timeUnit)
                .thenApply(input -> {
                      return obj;
                    }
                );
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }


  public Mono<Customer> getLocalCustomer(String name) {
    log.info("Calling backend with  {}", name);
    CustomerDetails customerDetails = new CustomerDetails();
    customerDetails.setAddress("Input Address");
    List<CustomerDetails> list = new ArrayList<CustomerDetails>();
    list.add(customerDetails);
    return Mono.just(Customer.builder()
        .name(name)
        .detailsList(list)
        .build());
  }
}
