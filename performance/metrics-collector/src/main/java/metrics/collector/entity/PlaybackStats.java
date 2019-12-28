package metrics.collector.entity;

import java.time.Instant;

public class PlaybackStats {

    private String playerId;
    private Double startupDelay;
    private Long receivedFrames;
    private Long receivedSamples;
    private Long lostFrames;
    private Long lostSamples;
    private Instant timestamp;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Double getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(Double startupDelay) {
        this.startupDelay = startupDelay;
    }

    public Long getReceivedFrames() {
        return receivedFrames;
    }

    public void setReceivedFrames(Long receivedFrames) {
        this.receivedFrames = receivedFrames;
    }

    public Long getReceivedSamples() {
        return receivedSamples;
    }

    public void setReceivedSamples(Long receivedSamples) {
        this.receivedSamples = receivedSamples;
    }

    public Long getLostFrames() {
        return lostFrames;
    }

    public void setLostFrames(Long lostFrames) {
        this.lostFrames = lostFrames;
    }

    public Long getLostSamples() {
        return lostSamples;
    }

    public void setLostSamples(Long lostSamples) {
        this.lostSamples = lostSamples;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        if (timestamp != null)
            this.timestamp = Instant.parse(timestamp);
    }

    public Double getValue(String metricName) {
        switch (metricName) {
            case "playback_startup_delay":
                return getStartupDelay();
            case "playback_received_samples":
                return getReceivedSamples().doubleValue();
            case "playback_received_frames":
                return getReceivedFrames().doubleValue();
            case "playback_lost_samples":
                return getLostSamples().doubleValue();
            case "playback_lost_frames":
                return getLostFrames().doubleValue();
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "PlaybackStats{" +
                "playerId='" + playerId + '\'' +
                ", startupDelay=" + startupDelay +
                ", receivedFrames=" + receivedFrames +
                ", receivedSamples=" + receivedSamples +
                ", lostFrames=" + lostFrames +
                ", lostSamples=" + lostSamples +
                ", timestamp=" + timestamp +
                '}';
    }
}
