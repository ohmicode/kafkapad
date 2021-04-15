package ru.mos.devutils.kafka.client;

import java.util.ArrayList;
import java.util.List;

public final class KafkaPartition {

    private final int partition;
    private final KafkaNode leader;
    private final List<KafkaNode> replicas = new ArrayList<>();
    private final List<KafkaNode> inSyncReplicas = new ArrayList<>();
    private final List<KafkaNode> offlineReplicas = new ArrayList<>();

    public KafkaPartition(int partition, KafkaNode leader, List<KafkaNode> replicas, List<KafkaNode> inSyncReplicas, List<KafkaNode> offlineReplicas) {
        this.partition = partition;
        this.leader = leader;
        this.replicas.addAll(replicas);
        this.inSyncReplicas.addAll(inSyncReplicas);
        this.offlineReplicas.addAll(offlineReplicas);
    }

    public int getPartition() {
        return partition;
    }

    public KafkaNode getLeader() {
        return leader;
    }

    public List<KafkaNode> getReplicas() {
        return replicas;
    }

    public List<KafkaNode> getInSyncReplicas() {
        return inSyncReplicas;
    }

    public List<KafkaNode> getOfflineReplicas() {
        return offlineReplicas;
    }
}
