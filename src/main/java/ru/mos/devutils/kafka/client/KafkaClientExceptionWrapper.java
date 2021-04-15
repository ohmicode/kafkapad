package ru.mos.devutils.kafka.client;

public class KafkaClientExceptionWrapper extends RuntimeException {

    private Exception rootException;

    public KafkaClientExceptionWrapper(Exception exception) {
        rootException = exception;
    }

    public Exception getCause() {
        return rootException;
    }
}
