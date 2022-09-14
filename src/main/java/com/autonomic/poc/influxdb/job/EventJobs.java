package com.autonomic.poc.influxdb.job;

import com.autonomic.poc.influxdb.domain.BatteryVoltageState;
import com.autonomic.poc.influxdb.domain.BatteryVoltageState.BatteryVoltage;
import com.autonomic.poc.influxdb.domain.FuelLevelState;
import com.autonomic.poc.influxdb.domain.IgnitionState;
import com.autonomic.poc.influxdb.domain.IgnitionState.Ignition;
import com.autonomic.poc.influxdb.domain.OdometerState;
import com.autonomic.poc.influxdb.domain.TirePressureState;
import com.autonomic.poc.influxdb.domain.TirePressureState.TirePressure;
import com.autonomic.poc.influxdb.repository.EventRepository;
import com.github.javafaker.Faker;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "state.jobs.enabled", havingValue = "true")
public class EventJobs {

  private final EventRepository eventRepository;

  @Scheduled(fixedRate = 1000)
  public void createMetrics() throws InterruptedException {
    String vin = "ABC1234";
    Faker faker = new Faker();
    Double longitude =Double.parseDouble(faker.address().longitude());
    Double latitude =Double.parseDouble(faker.address().latitude());
    log.info("Writing");
    BatteryVoltageState batteryVoltageState = new BatteryVoltageState();
    batteryVoltageState.setValue(BatteryVoltage.getRandom());
    batteryVoltageState.setTimestamp(Instant.now());
    batteryVoltageState.setLatitude(latitude);
    batteryVoltageState.setLongitude(longitude);
    batteryVoltageState.setVin(vin);

    eventRepository.save(batteryVoltageState);

    Thread.sleep(200);
    FuelLevelState fuelLevelState = new FuelLevelState();
    fuelLevelState.setLatitude(latitude);
    fuelLevelState.setLongitude(longitude);
    fuelLevelState.setTimestamp(Instant.now());
    fuelLevelState.setVin(vin);
    fuelLevelState.setValue(Math.random()*100);

    eventRepository.save(fuelLevelState);

    Thread.sleep(100);
    IgnitionState ignitionState = new IgnitionState();
    ignitionState.setLatitude(latitude);
    ignitionState.setLongitude(longitude);
    ignitionState.setTimestamp(Instant.now());
    ignitionState.setVin(vin);
    ignitionState.setValue(Ignition.getRandom());

    eventRepository.save(ignitionState);

    Thread.sleep(100);
    OdometerState odometerState = new OdometerState();
    odometerState.setLatitude(latitude);
    odometerState.setLongitude(longitude);
    odometerState.setTimestamp(Instant.now());
    odometerState.setVin(vin);
    odometerState.setValue(Math.random() * 100);

    eventRepository.save(odometerState);

    Thread.sleep(100);
    TirePressureState tirePressureState = new TirePressureState();
    tirePressureState.setLatitude(latitude);
    tirePressureState.setLongitude(longitude);
    tirePressureState.setTimestamp(Instant.now());
    tirePressureState.setVin(vin);
    tirePressureState.setValue(TirePressure.getRandom());

    eventRepository.save(tirePressureState);

  }

}
