package metrics.collector.entity;

import java.time.Instant;

public class PlaybackStats {

    private String playerId;
    private Double startupDelay;
    private Long receivedFrames;
    private Long lostFrames;
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

    public Long getLostFrames() {
        return lostFrames;
    }

    public void setLostFrames(Long lostFrames) {
        this.lostFrames = lostFrames;
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
            case "playback_received_frames":
                return (getReceivedFrames() != null) ? getReceivedFrames().doubleValue() : null;
            case "playback_lost_frames":
                return (getLostFrames() != null) ? getLostFrames().doubleValue() : null;
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
                ", lostFrames=" + lostFrames +
                ", timestamp=" + timestamp +
                '}';
    }
}
