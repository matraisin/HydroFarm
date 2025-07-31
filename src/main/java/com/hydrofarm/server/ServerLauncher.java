package com.hydrofarm.server;

public class ServerLauncher {
    public static void main(String[] args) throws Exception {
        DummyGrpcServer server = new DummyGrpcServer();
        server.start();
    }
}
