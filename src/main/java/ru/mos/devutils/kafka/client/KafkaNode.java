package ru.mos.devutils.kafka.client;

public class KafkaNode {

    private final int id;
    private final String host;
    private final int port;
    private final String rack;

    public KafkaNode(int id, String host, int port, String rack) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.rack = rack;
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getRack() {
        return rack;
    }
}
