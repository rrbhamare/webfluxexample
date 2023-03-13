package com.example.hazelcast.service;

import com.example.hazelcast.dto.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {

  Mono<Customer> getCustomer(String name);

}
