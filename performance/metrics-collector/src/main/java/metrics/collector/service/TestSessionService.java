package metrics.collector.service;

import io.micronaut.context.annotation.Value;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Singleton
public class TestSessionService {

    private static final Logger log = LoggerFactory.getLogger(TestSessionService.class);

    private final CollectorRegistry registry;

    private Instant testSessionStart;

    private KubernetesMetricsScraperService kubernetesMetricsScraperService;
    private PlaybackMetricsService playbackMetricsService;
    private Gauge nPlayersMetric;

    @Value("${addedTimeMinutes:1}")
    private Integer addedTimeMinutes;

    @Inject
    public TestSessionService(KubernetesMetricsScraperService kubernetesMetricsScraperService, PlaybackMetricsService playbackMetricsService) {
        this.kubernetesMetricsScraperService = kubernetesMetricsScraperService;
        this.playbackMetricsService = playbackMetricsService;
        registry = new CollectorRegistry();
    }

    public void startTestSession() {
        log.info("Starting test session");

        synchronized (registry) {
            if (testSessionStart == null) {
                initializeMetrics();
            }
        }

        nPlayersMetric.inc();
    }

    void initializeMetrics() {
        testSessionStart = Instant.now();

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

        Observable.timer(addedTimeMinutes, TimeUnit.MINUTES).subscribe((i) -> {
            registry.clear();
            testSessionStart = null;
            logTestSessions();
        });
    }

    private void logTestSessions() {
        Instant testSessionFinish = Instant.now();
        String sessionStart = "Session started at: " + testSessionStart.toString() + " (" + testSessionStart.toEpochMilli() + ")";
        String sessionFinish = "Session finished at: " + testSessionFinish.toString() + " (" + testSessionFinish.toEpochMilli() + ")";
        String sessionDuration = "Session duration: " + Duration.between(testSessionStart, testSessionFinish).toString();

        log.info(sessionStart); log.info(sessionFinish); log.info(sessionDuration);

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("test-sessions.txt", true), StandardCharsets.UTF_8));
            writer.append(sessionStart + "\n");
            writer.append(sessionFinish + "\n");
            writer.append(sessionDuration + "\n\n");
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage());
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
