* Run Prometheus
```shell script
docker run -d \
    --name=prometheus \
    -p 9090:9090 \
    -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
    prom/prometheus
```
* Run Grafana
```shell script
docker run -d --name=grafana -p 3000:3000 grafana/grafana
```
* Build Metrics  Collector
```shell script
./gradlew build
docker build -t metrics-collector .
docker tag metrics-collector cloud.canister.io:5000/tcrespo12/metrics-collector:latest
docker push cloud.canister.io:5000/tcrespo12/metrics-collectors:latest
```
* Run Metrics  Collector
```shell script
docker run -d -p 8081:8081 \
    --name metrics-collector
    cloud.canister.io:5000/tcrespo12/metrics-collectors:latest
```