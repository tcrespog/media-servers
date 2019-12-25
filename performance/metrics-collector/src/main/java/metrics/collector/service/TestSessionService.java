package metrics.collector.service;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import io.reactivex.Observable;
import metrics.collector.entity.PlaybackStats;
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
    private Gauge nPlayersMetric;
    private Gauge startupDelayMetric;
    private Gauge stallsMetric;
    private Gauge droppedFramesMetric;

    @Inject
    public TestSessionService(KubernetesMetricsScraperService kubernetesMetricsScraperService) {
        this.kubernetesMetricsScraperService = kubernetesMetricsScraperService;
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

        startupDelayMetric = Gauge.build("playback_startup_delay", "Playback startup delay (s)")
                .labelNames("playerId")
                .register(registry);
        stallsMetric = Gauge.build("playback_stalls", "Playback stalls")
                .labelNames("playerId")
                .register(registry);
        droppedFramesMetric = Gauge.build("playback_dropped_frames", "Playback dropped frames")
                .labelNames("playerId")
                .register(registry);
        nPlayersMetric = Gauge.build("playback_players", "Number of players")
                .register(registry);
        kubernetesMetricsScraperService.register(registry);
    }

    public void endTestSession() {
        log.info("Ending test session");
        nPlayersMetric.dec();

        if (nPlayersMetric.get() == 0) {
            Observable.timer(4, TimeUnit.MINUTES).subscribe((i) -> {
                registry.clear();
                log.info("Session duration {}", Duration.between(testSessionStart, Instant.now()));
            });
        }
    }

    public void updatePlaybackStats(PlaybackStats stats) {
        log.info("Updating playback stats {}", stats);

        if (stats.getStartupDelay() != null) {
            startupDelayMetric.labels(stats.getPlayerId())
                    .set(stats.getStartupDelay());
        }
        if (stats.getDroppedFrames() != null) {
            Gauge.Child labeledMetric = droppedFramesMetric.labels(stats.getPlayerId());
            labeledMetric.set(stats.getDroppedFrames());
        }
        if (stats.getStalls() != null) {
            Gauge.Child labeledMetric = stallsMetric.labels(stats.getPlayerId());
            labeledMetric.set(stats.getStalls());
        }
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
