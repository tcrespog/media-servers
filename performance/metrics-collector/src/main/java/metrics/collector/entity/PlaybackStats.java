package metrics.collector.entity;

import java.time.Instant;

public class PlaybackStats {

    private String playerId;
    private Double startupDelay;
    private Long droppedFrames;
    private Long stalls;
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

    public Long getDroppedFrames() {
        return droppedFrames;
    }

    public void setDroppedFrames(Long droppedFrames) {
        this.droppedFrames = droppedFrames;
    }

    public Long getStalls() {
        return stalls;
    }

    public void setStalls(Long stalls) {
        this.stalls = stalls;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        if (timestamp != null)
            this.timestamp = Instant.parse(timestamp);
    }

    @Override
    public String toString() {
        return "PlaybackStats{" +
                "playerId='" + playerId + '\'' +
                ", startupDelay=" + startupDelay +
                ", droppedFrames=" + droppedFrames +
                ", stalls=" + stalls +
                ", timestamp=" + timestamp +
                '}';
    }
}
