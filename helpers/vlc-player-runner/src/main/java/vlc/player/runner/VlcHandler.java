package vlc.player.runner;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VlcHandler {

    private static final Logger log = LoggerFactory.getLogger(VlcHandler.class);

    private String programPath;
    private String streamUrl;
    private Integer instances;

    private HttpClient httpClient;
    private List<VlcInstance> vlcInstances;

    public VlcHandler(String programPath, String metricsCollectorUrl, String streamUrl, Integer instances) throws MalformedURLException {
        this.programPath = programPath;
        this.streamUrl = streamUrl;
        this.instances = instances;
        httpClient = HttpClient.create(new URL(metricsCollectorUrl));
    }

    public void run() {
       createInstances();

        subscribeToStats();
        playInstances();
        requestStatsPeriodically();
    }

    private void createInstances() {
        vlcInstances = new ArrayList<>();
        for (int i = 1; i <= instances; i++) {
            vlcInstances.add(new VlcInstance("" + i, programPath, streamUrl));
        }
    }

    private void requestStatsPeriodically() {
        Observable.interval(30, TimeUnit.SECONDS)
                .subscribe(i -> {
                    for (VlcInstance vlc : vlcInstances) {
                        vlc.requestStats();
                    }
                });
    }

    private void subscribeToStats() {
        for (VlcInstance vlc : vlcInstances) {
            vlc.getStats$().subscribe(s -> sendStats(s), e -> {}, this::endTestSession);
        }
    }

    private void sendStats(PlaybackStats stats) {
        log.info("Sending stats: {}", stats.toString());
        httpClient.toBlocking().retrieve(HttpRequest.POST("/playback/metrics", stats), String.class);
    }

    private void playInstances() {
        for (VlcInstance vlcInstance : vlcInstances) {
            Observable.just(vlcInstance)
                    .subscribeOn(Schedulers.io())
                    .subscribe(vlc -> {
                        startTestSession();
                        vlc.play();
                    });
        }
    }

    private void startTestSession() {
        log.info("Starting test session");
        httpClient.toBlocking().retrieve(HttpRequest.GET("/test/start"), String.class);
    }

    private void endTestSession() {
        log.info("Ending test session");
        httpClient.toBlocking().retrieve(HttpRequest.GET("/test/end"), String.class);
    }

}
