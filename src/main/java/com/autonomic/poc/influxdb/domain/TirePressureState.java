package com.autonomic.poc.influxdb.domain;

import com.autonomic.poc.influxdb.domain.BatteryVoltageState.BatteryVoltage;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;
import lombok.Setter;

@Measurement(name="battery_voltage")
@Getter
@Setter
public class TirePressureState extends BasicState{

  public enum TirePressure {
    UNKNOWN_WHEEL(0),
    FRONT_LEFT(1),
    FRONT_RIGHT(2),
    REAR_LEFT(3),
    REAR_RIGHT(4),
    REAR_LEFT_INNER(5),
    REAR_LEFT_OUTER(6),
    REAR_RIGHT_INNER(7),
    REAR_RIGHT_OUTER(8),
    SYSTEM(9),
    UNRECOGNIZED(-1);

    private int status;
    TirePressure(int status){
        this.status = status;
    }

    public static TirePressure getRandom() {
      return values()[(int) (Math.random() * values().length)];
    }

  }
  @Column
  private TirePressure value;

}
