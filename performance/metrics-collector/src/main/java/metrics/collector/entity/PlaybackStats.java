package metrics.collector.entity;

import java.time.Instant;

public class PlaybackStats {

    private String playerId;
    private Double startupDelay;
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

    @java.lang.Override
    public java.lang.String toString() {
        return "PlaybackStats{" +
                "playerId='" + playerId + '\'' +
                ", startupDelay=" + startupDelay +
                ", lostFrames=" + lostFrames +
                ", lostSamples=" + lostSamples +
                ", timestamp=" + timestamp +
                '}';
    }
}
