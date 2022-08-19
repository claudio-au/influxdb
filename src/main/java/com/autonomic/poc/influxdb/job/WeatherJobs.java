package com.autonomic.poc.influxdb.job;

import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.domain.repository.WeatherRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "weather.jobs.enabled", havingValue = "true")
public class WeatherJobs {

  private final WeatherRepository weatherRepository;

  @Scheduled(fixedRate = 1)
  public void createMetrics() {
    log.info("Writing");
    Weather weather = new Weather();
    weather.setTemperature(Math.random() * 100);
    weather.setHumidity(Math.random() * 100);
    weather.setTimestamp(Instant.now());
    weather.setCity("Calgary");
    weather.setCountry("Canada");
    weather.setLatitude(51.0447);
    weather.setLongitude(114.0719);
    weatherRepository.save(weather);
  }
  @Scheduled(fixedRate = 1)
  public void createMetricsToronto() {
    log.info("Writing");
    Weather weather = new Weather();
    weather.setTemperature(Math.random() * 100);
    weather.setHumidity(Math.random() * 100);
    weather.setTimestamp(Instant.now());
    weather.setCity("Toronto");
    weather.setCountry("Canada");
    weather.setLatitude(43.6532);
    weather.setLongitude(79.3832);
    weatherRepository.save(weather);
  }

}
