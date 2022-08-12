package com.autonomic.poc.influxdb.controller;

import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.domain.repository.WeatherRepository;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistoricalController {

  private final WeatherRepository weatherRepository;

  @GetMapping("/historic")
  public Flux<Weather> get(
      @RequestParam(name = "start", required = false, defaultValue = "-1h") String start,
      @RequestParam(name = "stop", required = false, defaultValue = "now()") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit) {
    Flux<Weather> response = this.weatherRepository
        .findByDateRange(start, end, limit);

    return response;
  }

  @GetMapping("/query2")
  public Flux<Weather> get2(
      @RequestParam(name = "start") String start,
      @RequestParam(name = "stop") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit) {
    Flux<Weather> response = this.weatherRepository
        .findByRange(
            Instant.parse(start).atZone(ZoneId.of("UTC")).toInstant(),
            Instant.parse(end).atZone(ZoneId.of("UTC")).toInstant(),
            limit
        );

    return response;
  }

  @GetMapping("/query-string")
  public Flux<String> getRaw(
      @RequestParam(name = "start", required = false, defaultValue = "-1h") String start,
      @RequestParam(name = "stop", required = false, defaultValue = "now()") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit) {
    Flux<String> response = this.weatherRepository
        .findRawByDateRange(start, end, limit);

    return response;
  }
}
