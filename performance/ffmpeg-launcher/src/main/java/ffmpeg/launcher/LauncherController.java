package ffmpeg.launcher;

import io.micronaut.http.HttpParameters;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Controller("/emit")
public class LauncherController {

    private static final Logger log = LoggerFactory.getLogger(LauncherController.class);

    private EmitterService emitterService;

    @Inject
    public LauncherController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public HttpResponse<String> emit(HttpParameters parameters) {
        String resolution = parameters.get("resolution", String.class, "");
        String frameRate = parameters.get("frameRate", String.class, "");
        String codec = parameters.get("codec", String.class, "");
        String targetUrl = parameters.get("url", String.class, "");
        log.info("Parameters {}, {}, {}, {}", resolution, frameRate, codec, targetUrl);

        return emitterService.emit(resolution, frameRate, codec, targetUrl);
    }

}
