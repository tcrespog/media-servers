package ffmpeg.launcher;

import io.micronaut.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.nio.file.Files;

@Singleton
public class EmitterService {

    private static final Logger log = LoggerFactory.getLogger(EmitterService.class);

    private static File videosDirectory = new File("/videos");

    private Process ffmpegProcess;

    public HttpResponse<String> emit(String resolution, String frameRate, String codec, String targetUrl) {
        if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
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
            String mimeType = Files.probeContentType(videoFile.toPath());
            log.info("The mime type {}", mimeType);

            ffmpegProcess = new ProcessBuilder("ffmpeg", "-re", "-i", videoFile.getAbsolutePath(),
                    "-f", mimeType.substring(mimeType.indexOf('/') + 1), "-content_type", mimeType,
                    "-vcodec", "copy", "-acodec", "copy", targetUrl)
                    .inheritIO()
                    .start();

            return HttpResponse.created("Emission initiated");
        } catch (Exception e) {
            log.error(e.getMessage());
            return HttpResponse.badRequest("Error");
        }
    }

}