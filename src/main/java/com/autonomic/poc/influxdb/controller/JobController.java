package com.autonomic.poc.influxdb.controller;

import com.autonomic.poc.influxdb.job.WeatherJobs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

  private final ScheduledAnnotationBeanPostProcessor postProcessor;
  @Nullable
  private final WeatherJobs weatherJobs;
  private final ObjectMapper objectMapper;

  @GetMapping(value = "/start")
  public String start(@RequestParam(name="task", defaultValue = "createMetrics") String task) {
    this.postProcessor.postProcessAfterInitialization(weatherJobs, task);
    return "Running Job - createMetrics";
  }

  @GetMapping(value = "/stop")
  public String stop(@RequestParam(name="task", defaultValue = "createMetrics") String task) {
    this.postProcessor.postProcessBeforeDestruction(weatherJobs, task);
    return "Stopping Job - createMetrics";
  }

  @GetMapping(value = "/")
  public String listSchedules() throws JsonProcessingException {
    Set<ScheduledTask> setTasks = postProcessor.getScheduledTasks();
    if (!setTasks.isEmpty()) {
      return setTasks.toString();
    } else {
      return "No running tasks !";
    }
  }
}

