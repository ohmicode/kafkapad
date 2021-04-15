FROM openjdk:8

RUN mkdir -p /opt/kafka_pad/
COPY build/libs/kafka_pad-1.0-SNAPSHOT.jar /opt/kafka_pad/kafka_pad.jar

WORKDIR /opt/kafka_pad
ENTRYPOINT ["java","-jar","kafka_pad.jar"]
