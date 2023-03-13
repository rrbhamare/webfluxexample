package com.example.hazelcast.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer implements Serializable {

  private String name;
  private String lastName;
  private List<CustomerDetails> detailsList;
}
