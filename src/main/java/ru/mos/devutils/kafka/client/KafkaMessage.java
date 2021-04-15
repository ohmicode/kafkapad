package ru.mos.devutils.kafka.client;

import java.util.HashMap;
import java.util.Map;

public final class KafkaMessage {

    private final long timestamp;
    private final String timestampType;
    private final Map<String, String> headers = new HashMap<>();
    private final int partition;
    private final long offset;
    private final String key;
    private final String value;

    public KafkaMessage(long timestamp, String timestampType, Map<String, String> headers, int partition, long offset, String key, String value) {
        this.timestamp = timestamp;
        this.timestampType = timestampType;
        this.headers.putAll(headers);
        this.partition = partition;
        this.offset = offset;
        this.key = key;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampType() {
        return timestampType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
