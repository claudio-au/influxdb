package com.autonomic.poc.influxdb.controller;

import com.autonomic.poc.influxdb.domain.VehicleState;
import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.repository.VehicleStateRepository;
import com.autonomic.poc.influxdb.repository.WeatherRepository;
import com.influxdb.query.FluxRecord;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VehicleStateController {

  private final VehicleStateRepository vehicleStateRepository;

  @GetMapping("/vehicle-state")
  public Flux<VehicleState> get(
      @RequestParam(name = "start", required = false, defaultValue = "-1h") String start,
      @RequestParam(name = "stop", required = false, defaultValue = "now()") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit) {
    Flux<VehicleState> response = this.vehicleStateRepository
        .findByDateRange(start, end, limit);

    return response;
  }
}
