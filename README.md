# InfluxDB POC for TSS.

InfluxDB is an open-source time series database (TSDB) developed by the company InfluxData. It is written in the Go programming language for storage and retrieval of time series data in fields such as operations monitoring, application metrics, Internet of Things sensor data, and real-time analytics. It also has support for processing data from Graphite.

Current version: 2.0


## POC Goal:

Find a tool to replace Druid infrastructure.

This tool has to be able to process a high number of queries quickly.


## Scope of POC

This POC has to write and read data in influxdb and provide a way to user query the information.  
(https://github.com/claudio-au/influxdb/blob/main/src/main/java/com/autonomic/poc/influxdb/job/WeatherJobs.java)

This application is running using Java 11 and Spring Boot.
Also, It is using reactive library to influxdb

https://github.com/influxdata/influxdb-client-java/tree/master/client-reactive


if you want to ingest data, please uncomment the annotation **@EnableScheduling** on InfluxdbApplication

## Data ingestion

To ingest data, it was created a job task that runs every 1 milliseconds 
and save a measurement called weather into influxdb.
The structure of this measurement is defined on model Weather.java (https://github.com/claudio-au/influxdb/blob/main/src/main/java/com/autonomic/poc/influxdb/domain/Weather.java)

## Data Reading

By default, the application is querying data from a specific period (1 hour).
start: "2022-07-25T14:00:00Z", end: "2022-07-25T15:00:00Z"
InfluxDB accepts relative dates for example **-1h**

To access the endpoint, 
run the application and access the endpoint http://localhost:8080/query
It will run a pre-configured query (not aggregated).


## Performance
InfluxDB showed an excelent performance to query non-aggregated data.
Each request took 635ms to return 381k records.

In a load test, running on local machine for 1minute for 10 virtual users (concurrent)
```shell
          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: poc-get.js
     output: -

  scenarios: (100.00%) 1 scenario, 10 max VUs, 1m30s max duration (incl. graceful stop):
           * default: 10 looping VUs for 1m0s (gracefulStop: 30s)


running (1m01.8s), 00/10 VUs, 261 complete and 0 interrupted iterations
default ✓ [======================================] 10 VUs  1m0s

     ✓ response code was 200

     checks.........................: 100.00% ✓ 261      ✗ 0   
     data_received..................: 2.1 GB  34 MB/s
     data_sent......................: 22 kB   359 B/s
     http_req_blocked...............: avg=94.14µs min=1µs      med=6µs    max=2.43ms  p(90)=7µs    p(95)=12µs  
     http_req_connecting............: avg=14.55µs min=0s       med=0s     max=440µs   p(90)=0s     p(95)=0s    
   ✗ http_req_duration..............: avg=1.33s   min=667.4ms  med=1.27s  max=2.8s    p(90)=1.69s  p(95)=2.11s 
       { expected_response:true }...: avg=1.33s   min=667.4ms  med=1.27s  max=2.8s    p(90)=1.69s  p(95)=2.11s 
   ✓ http_req_failed................: 0.00%   ✓ 0        ✗ 261 
     http_req_receiving.............: avg=3.41ms  min=1.35ms   med=2.77ms max=20.54ms p(90)=5.11ms p(95)=6.77ms
     http_req_sending...............: avg=33.42µs min=3µs      med=25µs   max=372µs   p(90)=41µs   p(95)=126µs 
     http_req_tls_handshaking.......: avg=0s      min=0s       med=0s     max=0s      p(90)=0s     p(95)=0s    
     http_req_waiting...............: avg=1.33s   min=663.87ms med=1.27s  max=2.79s   p(90)=1.69s  p(95)=2.1s  
     http_reqs......................: 261     4.225073/s
     iteration_duration.............: avg=2.33s   min=1.66s    med=2.27s  max=3.81s   p(90)=2.69s  p(95)=3.12s 
     iterations.....................: 261     4.225073/s
     vus............................: 4       min=4      max=10
     vus_max........................: 10      min=10     max=10

```

None 4xx or 5xx status codes happened. 

However, some requests took more than 2 seconds to run, failing on assertion for P(95)<2000.

**Couchbase** POC https://github.com/claudio-au/couchbase, running on same scenario, was able to run only 10 request during the whole test. 

Also, each request took more than 4.5 second to return data.
