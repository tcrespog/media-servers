package metrics.collector.service;

import io.prometheus.client.Collector;
import metrics.collector.entity.PlaybackStats;
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
        List<MetricFamilySamples.Sample> lostSamplesSamples = new ArrayList<>();
        List<MetricFamilySamples.Sample> lostFramesSamples = new ArrayList<>();
        Iterator<PlaybackStats> i = statsQueue.iterator();
        while (i.hasNext()) {
            PlaybackStats s = i.next();

            log.info("Reading {}", s);
            MetricFamilySamples.Sample delaySample = new MetricFamilySamples.Sample("playback_startup_delay", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getStartupDelay(), s.getTimestamp().toEpochMilli());
            MetricFamilySamples.Sample lostSamplesSample = new MetricFamilySamples.Sample("playback_lost_samples", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getLostSamples(), s.getTimestamp().toEpochMilli());
            MetricFamilySamples.Sample lostFramesSample = new MetricFamilySamples.Sample("playback_lost_frames", Arrays.asList("playerId"), Arrays.asList(s.getPlayerId()), s.getLostFrames(), s.getTimestamp().toEpochMilli());
            delaySamples.add(delaySample);
            lostSamplesSamples.add(lostSamplesSample);
            lostFramesSamples.add(lostFramesSample);

            i.remove();
        }
        MetricFamilySamples delayMetricFamilySamples = new MetricFamilySamples("playback_startup_delay", Type.GAUGE, "Playback startup delay (s)", delaySamples);
        MetricFamilySamples lostSamplesMetricFamilySamples = new MetricFamilySamples("playback_lost_samples", Type.GAUGE, "Playback lost samples", lostFramesSamples);
        MetricFamilySamples lostFramesMetricFamilySamples = new MetricFamilySamples("playback_lost_frames", Type.GAUGE, "Playback lost frames", lostSamplesSamples);
        allMetricFamilySamples.add(delayMetricFamilySamples);
        allMetricFamilySamples.add(lostSamplesMetricFamilySamples);
        allMetricFamilySamples.add(lostFramesMetricFamilySamples);

        return allMetricFamilySamples;
    }

    public void addStats(PlaybackStats stats) {
        statsQueue.add(stats);
    }

}
