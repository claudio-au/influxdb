package com.autonomic.poc.influxdb.repository;

import com.autonomic.poc.influxdb.domain.VehicleState;
import com.autonomic.poc.influxdb.domain.Weather;
import com.influxdb.client.domain.Query;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleStateRepository {

  private final InfluxDBClientReactive client;

  public void save(VehicleState vehicleState) {
    WriteReactiveApi writeApi = client.getWriteReactiveApi();

    var publisher = writeApi.writeMeasurement(WritePrecision.NS, vehicleState);
    Disposable subscriber = Flowable.fromPublisher(publisher)
        .subscribe(success -> log.info("Successfully written vehicle_state measurement"));
    subscriber.dispose();
  }

  public Flux<VehicleState> findByDateRange(String start, String end, int limit) {
    long startTime = System.nanoTime();
    String query = "from(bucket: \"tss\")"
        + "  |> range(start: "+start+", stop: "+end+")"
        + "  |> filter(fn: (r) => r[\"_measurement\"] == \"vehicle_state\")"
        + "  |> limit(n: "+limit+") "
        + "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
    log.info(query);
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    Publisher<VehicleState> publisher =queryReactiveApi.query(query, VehicleState.class);

    return Flux.from(publisher)
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });
  }

}
