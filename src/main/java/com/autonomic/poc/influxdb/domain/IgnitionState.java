package com.autonomic.poc.influxdb.domain;

import com.autonomic.poc.influxdb.domain.TirePressureState.TirePressure;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;
import lombok.Setter;

@Measurement(name="ignition")
@Getter
@Setter
public class IgnitionState extends BasicState{

  public enum Ignition {
    UNKNOWN_WHEEL(0),
    ON(1),
    OFF(2),
    UNRECOGNIZED(-1);

    private int status;
    Ignition(int status){
      this.status = status;
    }

    public static Ignition getRandom() {
      return values()[(int) (Math.random() * values().length)];
    }

  }
  @Column
  private Ignition value;

}
