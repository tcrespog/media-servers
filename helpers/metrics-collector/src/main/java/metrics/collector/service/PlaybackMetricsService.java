package metrics.collector.service;

import io.prometheus.client.Collector;
import metrics.collector.entity.PlaybackStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class PlaybackMetricsService extends Collector {

    private static final Logger log = LoggerFactory.getLogger(PlaybackMetricsService.class);

    private ConcurrentLinkedQueue<PlaybackStats> statsQueue;

    @Inject
    public PlaybackMetricsService() {
        statsQueue = new ConcurrentLinkedQueue<>();
    }

    public List<MetricFamilySamples> collect() {
        log.info("Obtained playback metrics: {}", statsQueue);
        return buildSamples();
    }

    private List<MetricFamilySamples> buildSamples() {
        MetricFamilySamples[] sampleFamilies = {
                new MetricFamilySamples("playback_startup_delay", Type.GAUGE, "Playback startup delay (s)", new ArrayList<>()),
                new MetricFamilySamples("playback_received_frames", Type.GAUGE, "Playback received frames", new ArrayList<>()),
                new MetricFamilySamples("playback_lost_frames", Type.GAUGE, "Playback lost frames", new ArrayList<>()),
        };

        Iterator<PlaybackStats> i = statsQueue.iterator();
        while (i.hasNext()) {
            PlaybackStats s = i.next();
            for (MetricFamilySamples sampleFamily : sampleFamilies) {
                Double value = s.getValue(sampleFamily.name);
                if (value == null) {
                    continue;
                }

                MetricFamilySamples.Sample sample = new MetricFamilySamples.Sample(sampleFamily.name, Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), value, s.getTimestamp().toEpochMilli());
                sampleFamily.samples.add(sample);
            }
            i.remove();
        }

        return Arrays.asList(sampleFamilies);
    }

    public void addStats(PlaybackStats stats) {
        statsQueue.add(stats);
    }

}
