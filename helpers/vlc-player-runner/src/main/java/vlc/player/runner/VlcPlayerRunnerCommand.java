package vlc.player.runner;

import io.micronaut.configuration.picocli.PicocliRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Duration;

@Command(name = "vlc-player-runner", description = "Run multiple VLC media players",
        mixinStandardHelpOptions = true)
public class VlcPlayerRunnerCommand implements Runnable {

    @Option(names = { "-p", "--path" }, defaultValue = "vlc", paramLabel = "VLC PATH", description = "the path to VLC")
    String programPath;
    @Option(names = { "-m", "--metricsUrl" }, paramLabel = "METRICS URL", description = "the metrics collector URL")
    String metricsUrl;
    @Option(names = { "-s", "--streamUrl" }, paramLabel = "STREAM URL", description = "the stream URL to play")
    String streamUrl;
    @Option(names = { "-d", "--duration" }, defaultValue = "PT15M", paramLabel = "DURATION", description = "the running time duration in ISO-8601 format (PnDTnHnMn.nS)")
    Duration duration;
    @Option(names = { "-i", "--instances" }, defaultValue = "2", paramLabel = "INSTANCES", description = "the number of instances to run")
    Integer instances;

    private static final Logger log = LoggerFactory.getLogger(VlcPlayerRunnerCommand.class);

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(VlcPlayerRunnerCommand.class, args);
    }

    public void run() {
        try {
            VlcHandler vlcHandler = new VlcHandler(programPath, metricsUrl, streamUrl, instances);
            vlcHandler.run();

            log.info("Sleeping duration: {}", duration.toString());
            Thread.sleep(duration.toMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
