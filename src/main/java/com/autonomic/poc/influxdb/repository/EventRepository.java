package com.autonomic.poc.influxdb.repository;

import com.autonomic.poc.influxdb.domain.BasicState;
import com.autonomic.poc.influxdb.domain.BatteryVoltageState;
import com.autonomic.poc.influxdb.domain.Measurements;
import com.autonomic.poc.influxdb.domain.Weather;
import com.autonomic.poc.influxdb.dto.EventAlignedDTO;
import com.autonomic.poc.influxdb.dto.EventDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventRepository {

  private final InfluxDBClientReactive client;
  private final InfluxDBClient syncClient;


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

  public List<Measurements> findMeasurements() {
    String query = "import \"influxdata/influxdb/schema\"\n"
        + "schema.measurements(bucket: \"vsh\")";

    QueryApi queryApi = syncClient.getQueryApi();
    return queryApi.query(query, Measurements.class);

  }

  public Flux<EventAlignedDTO> findAligned(String start, String end, int limit, String aggregationInterval){
    long startTime = System.nanoTime();
    List<String> queries = new ArrayList<>();
    List<String> union = new ArrayList<>();
    List<String> fillElements = new ArrayList<>();
    String templateFill = "    |> fill(column: \"%measurement%\", usePrevious: true)\n"
        + "    |> fill(column: \"timestamp_%measurement%\", usePrevious: true)\n";
    String templateUnion = "joinTables = union(tables: [%tables%]) \n"
        + "    |> group(columns: [\"_time\"], mode: \"by\")\n"
        + "     %fill%"
        + "    |>tail(n: 1)\n"
        + "    |>group()\n"
        + "    |>limit(n:"+limit+")\n"
        + "    |>yield(name:\"union\")";
    String template = "%measurement% = from(bucket: \"vsh\")\n"
        + "    |> range(start: "+start+", stop: "+end+" )\n"
        + "    |> filter(fn: (r) => r[\"_field\"] == \"value\")\n"
        + "    |> filter(fn: (r) => r[\"_measurement\"] == \"%measurement%\")\n"
        + "    |> duplicate(column: \"_time\", as: \"timestamp_%measurement%\")\n"
        + "    |> aggregateWindow(every: AGGREGATION, fn: last, createEmpty: false)\n"
        + "    |> keep(columns: [\"_measurement\", \"_time\", \"_value\", \"vin\", \"timestamp_%measurement%\"])\n"
        + "    |> pivot(rowKey:[\"_time\",\"timestamp_%measurement%\"], columnKey: [\"_measurement\"], valueColumn: \"_value\")\n"
        + "\n";
    findMeasurements()
        .stream()
        .forEach( m-> {
          String value = template.replaceAll("%measurement%", m.getValue());
          queries.add(value);
          union.add(m.getValue());
          fillElements.add(templateFill.replaceAll("%measurement%", m.getValue()));

        });
    String query = "AGGREGATION="+aggregationInterval+"\n" +
        queries.stream().collect(Collectors.joining("\n"));
    query += templateUnion
        .replace("%tables%", union.stream().collect(Collectors.joining(",")))
        .replace("%fill%", fillElements.stream().collect(Collectors.joining("\n")));

    final String logQuery = query;
    QueryReactiveApi queryReactiveApi = client.getQueryReactiveApi();
    Publisher<EventAlignedDTO> publisher =queryReactiveApi.query(query, EventAlignedDTO.class);
    return Flux.from(publisher)
        .doOnComplete(() -> {
          log.info("Query time: {}", ((System.nanoTime() - startTime) /1000000));
          log.info("Query: \n{}", logQuery);
        });
  }

}
