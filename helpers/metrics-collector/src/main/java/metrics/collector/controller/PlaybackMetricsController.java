package metrics.collector.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import metrics.collector.entity.PlaybackStats;
import metrics.collector.service.PlaybackMetricsService;

import javax.inject.Inject;

@Controller("/playback")
public class PlaybackMetricsController {

    private PlaybackMetricsService playbackMetricsService;

    @Inject
    public PlaybackMetricsController(PlaybackMetricsService playbackMetricsService) {
        this.playbackMetricsService = playbackMetricsService;
    }

    @Post("/metrics")
    @Produces(MediaType.TEXT_PLAIN)
    public String metrics(@Body PlaybackStats stats) {
        playbackMetricsService.addStats(stats);
        return "OK";
    }

}
