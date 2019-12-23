package metrics.collector.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Container {

    private static final Pattern pattern = Pattern.compile("(\\d+)(\\w*)");

    private String name;
    private Double memory;
    private Double cpu;

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("usage")
    private void readUsage(Map<String, String> usage) {
        memory = readMemoryInMi(usage.get("memory"));
        cpu = readCpuInMs(usage.get("cpu"));
    }

    private Double readMemoryInMi(String rawMemory) {
        Matcher matcher = pattern.matcher(rawMemory);
        if (!matcher.matches()) {
            return null;
        }

        Double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        if (unit.equals("Ki")) {
            return number / 1024;
        } else if (unit.equals("Mi")) {
            return number;
        } else if (unit.equals("Gi")) {
            return number * 1024;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Double readCpuInMs(String rawCpu) {
        Matcher matcher = pattern.matcher(rawCpu);
        if (!matcher.matches()) {
            return null;
        }

        Double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        if (unit.equals("n")) {
            return number / 1_000_000;
        } else if (unit.equals("u")) {
            return number / 1000;
        } else if (unit.equals("")) {
            return number * 1000;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getName() {
        return name;
    }

    public Double getMemory() {
        return memory;
    }

    public Double getCpu() {
        return cpu;
    }

    @Override
    public String toString() {
        return "Container{" +
                "name='" + name + '\'' +
                ", memory=" + memory +
                ", cpu=" + cpu +
                '}';
    }
}
