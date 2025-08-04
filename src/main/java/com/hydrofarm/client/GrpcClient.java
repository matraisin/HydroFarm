package com.hydrofarm.client;

import com.hydrofarm.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


import java.util.function.Consumer;

public class GrpcClient {
    private final ManagedChannel channel;
    private final HydroServiceGrpc.HydroServiceStub asyncStub;
    private final HydroServiceGrpc.HydroServiceBlockingStub blockingStub;

    private io.grpc.ClientCall<?, ?> currentCall; // for cancel
    private boolean streaming = false;

    public GrpcClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        asyncStub = HydroServiceGrpc.newStub(channel);
        blockingStub = HydroServiceGrpc.newBlockingStub(channel);
    }

    public void startStreaming(Consumer<SensorData> onDataReceived) {
        if (streaming) return;

        streaming = true;

        // Use a new stub that gives us access to the underlying call
        io.grpc.ClientCall<Empty, SensorData> call = channel.newCall(HydroServiceGrpc.getStreamSensorDataMethod(), io.grpc.CallOptions.DEFAULT);
        currentCall = call;

        asyncStub.streamSensorData(Empty.getDefaultInstance(), new StreamObserver<>() {
            @Override
            public void onNext(SensorData data) {
                onDataReceived.accept(data);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("❌ Stream error: " + t.getMessage());
                streaming = false;
            }

            @Override
            public void onCompleted() {
                System.out.println("✅ Stream completed.");
                streaming = false;
            }
        });
    }

    public void stop() {
        if (currentCall != null) {
            System.out.println("🛑 Cancelling stream...");
            currentCall.cancel("Cancelled by user", null);
            currentCall = null;
        }

        streaming = false;
    }

    public String sendEmergencyShutdown() {
        ShutdownResponse response = blockingStub.emergencyShutdown(Empty.newBuilder().build());
        return response.getStatus();
    }


    public void startEnvironmentStream(Consumer<EnvironmentData> onDataReceived) {
        EnvironmentServiceGrpc.EnvironmentServiceStub envStub = EnvironmentServiceGrpc.newStub(channel);
        envStub.streamEnvironmentData(Empty.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(EnvironmentData data) {
                onDataReceived.accept(data);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("🌡️ Env stream error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("🌿 Env stream completed.");
            }
        });
    }


<<<<<<< HEAD
    //Add pest stream to service
    public void startPestStream(Consumer<PestData> onDataReceived) {
        PestServiceGrpc.PestServiceStub pestStub = PestServiceGrpc.newStub(channel);
        pestStub.streamPestData(Empty.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(PestData data) {
                onDataReceived.accept(data);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("🐛 Pest stream error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("🌿 Pest stream completed.");
            }
        });
    }


    //Tab 4 stream part starts here:
    public void startStorageStream(Consumer<StorageData> onDataReceived) {
        StorageServiceGrpc.StorageServiceStub storageStub = StorageServiceGrpc.newStub(channel);
        storageStub.streamStorageData(Empty.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(StorageData data) {
                onDataReceived.accept(data);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("🛢️ Storage stream error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("📦 Storage stream completed.");
            }
        });
    }



=======
>>>>>>> eae4c1ead4da7d3bd54e70528f5d5bfea77dbf1e
}
