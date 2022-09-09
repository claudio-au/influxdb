package com.autonomic.poc.influxdb.domain;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;
import lombok.Setter;

@Measurement(name="battery_voltage")
@Getter
@Setter
public class BatteryVoltageState extends BasicState{

  public enum BatteryVoltage {
    UNKNOWN_BATTERY(0),
    PRIMARY_BATTERY(1),
    ACCESSORY_BATTERY(2),
    BACKUP_BATTERY(3),
    UNRECOGNIZED(-1);

    private int status;
    BatteryVoltage(int status){
      this.status = status;
    }

    public static BatteryVoltage getRandom() {
      return values()[(int) (Math.random() * values().length)];
    }

  }
  @Column
  private BatteryVoltage value;

}
