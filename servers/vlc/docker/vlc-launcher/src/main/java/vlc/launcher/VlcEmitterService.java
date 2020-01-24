package vlc.launcher;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;

@Singleton
public class VlcEmitterService {

    private static final Logger log = LoggerFactory.getLogger(VlcEmitterService.class);

    @Value("${rtp.host:localhost}")
    private String rtpHost;
    @Value("${rtp.port:8554}")
    private String rtpPort;

    private static File videosDirectory = new File("/videos");

    private Process vlcProcess;

    public HttpResponse<String> emit(String resolution, String frameRate, String codec) {
        if (vlcProcess != null && vlcProcess.isAlive()) {
            return HttpResponse.badRequest("An emission is already in progress");
        }

        try {
            File[] videoFiles = videosDirectory.listFiles(file ->
                    file.getName().contains(resolution) && file.getName().contains(frameRate) && file.getName().contains(codec)
            );

            if (videoFiles == null || videoFiles.length == 0) {
                return HttpResponse.badRequest("No video to emit found");
            }

            File videoFile = videoFiles[0];
            vlcProcess = new ProcessBuilder("cvlc", "-vvv", videoFile.getAbsolutePath(),
//                    "--sout", "#rtp{sdp=rtsp://:" + rtpPort + "/stream}"
                    "--sout", "#http{mux=ts,dst=:8081/stream}"
            )
                    .inheritIO()
                    .start();

            return HttpResponse.created("Emission initiated");
        } catch (Exception e) {
            log.error(e.getMessage());
            return HttpResponse.badRequest("Error");
        }
    }

}