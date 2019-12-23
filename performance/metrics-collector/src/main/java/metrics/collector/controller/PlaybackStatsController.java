package metrics.collector.controller;

import io.micronaut.http.annotation.*;
import metrics.collector.entity.PlaybackStats;
import metrics.collector.service.TestSessionService;

import javax.inject.Inject;

@Controller("/playback")
public class PlaybackStatsController {

    private TestSessionService testSessionService;

    @Inject
    public PlaybackStatsController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @Post("/stats")
    public void stats(@Body PlaybackStats stats) {
        testSessionService.updatePlaybackStats(stats);
    }

}
