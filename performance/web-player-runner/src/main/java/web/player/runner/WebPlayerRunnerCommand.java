package web.player.runner;

import io.micronaut.configuration.picocli.PicocliRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Duration;

@Command(name = "web-player-runner", description = "Run multiple web players", mixinStandardHelpOptions = true)
public class WebPlayerRunnerCommand implements Runnable {

    @Option(names = { "-b", "--browser" }, defaultValue = "chrome", paramLabel = "BROWSER", description = "the browser (chrome, firefox, edge, ie)")
    String browserName;

    @Option(names = { "-u", "--url" }, paramLabel = "URL", description = "the URL to navigate to")
    String url;

    @Option(names = { "-d", "--duration" }, defaultValue = "PT15M", paramLabel = "DURATION", description = "the running time duration in ISO-8601 format (PnDTnHnMn.nS)")
    Duration duration;

    @Option(names = { "-i", "--instances" }, defaultValue = "15", paramLabel = "INSTANCES", description = "the number of instances to run")
    Integer instances;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(WebPlayerRunnerCommand.class, args);
    }

    public void run() {
        BrowsersHandler browsersHandler = new BrowsersHandler(browserName, url, instances, duration);
        browsersHandler.run();
    }
}
