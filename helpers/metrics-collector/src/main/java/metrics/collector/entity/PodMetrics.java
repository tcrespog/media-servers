package metrics.collector.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(value = {"kind", "apiVersion","metadata"})
public class PodMetrics {

    private Instant timestamp;
    private Duration window;
    private List<Container> containers;

    public void setTimestamp(String timestamp) {
        this.timestamp = Instant.parse(timestamp);
    }

    public void setWindow(String window) {
        String iso8601Duration = "PT" + window.toUpperCase();
        this.window = Duration.parse(iso8601Duration);
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Duration getWindow() {
        return window;
    }

    public List<Container> getContainers() {
        return containers;
    }

    @Override
    public String toString() {
        return "PodMetrics{" +
                "timestamp=" + timestamp +
                ", window=" + window +
                ", containers=" + containers +
                '}';
    }
}
