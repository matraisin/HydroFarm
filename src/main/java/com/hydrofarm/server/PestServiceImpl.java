package com.hydrofarm.server;

import com.hydrofarm.grpc.*;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PestServiceImpl extends PestServiceGrpc.PestServiceImplBase {
    private final Random random = new Random();

    @Override
    public void streamPestData(Empty request, StreamObserver<PestData> responseObserver) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            PestData data = PestData.newBuilder()
                    .setTimestamp(LocalDateTime.now().toString())
                    .setPestLevel(random.nextInt(101)) // 0–100
                    .setStrainHealth(50 + random.nextInt(51)) // 50–100
                    .build();

            responseObserver.onNext(data);
        }, 0, 5, TimeUnit.SECONDS);
    }
}
