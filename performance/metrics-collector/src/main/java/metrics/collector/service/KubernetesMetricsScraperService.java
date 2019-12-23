package metrics.collector.service;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import metrics.collector.entity.Container;
import metrics.collector.entity.PodMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class KubernetesMetricsScraperService extends Collector {

    private static final Logger log = LoggerFactory.getLogger(KubernetesMetricsScraperService.class);

    @Value("${kubernetes.pod}")
    private String podName;

    @Inject
    @Client("http://localhost:8001")
    private HttpClient client;

    private PodMetrics requestMetrics() {
        log.info("Requesting metrics from pod '{}'", podName);
        final String metricsApiAddress = "/apis/metrics.k8s.io/v1beta1/namespaces/default/pods/" + podName;

        return client.toBlocking()
                     .retrieve(HttpRequest.GET(metricsApiAddress), PodMetrics.class);
    }

    public List<MetricFamilySamples> collect() {
        PodMetrics podMetrics = requestMetrics();
        log.info("Obtained metrics {}", podMetrics);

        return buildSamplesPreservingTimestamps(podMetrics);
    }

    private List<MetricFamilySamples> buildSamples(PodMetrics podMetrics) {
        List<MetricFamilySamples> allMetricFamilySamples = new ArrayList<>();

        GaugeMetricFamily memoryGauge = new GaugeMetricFamily("kubernetes_pod_memory", "Kubernetes containers memory consumption (Ki)", Arrays.asList("container"));
        GaugeMetricFamily cpuGauge = new GaugeMetricFamily("kubernetes_pod_cpu", "Kubernetes containers CPU consumption (ns)", Arrays.asList("container"));
        for (Container c : podMetrics.getContainers()) {
            memoryGauge.addMetric(Arrays.asList(c.getName()), c.getMemory());
            cpuGauge.addMetric(Arrays.asList(c.getName()), c.getCpu());
        }
        allMetricFamilySamples.add(memoryGauge);
        allMetricFamilySamples.add(cpuGauge);

        return allMetricFamilySamples;
    }

    private List<MetricFamilySamples> buildSamplesPreservingTimestamps(PodMetrics podMetrics) {
        List<MetricFamilySamples> allMetricFamilySamples = new ArrayList<>();

        List<MetricFamilySamples.Sample> memorySamples = new ArrayList<>();
        List<MetricFamilySamples.Sample> cpuSamples = new ArrayList<>();
        for (Container c : podMetrics.getContainers()) {
            MetricFamilySamples.Sample memorySample = new MetricFamilySamples.Sample("kubernetes_pod_memory", Arrays.asList("container"), Arrays.asList(c.getName()), c.getMemory(), podMetrics.getTimestamp().toEpochMilli());
            MetricFamilySamples.Sample cpuSample = new MetricFamilySamples.Sample("kubernetes_pod_cpu", Arrays.asList("container"), Arrays.asList(c.getName()), c.getCpu(), podMetrics.getTimestamp().toEpochMilli());
            memorySamples.add(memorySample);
            cpuSamples.add(cpuSample);
        }
        MetricFamilySamples memoryMetricFamilySamples = new MetricFamilySamples("kubernetes_pod_memory", Type.GAUGE, "Kubernetes containers memory consumption (Ki)", memorySamples);
        MetricFamilySamples cpuMetricFamilySamples = new MetricFamilySamples("kubernetes_pod_cpu", Type.GAUGE, "Kubernetes containers CPU consumption (ns)", cpuSamples);
        allMetricFamilySamples.add(memoryMetricFamilySamples);
        allMetricFamilySamples.add(cpuMetricFamilySamples);

        return allMetricFamilySamples;
    }

}
