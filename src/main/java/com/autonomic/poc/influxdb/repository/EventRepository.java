package com.autonomic.poc.influxdb.repository;

import com.autonomic.poc.influxdb.domain.BasicState;
import com.autonomic.poc.influxdb.domain.BatteryVoltageState;
import com.autonomic.poc.influxdb.domain.Weather;
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

  public <T> Flux<T> findByDateRange(String start, String end, int limit, String measurement) {
    long startTime = System.nanoTime();
    String query = "from(bucket: \"tss\")"
        + "  |> range(start: "+start+", stop: "+end+")"
        + "  |> filter(fn: (r) => r[\"_measurement\"] == \"weather\")"
        + "  |> filter(fn: (r) => r[\"_field\"] == \""+measurement+"\")"
        //+ "  |> aggregateWindow(every: 10s, fn: mean, createEmpty: false)"
        + "  |> limit(n: "+limit+") "
        + "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
    log.info(query);
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    Class<T> clazz = (Class<T>) new Object().getClass();
    Publisher<T> publisher =queryReactiveApi.query(query, clazz);

    return Flux.from(publisher)
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });
  }

}
