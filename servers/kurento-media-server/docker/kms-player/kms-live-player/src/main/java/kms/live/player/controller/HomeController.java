package kms.live.player.controller;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import java.util.concurrent.atomic.AtomicInteger;

@Controller("/")
public class HomeController {

    private final AtomicInteger playerId = new AtomicInteger(0);

    @Value("${metrics.server:`http://localhost:8081`}")
    String metricsServerUrl;

    @View("index")
    @Get
    public HttpResponse index() {
        return HttpResponse.ok(
                CollectionUtils.mapOf(
                        "metricsServerUrl", metricsServerUrl,
                        "playerId", playerId.incrementAndGet()
                )
        );
    }

}
