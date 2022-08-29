package com.autonomic.poc.influxdb.domain;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Measurement(name="vehicle_state")
public class VehicleState {

  @Column
  private Long heading;
  @Column
  private Long engineSpeed;
  @Column
  private Double latitude;
  @Column
  private Double longitude;
  @Column(tag = true)
  private String brakePedalStatus;
  @Column(tag = true)
  private String driver;
  @Column(tag = true)
  private String vin;
  @Column(tag = true)
  private String personalOrBusiness;
  @Column(timestamp = true, name = "time")
  private Instant timestamp;
}
