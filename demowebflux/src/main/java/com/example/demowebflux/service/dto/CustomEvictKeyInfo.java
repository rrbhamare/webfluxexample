package com.example.demowebflux.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomEvictKeyInfo {

  private String cacheName;
  private String keyName;
}

