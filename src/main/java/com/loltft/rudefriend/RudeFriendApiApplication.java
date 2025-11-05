package com.loltft.rudefriend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan("com.loltft.rudefriend")
public class RudeFriendApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RudeFriendApiApplication.class, args);
  }
}
