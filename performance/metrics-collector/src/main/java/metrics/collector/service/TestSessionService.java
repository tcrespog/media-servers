package metrics.collector.service;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Singleton
public class TestSessionService {

    private static final Logger log = LoggerFactory.getLogger(TestSessionService.class);

    private CollectorRegistry registry;

    private Instant testSessionStart;

    private KubernetesMetricsScraperService kubernetesMetricsScraperService;
    private PlaybackMetricsService playbackMetricsService;
    private Gauge nPlayersMetric;

    @Inject
    public TestSessionService(KubernetesMetricsScraperService kubernetesMetricsScraperService, PlaybackMetricsService playbackMetricsService) {
        this.kubernetesMetricsScraperService = kubernetesMetricsScraperService;
        this.playbackMetricsService = playbackMetricsService;
    }

    public void startTestSession() {
        log.info("Starting test session");

        if (registry == null) {
            initializeMetrics();
        }

        nPlayersMetric.inc();
    }

    void initializeMetrics() {
        testSessionStart = Instant.now();
        registry = new CollectorRegistry();

        nPlayersMetric = Gauge.build("playback_players", "Number of players")
                .register(registry);
        kubernetesMetricsScraperService.register(registry);
        playbackMetricsService.register(registry);
    }

    public void endTestSession() {
        log.info("Ending test session");
        nPlayersMetric.dec();

        if (nPlayersMetric.get() > 0) {
           return;
        }

        registry.unregister(playbackMetricsService);
        Observable.timer(4, TimeUnit.MINUTES).subscribe((i) -> {
            registry.clear();
            registry = null;
            log.info("Session duration {}", Duration.between(testSessionStart, Instant.now()));
        });
    }

    public String printMetrics() {
        if  (registry == null) {
            return "";
        }

        StringWriter writer = new StringWriter();
        try {
            TextFormat.write004(writer, registry.metricFamilySamples());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return writer.toString();
    }

}
