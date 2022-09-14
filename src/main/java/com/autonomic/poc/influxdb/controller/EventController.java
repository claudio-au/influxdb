package com.autonomic.poc.influxdb.controller;

import com.autonomic.poc.influxdb.domain.VehicleState;
import com.autonomic.poc.influxdb.dto.EventAlignedDTO;
import com.autonomic.poc.influxdb.dto.EventDTO;
import com.autonomic.poc.influxdb.repository.EventRepository;
import com.autonomic.poc.influxdb.repository.VehicleStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

  private final EventRepository eventRepository;

  @GetMapping("/event")
  public Flux<EventDTO> get(
      @RequestParam(name = "start", required = false, defaultValue = "-1h") String start,
      @RequestParam(name = "stop", required = false, defaultValue = "now()") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit) {
    Flux<EventDTO> response = this.eventRepository.findByDateRange(start, end, limit);

    return response;
  }

  @GetMapping("/event-aligned")
  public Flux<EventAlignedDTO> getAligned(
      @RequestParam(name = "start", required = false, defaultValue = "-1h") String start,
      @RequestParam(name = "stop", required = false, defaultValue = "now()") String end,
      @RequestParam(name = "limit", required = false, defaultValue = "10000") int limit,
      @RequestParam(name = "aggregationInterval", required = false, defaultValue = "10s") String aggregationInterval) {
    Flux<EventAlignedDTO> response = this.eventRepository.findAligned(start, end, limit, aggregationInterval);

    return response;
  }
}
