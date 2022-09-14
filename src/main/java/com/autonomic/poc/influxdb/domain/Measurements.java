package com.autonomic.poc.influxdb.domain;

import com.influxdb.annotations.Column;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Measurements {

  @Column
  String value;

}
