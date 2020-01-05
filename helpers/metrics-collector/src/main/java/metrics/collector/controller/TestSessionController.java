package metrics.collector.controller;

import io.micronaut.http.HttpParameters;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
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
    @Produces(MediaType.TEXT_PLAIN)
    public String start(HttpParameters params) {
        testSessionService.startTestSession();
        return "OK";
    }

    @Get("/end")
    @Produces(MediaType.TEXT_PLAIN)
    public String end() {
        testSessionService.endTestSession();
        return "OK";
    }

}
