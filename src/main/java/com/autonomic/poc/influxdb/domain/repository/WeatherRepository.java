package com.autonomic.poc.influxdb.domain.repository;

import com.autonomic.poc.influxdb.domain.Weather;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherRepository {

  private final InfluxDBClientReactive client;

  public void save(Weather weather) {
    WriteReactiveApi writeApi = client.getWriteReactiveApi();

    var publisher = writeApi.writeMeasurement(WritePrecision.NS, weather);
    Disposable subscriber = Flowable.fromPublisher(publisher)
        .subscribe(success -> log.info("Successfully written weather measurement"));
    subscriber.dispose();
  }

  public Flux<Weather> findByDateRange(String start, String end, int limit) {
    long startTime = System.nanoTime();
    String query = "from(bucket: \"tss\")"
        + "  |> range(start: "+start+", stop: "+end+")"
        + "  |> filter(fn: (r) => r[\"_measurement\"] == \"weather\")"
        + "  |> filter(fn: (r) => r[\"_field\"] == \"humidity\" or r[\"_field\"] == \"temperature\")"
        //+ "  |> aggregateWindow(every: 10s, fn: mean, createEmpty: false)"
        + "  |> limit(n: "+limit+") "
        + "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
    log.info(query);
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    Publisher<Weather> publisher =queryReactiveApi.query(query, Weather.class);

    return Flux.from(publisher)
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });
  }

  public Flux<String> findRawByDateRange(String start, String end, int limit) {
    long startTime = System.nanoTime();
    String query = "from(bucket: \"tss\")"
        + "  |> range(start: "+start+", stop: "+end+")"
        + "  |> filter(fn: (r) => r[\"_measurement\"] == \"weather\")"
        + "  |> filter(fn: (r) => r[\"_field\"] == \"humidity\" or r[\"_field\"] == \"temperature\")"
        //+ "  |> aggregateWindow(every: 15m, fn: mean, createEmpty: false)"
        + "  |> limit(n: "+limit+") "
        + "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
    log.info(query);
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();

    return Flux.from(queryReactiveApi.queryRaw(query))
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });

  }

  public Flux<Weather> findByRange(Instant start, Instant end, int limit) {
    long startTime = System.nanoTime();
    com.influxdb.query.dsl.Flux flux = com.influxdb.query.dsl.Flux.from("tss")
          .range(start, end)
        .filter(Restrictions.or(Restrictions.measurement().equal("weather")))
        .filter(Restrictions.or(Restrictions.field().equal("temperature"), Restrictions.field().equal("humidity")))
        /*.aggregateWindow(15L, ChronoUnit.MINUTES, "mean")
          .withCreateEmpty(false)*/
        .limit(limit)
        .pivot()
          .withRowKey(new String[] {"_time"})
          .withColumnKey(new String[] {"_field"})
          .withValueColumn("_value");

    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    System.out.println(flux.toString());
    return Flux.from(queryReactiveApi.query(flux.toString(), Weather.class))
        .doOnComplete(() -> {
          System.out.println("Query time: " + ((System.nanoTime() - startTime) /1000000));
        });
  }

}
