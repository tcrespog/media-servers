package metrics.collector.service;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.prometheus.client.Collector;
import metrics.collector.entity.Container;
import metrics.collector.entity.PodMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class KubernetesMetricsScraperService extends Collector {

    private static final Logger log = LoggerFactory.getLogger(KubernetesMetricsScraperService.class);

    @Value("${kubernetes.pod}")
    private String podName;

    private HttpClient client;

    @Inject
    public KubernetesMetricsScraperService(@Value("${kubernetes.proxy:`http://localhost:8001`}") URL proxyUrl) {
        client = HttpClient.create(proxyUrl);
    }

    private PodMetrics requestMetrics() {
        final String metricsApiAddress = "/apis/metrics.k8s.io/v1beta1/namespaces/default/pods/" + podName;

        return client.toBlocking()
                     .retrieve(HttpRequest.GET(metricsApiAddress), PodMetrics.class);
    }

    public List<MetricFamilySamples> collect() {
        PodMetrics podMetrics = requestMetrics();
        log.info("Obtained Kubernetes metrics from pod {}: {}", podName, podMetrics);

        return buildSamples(podMetrics);
    }

    private List<MetricFamilySamples> buildSamples(PodMetrics podMetrics) {
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
