package com.hydrofarm.server;

import com.hydrofarm.grpc.*;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EnvironmentServiceImpl extends EnvironmentServiceGrpc.EnvironmentServiceImplBase {
    private final Random random = new Random();

    @Override
    public void streamEnvironmentData(Empty request, StreamObserver<EnvironmentData> responseObserver) {
        // Send fake environmental data every 5 seconds
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            EnvironmentData data = EnvironmentData.newBuilder()
                    .setTimestamp(LocalDateTime.now().toString())
                    .setTemperature(20 + random.nextFloat() * 10)
                    .setHumidity(40 + random.nextFloat() * 20)
                    .setCo2(300 + random.nextFloat() * 200)
                    .build();

            responseObserver.onNext(data);
        }, 0, 5, TimeUnit.SECONDS);
    }
}

