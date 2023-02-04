package com.example.demowebflux.config.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "demo.client.integration")
public class IntegrationClientProperty extends ClientConfigProperty {

}
