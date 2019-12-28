package metrics.collector.service;

import io.prometheus.client.Collector;
import metrics.collector.entity.Container;
import metrics.collector.entity.PlaybackStats;
import metrics.collector.entity.PodMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
        List<MetricFamilySamples> allMetricFamilySamples = new ArrayList<>();

        List<MetricFamilySamples.Sample> delaySamples = new ArrayList<>();
        List<MetricFamilySamples.Sample> stallsSamples = new ArrayList<>();
        List<MetricFamilySamples.Sample> droppedFramesSamples = new ArrayList<>();
        Iterator<PlaybackStats> i = statsQueue.iterator();
        while (i.hasNext()) {
            PlaybackStats s = i.next();

            MetricFamilySamples.Sample delaySample = new MetricFamilySamples.Sample("playback_startup_delay", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getStartupDelay(), s.getTimestamp().toEpochMilli());
            MetricFamilySamples.Sample stallsSample = new MetricFamilySamples.Sample("playback_stalls", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getStalls(), s.getTimestamp().toEpochMilli());
            MetricFamilySamples.Sample droppedFramesSample = new MetricFamilySamples.Sample("playback_dropped_frames", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getDroppedFrames(), s.getTimestamp().toEpochMilli());
            delaySamples.add(delaySample);
            stallsSamples.add(stallsSample);
            droppedFramesSamples.add(droppedFramesSample);

            i.remove();
        }
        MetricFamilySamples delayMetricFamilySamples = new MetricFamilySamples("playback_startup_delay", Type.GAUGE, "Playback startup delay (s)", delaySamples);
        MetricFamilySamples stallsMetricFamilySamples = new MetricFamilySamples("playback_stalls", Type.GAUGE, "Playback stalls", droppedFramesSamples);
        MetricFamilySamples droppedFramesMetricFamilySamples = new MetricFamilySamples("playback_dropped_frames", Type.GAUGE, "Playback dropped frames", stallsSamples);
        allMetricFamilySamples.add(delayMetricFamilySamples);
        allMetricFamilySamples.add(stallsMetricFamilySamples);
        allMetricFamilySamples.add(droppedFramesMetricFamilySamples);

        return allMetricFamilySamples;
    }

    public void addStats(PlaybackStats stats) {
        statsQueue.add(stats);
    }

}
