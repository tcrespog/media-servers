package metrics.collector.controller;

import io.micronaut.http.HttpParameters;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import metrics.collector.entity.PlaybackStats;
import metrics.collector.service.TestSessionService;

import javax.inject.Inject;

@Controller("/test")
public class TestSessionController {

    private TestSessionService testSessionService;

    @Inject
    public TestSessionController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @Get("/start")
    public void start(HttpParameters params) {
        testSessionService.startTestSession();
    }

    @Get("/end")
    public void end() {
        testSessionService.endTestSession();
    }

}
