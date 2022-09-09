package com.autonomic.poc.influxdb.repository;

import com.autonomic.poc.influxdb.domain.BasicState;
import com.autonomic.poc.influxdb.domain.BatteryVoltageState;
import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.dto.EventDTO;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
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
public class EventRepository {

  private final InfluxDBClientReactive client;


  public <T> void save(T model) {
    WriteReactiveApi writeApi = client.getWriteReactiveApi();

    var publisher = writeApi.writeMeasurement(WritePrecision.NS, model);
    Disposable subscriber = Flowable.fromPublisher(publisher)
        .subscribe(success -> log.info("Successfully written measurement"));
    subscriber.dispose();
  }

  public Flux<EventDTO> findByDateRange(String start, String end, int limit) {
    long startTime = System.nanoTime();
    String query = "from(bucket: \"vsh\")\n"
        + "  |> range(start: "+start+", stop: "+end+")\n"
        + "  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n"
        + "  |> aggregateWindow(every: 10s, fn: last, createEmpty: false)\n"
        + "  |> limit(n: "+limit+")"
        //+ "  |> map(fn: (r) => ({r with _value: string(v: r[\"_value\"])}))\n"
        + "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_measurement\"], valueColumn: \"_value\")\n"
        + "  |> drop(columns: [\"_start\", \"_stop\", \"_field\"])\n"
        + "  |> yield(name: \"event\")";
    log.info(query);
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    Publisher<EventDTO> publisher =queryReactiveApi.query(query, EventDTO.class);

    return Flux.from(publisher)
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });
  }

}
