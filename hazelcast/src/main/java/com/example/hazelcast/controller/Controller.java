package com.example.hazelcast.controller;

import com.example.hazelcast.dto.Customer;
import com.example.hazelcast.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

  private CustomerService customerService;

  public Controller(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/api/customer/{customer}")
  Mono<Customer> getCustomer(@PathVariable String customer) {
    return this.customerService.getCustomer(customer);
  }


}
