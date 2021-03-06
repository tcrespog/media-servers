* Run Prometheus (make sure to specify the correct metrics-collector URL in prometheus.yml)
```shell script
cd prometheus &&
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
* Assemble Metrics Collector JAR file
```shell script
./gradlew build && \
cp build/libs/metrics-collector-*-all.jar ~/metrics-collector.jar
```
* Open Kubernetes proxy on a terminal
```shell script
kubectl proxy
```
* Run Metrics Collector (on host)
```shell script
export KUBERNETES_POD=kms-cloud; \
export KUBERNETES_PROXY=http://localhost:8001; \
java -jar metrics-collector.jar
```