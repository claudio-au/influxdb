package com.autonomic.poc.influxdb.domain;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Measurement(name="fuel_level")
@Getter
@Setter
public class FuelLevelState extends BasicState {

  @Column
  private Double value;

}
