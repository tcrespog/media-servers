package vlc.player.runner;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VlcInstance {

    private static final Logger log = LoggerFactory.getLogger(VlcInstance.class);
    private static final Pattern statPattern = Pattern.compile(".*?(\\d+)");
    private static final Pattern numberPattern = Pattern.compile("(\\d+)");

    private String playerId;
    private String programPath;
    private String streamUrl;

    private Process process;
    private BufferedWriter processInput;
    private BufferedReader processOutput;

    private Instant launchTimestamp;
    private Double startupTime;
    private boolean isPlaybackEnded;

    private PlaybackStats totalStats;
    private Subject<PlaybackStats> statsSubject;

    public VlcInstance(String id, String programPath, String streamUrl) {
        this.playerId = id;
        this.programPath = programPath;
        this.streamUrl = streamUrl;
        statsSubject = PublishSubject.create();
        totalStats = new PlaybackStats(playerId, null);
        isPlaybackEnded = false;
    }

    public void play() throws Exception {
        log.info("Playing player {}", playerId);

        process = new ProcessBuilder(programPath, "--extraintf", "rc", "--no-rc-fake-tty", streamUrl)
                .directory(null)
                .redirectErrorStream(true)
                .start();
        launchTimestamp = Instant.now();

        processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        readOutput();
    }

    private void readOutput() throws Exception {
        PlaybackStats stats = null;
        for (String line = processOutput.readLine(); line != null; line = processOutput.readLine()) {
            parseGetTimeOutput(line);
            stats = parseStatsOutput(stats, line);

            if (isPlaybackEnded) {
                log.info("Playback for player {} ended", playerId);
                statsSubject.onComplete();
                break;
            }
        }
    }

    private PlaybackStats parseStatsOutput(PlaybackStats stats, String line) {
        if (line.contains("begin of statistical info")) {
            stats = new PlaybackStats(playerId, Instant.now());
            stats.setStartupDelay(startupTime);
            log.info("Reading stats for player {}", playerId);
        }

        if (stats != null) {
            parseStatsLine(stats, line);
        }

        if (line.contains("end of statistical info")) {
            statsSubject.onNext(stats);
        }

        return stats;
    }

    private void parseStatsLine(PlaybackStats stats, String line) {
        Matcher matcher = statPattern.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        Long number = Long.parseLong(matcher.group(1));

        if (line.contains("frames lost")) {
            stats.setLostFrames(number - totalStats.getLostFrames());
            totalStats.setLostFrames(number);
        } else if (line.contains("frames displayed")) {
            stats.setReceivedFrames(number - totalStats.getReceivedFrames());
            totalStats.setReceivedFrames(number);
        } else if (line.contains("buffers lost")) {
            stats.setLostSamples(number - totalStats.getLostSamples());
            totalStats.setLostSamples(number);
        } else if (line.contains("buffers played")) {
            stats.setReceivedSamples(number - totalStats.getReceivedSamples());
            totalStats.setReceivedSamples(number);
        }
    }

    private void parseGetTimeOutput(String line) {
        isPlaybackEnded = line.trim().equals("");

        long playedTime = parseNumberLine(line);
        if (playedTime <= 0) {
            return;
        }

        Instant now = Instant.now();
        Instant playbackStartTimestamp = now.minus(playedTime, ChronoUnit.SECONDS);
        startupTime = Duration.between(launchTimestamp, playbackStartTimestamp).toMillis() / 1000D;
    }

    private Long parseNumberLine(String line) {
        Matcher matcher = numberPattern.matcher(line);
        if (!matcher.matches()) {
            return 0L;
        }

        return Long.parseLong(matcher.group(1));
    }

    public boolean getIsPlaybackEnded() {
        return isPlaybackEnded;
    }

    public void requestStats() throws Exception {
        if (isPlaybackEnded) {
            return;
        }

        log.info("Requesting stats for player {}", playerId);

        sendCommand("get_time");
        sendCommand("stats");
    }

    private void sendCommand(String command) throws Exception {
        processInput.write(command);
        processInput.newLine();
        processInput.flush();
    }

    public Observable<PlaybackStats> getStats$() {
        return statsSubject;
    }

}