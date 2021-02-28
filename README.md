# java-spring-boot-test
ZTIdentity test project for Spring Boot

## Project schema

There are two RESTful services:
* /testservice1
* /testservice2

API endpoint of `testservice1`:
* `GET /data` - returns data retrieved from `testservice2/invoice`

API endpoint of `testservice2`:
* `GET /invoice` - returns invoice data for some user:
  * "user1" => "invoice1"
  * "user2" => "invoice2"
  * [other user] => "user_unknown"
  * [no user] => "not_available"

Current user value is retrieved from the "Authorization" header. **Value is 
taken as is, for the simplicity of the test**.

## Quick start

For testservice1:
```
$ cd testservice1
$ ./mvnw clean install
$ ./mvnw spring-boot:run
```

For testservice2:
```
$ cd testservice2
$ ./mvnw clean install
$ ./mvnw spring-boot:run
```

## Simple testing

### Curl

For now testservice1 returns "not available":
```
$ curl http://localhost:8080/data
not_available
$ curl -H "Authorization: user1" http://localhost:8080/data
not_available
```

But testservice2 returns data depending on passed user:
```
$ curl -H "Authorization: user1" http://localhost:8082/invoice
invoice1
$ curl -H "Authorization: other" http://localhost:8082/invoice
user_unknown
$ curl http://localhost:8082/invoice
not_available
```

### OpenTelemetry default

Run Jaeger all-in-one as described here: 
<https://www.jaegertracing.io/docs/1.21/getting-started/#all-in-one>
```
$ docker run -d \
  -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.21
```

Run testservice1 with an OpenTelemetry Java agent:
```
$ java -Dotel.traces.exporter=jaeger \
  -javaagent:../opentelemetry-javaagent-all.jar \
  -jar target/testservice1-0.0.1-SNAPSHOT.jar
```

Open Jaeger UI to see incoming traces at <http://localhost:16686/>

## Start with propagator

Start testservice1 with a ZTIdentity Java agent and enable Authorization 
propagator `ztiauth`.
```
$ java -Dotel.traces.exporter=jaeger -Dotel.propagators=ztiauth \
  -javaagent:../agent-1.0-SNAPSHOT-all.jar \
  -jar target/testservice1-0.0.1-SNAPSHOT.jar
```

## Test with propagator

Expected test result with propagator for testservice1:
```
$ curl -H "Authorization: user1" http://localhost:8080/data
invoice1
$ curl -H "Authorization: other" http://localhost:8080/data
user_unknown
```
