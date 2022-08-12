package com.autonomic.poc.influxdb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@RestController
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class InfluxdbApplication {

  public static void main(String[] args) {
    SpringApplication.run(InfluxdbApplication.class, args);
  }


}
