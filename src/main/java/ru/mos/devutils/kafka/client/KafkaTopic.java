package ru.mos.devutils.kafka.client;

import java.util.ArrayList;
import java.util.List;

public final class KafkaTopic {

    private final String name;
    private final List<KafkaPartition> partitions = new ArrayList<>();

    public KafkaTopic(String name, List<KafkaPartition> partitions) {
        this.name = name;
        this.partitions.addAll(partitions);
    }

    public String getName() {
        return name;
    }

    public List<KafkaPartition> getPartitions() {
        return partitions;
    }
}
