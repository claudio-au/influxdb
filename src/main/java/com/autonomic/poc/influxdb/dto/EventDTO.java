package com.autonomic.poc.influxdb.dto;

import com.autonomic.poc.influxdb.domain.BatteryVoltageState.BatteryVoltage;
import com.autonomic.poc.influxdb.domain.IgnitionState.Ignition;
import com.influxdb.annotations.Column;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO {

  @Column
  private String vin;
  @Column
  private BatteryVoltage batteryVoltage;
  @Column
  private Double fuel_level;
  @Column
  private Ignition ignition;

  @Column
  private Double odometer;

  @Column(timestamp = true, name = "time")
  private Instant timestamp;

}
