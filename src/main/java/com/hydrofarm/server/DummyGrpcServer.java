package com.hydrofarm.server;

import com.google.common.util.concurrent.MoreExecutors;
import com.hydrofarm.grpc.*;
import io.grpc.Context;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DummyGrpcServer {

    private Server server;

    public void start() throws IOException, InterruptedException {
        server = ServerBuilder.forPort(9090)
                .addService(new HydroServiceImpl())
                .addService(new EnvironmentServiceImpl())
                .build()
                .start();

        System.out.println("✅ gRPC Server started on port 9090");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutting down gRPC server...");
            if (server != null) {
                server.shutdown();
            }
        }));

        server.awaitTermination();
    }

    // ✅ Correct base class and method names
    static class HydroServiceImpl extends HydroServiceGrpc.HydroServiceImplBase {
        private final Random random = new Random();

        @Override
        public void streamSensorData(Empty request, StreamObserver<SensorData> responseObserver) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
                SensorData data = SensorData.newBuilder()
                        .setTimestamp(LocalDateTime.now().toString())
                        .setTemperature(18 + random.nextFloat() * 10)
                        .setHumidity(40 + random.nextFloat() * 20)
                        .setPH(5 + random.nextFloat() * 2)
                        .build();

                responseObserver.onNext(data);
            }, 0, 5, TimeUnit.SECONDS);

            // Add cancellation support
            Context.current().addListener(context -> {
                System.out.println("❌ Client cancelled stream. Stopping scheduler.");
                future.cancel(false); // Stop the periodic task
                scheduler.shutdown();
            }, MoreExecutors.directExecutor());
        }

        @Override
        public void emergencyShutdown(Empty request, StreamObserver<ShutdownResponse> responseObserver) {
            System.out.println("🚨 Emergency Shutdown requested!");
            ShutdownResponse response = ShutdownResponse.newBuilder()
                    .setStatus("Irrigation system shut down successfully!")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
