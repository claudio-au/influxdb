package com.autonomic.poc.influxdb.job;

import com.autonomic.poc.influxdb.domain.VehicleState;
import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.domain.repository.VehicleStateRepository;
import com.autonomic.poc.influxdb.domain.repository.WeatherRepository;
import com.github.javafaker.Faker;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "vehicle_state.jobs.enabled", havingValue = "true")
public class VehicleStateJobs {

  private final VehicleStateRepository vehicleStateRepository;

  @Value("${applications.properties.driver:}")
  private List<String> drivers;

  private Faker faker;
  private Map<String, String> VINs = new HashMap<>();

  private enum PedalStatus {
      DEPRESSING, PRESSING, RELEASED;

      public static PedalStatus random() {
        return PedalStatus.values()[new Random().nextInt(PedalStatus.values().length)];
      }

  }
  @PostConstruct
  public void init() {
    faker = new Faker();
    drivers.stream().parallel().forEach( d -> {
      VINs.put(d, faker.regexify("[A-Z0-9]{17}"));
    });
  }

  public String randomDriver() {
    return drivers.get(new Random().nextInt(drivers.size()));
  }
  @Scheduled(fixedRate = 1)
  public void createMetrics() {
    log.info("VehicleState");
    VehicleState vehicleState = new VehicleState();
    String driver = randomDriver();
    vehicleState.setDriver(driver);
    vehicleState.setVin(VINs.get(driver));
    vehicleState.setLatitude(Double.parseDouble(faker.address().latitude()));
    vehicleState.setLongitude(Double.parseDouble(faker.address().latitude()));
    vehicleState.setHeading(new Random().nextInt(0,100));
    vehicleState.setBrakePedalStatus(PedalStatus.random().name().toLowerCase());
    vehicleState.setEngineSpeed(new Random().nextInt(0, 7200));
    vehicleState.setPersonalOrBusiness("Personal");
    vehicleState.setTimestamp(Instant.now());

    vehicleStateRepository.save(vehicleState);
  }
}
