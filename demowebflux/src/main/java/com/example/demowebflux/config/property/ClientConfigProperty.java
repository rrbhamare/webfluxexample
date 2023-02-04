package com.example.demowebflux.config.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class ClientConfigProperty {

  protected String url;
  protected String user;
  protected String password;
  protected Integer connectionTimeout;
  protected Integer readTimeout;
}
