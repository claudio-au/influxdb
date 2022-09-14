package com.autonomic.poc.influxdb.dto;

import com.autonomic.poc.influxdb.domain.BatteryVoltageState.BatteryVoltage;
import com.autonomic.poc.influxdb.domain.IgnitionState.Ignition;
import com.autonomic.poc.influxdb.domain.TirePressureState.TirePressure;
import com.influxdb.annotations.Column;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventAlignedDTO {

  @Column
  private String vin;
  @Column
  private BatteryVoltage batteryVoltage;
  @Column
  private Instant timestampBatteryVoltage;

  @Column
  private Double fuel_level;
  @Column
  private Instant timestampFuelLevel;

  @Column
  private Ignition ignition;
  @Column
  private Instant timestampIgnition;

  @Column
  private TirePressure tirePressure;
  @Column
  private Instant timestampTirePressure;

  @Column
  private Double odometer;
  @Column
  private Instant timestampOdometer;


  @Column(timestamp = true, name = "time")
  private Instant timestamp;

}
