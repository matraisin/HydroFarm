package com.hydrofarm.server;

import com.hydrofarm.grpc.*;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StorageServiceImpl extends StorageServiceGrpc.StorageServiceImplBase {
    private final Random random = new Random();

    @Override
    public void streamStorageData(Empty request, StreamObserver<StorageData> responseObserver) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            float capacity = 20 + random.nextFloat() * 80; // simulate 20–100%
            String pumpStatus = random.nextBoolean() ? "ON" : "OFF";

            StorageData data = StorageData.newBuilder()
                    .setTimestamp(LocalDateTime.now().toString())
                    .setCapacityPercent(capacity)
                    .setPumpStatus(pumpStatus)
                    .build();

            responseObserver.onNext(data);
        }, 0, 5, TimeUnit.SECONDS);
    }
}

