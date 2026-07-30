package com.tracek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class TraceKApplication {

  public static void main(String[] args) {
    SpringApplication.run(TraceKApplication.class, args);
  }

}
