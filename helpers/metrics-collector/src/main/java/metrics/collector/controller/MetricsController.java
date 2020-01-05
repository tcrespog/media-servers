package metrics.collector.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import metrics.collector.service.TestSessionService;

import javax.inject.Inject;

@Controller("/metrics")
public class MetricsController {

    private TestSessionService testSessionService;

    @Inject
    public MetricsController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return  testSessionService.printMetrics();
    }

}
