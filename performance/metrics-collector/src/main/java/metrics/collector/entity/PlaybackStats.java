package metrics.collector.entity;

public class PlaybackStats {

    private String playerId;
    private Double startupDelay;
    private Long droppedFrames;
    private Long stalls;

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

    @Override
    public String toString() {
        return "PlaybackStats{" +
                "playerId='" + playerId + '\'' +
                ", startupDelay=" + startupDelay +
                ", droppedFrames=" + droppedFrames +
                ", stalls=" + stalls +
                '}';
    }
}
