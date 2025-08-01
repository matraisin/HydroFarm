package com.hydrofarm.client;

import com.hydrofarm.grpc.SensorData;

public class ClientLauncher {
    public static void main(String[] args) {
        GrpcClient client = new GrpcClient("localhost", 9090);

        client.startStreaming(data -> {
            System.out.printf("📡 %s | Temp: %.2f | Humidity: %.2f | pH: %.2f%n",
                    data.getTimestamp(), data.getTemperature(), data.getHumidity(), data.getPH());
        });

        // Block the main thread so streaming continues
        try {
            System.out.println("📶 Streaming started. Press ENTER to exit...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }
}

