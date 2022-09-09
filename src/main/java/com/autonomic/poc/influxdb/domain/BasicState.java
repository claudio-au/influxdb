package com.autonomic.poc.influxdb.domain;

import com.influxdb.annotations.Column;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicState {

  @Column
  private Double longitude;

  @Column
  private Double latitude;

  @Column(tag = true, name = "vin")
  private String vin;

  @Column(timestamp = true, name = "time")
  private Instant timestamp;
}
