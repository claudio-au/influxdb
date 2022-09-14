package com.autonomic.poc.influxdb.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.InfluxDBClientReactiveFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@Setter
public class InfluxDBConfig {

  @Value("${spring.influxdb.host}")
  private String host;
  @Value("${spring.influxdb.token}")
  private char[] token;
  @Value("${spring.influxdb.org}")
  private String org;
  @Value("${spring.influxdb.bucket}")
  private String bucket;

  @Bean
  public InfluxDBClientReactive createInfluxConnection() {
    System.out.println("Creating influx connection");
    InfluxDBClientReactive client = InfluxDBClientReactiveFactory
        .create(this.getHost(), this.getToken(), this.getOrg(), this.getBucket());

    return client;
  }

  @Bean
  public InfluxDBClient createSyncInfluxConnection() {
    InfluxDBClient client = InfluxDBClientFactory
        .create(this.getHost(), this.getToken(), this.getOrg(), this.getBucket());
    return client;
  }
}
