package com.hydrofarm.client;

import com.hydrofarm.grpc.SensorData;

import javax.swing.*;
import java.awt.*;
import com.hydrofarm.grpc.EnvironmentData;


public class HydroponicsSwingApp {
    private JFrame frame;
    private GrpcClient grpcClient;

    // Labels for nutrient tab
    private JLabel tempLabel, ppmLabel, phLabel;

    // Future: other subsystem labels go here
    private JLabel envTempLabel, envHumidityLabel, envCo2Label;
    // e.g., JLabel envTempLabel, co2Label, etc.

    public HydroponicsSwingApp() {
        grpcClient = new GrpcClient("localhost", 9090);
        initUI();
    }

    private void initUI() {
        frame = new JFrame("HydroFarm Monitoring App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nutrients", createNutrientPanel());
        tabbedPane.addTab("Environment", createEnvironmentPanel());

        tabbedPane.addTab("Pests & Strains", createPlaceholder("Pest control system coming soon..."));
        tabbedPane.addTab("Storage", createPlaceholder("Storage and pump system coming soon..."));

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Create Nutrient Monitoring Panel
    private JPanel createNutrientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel dataPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        tempLabel = new JLabel("Temperature: -- °C");
        ppmLabel = new JLabel("PPM: --");
        phLabel = new JLabel("pH: --");

        dataPanel.add(tempLabel);
        dataPanel.add(ppmLabel);
        dataPanel.add(phLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start Stream");
        JButton stopButton = new JButton("Stop");
        JButton refreshButton = new JButton("Refresh");
        JButton emergencyButton = new JButton("Emergency Shutdown");

        // Stream button logic
        startButton.addActionListener(e -> grpcClient.startStreaming(this::updateSensorUI));
        stopButton.addActionListener(e -> grpcClient.stop());
        refreshButton.addActionListener(e -> {
            // Simulate manual refresh if needed
            System.out.println("🔁 Manual refresh not implemented (streaming handles updates).");
        });

        emergencyButton.addActionListener(e -> {
            String result = grpcClient.sendEmergencyShutdown();
            JOptionPane.showMessageDialog(frame, result, "Emergency Shutdown", JOptionPane.WARNING_MESSAGE);
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(emergencyButton);

        panel.add(dataPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createEnvironmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        envTempLabel = new JLabel("Temperature: -- °C");
        envHumidityLabel = new JLabel("Humidity: -- %");
        envCo2Label = new JLabel("CO₂: -- ppm");

        JPanel dataPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        dataPanel.add(envTempLabel);
        dataPanel.add(envHumidityLabel);
        dataPanel.add(envCo2Label);

        JButton startEnvButton = new JButton("Start Env Stream");
        startEnvButton.addActionListener(e -> grpcClient.startEnvironmentStream(this::updateEnvironmentUI));

        panel.add(dataPanel, BorderLayout.CENTER);
        panel.add(startEnvButton, BorderLayout.SOUTH);

        return panel;
    }




    // Placeholder panels for future tabs
    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(text));
        return panel;
    }

    // Here we update mock data to view
    private void updateSensorUI(SensorData data) {
        SwingUtilities.invokeLater(() -> {
            tempLabel.setText(String.format("Temperature: %.2f °C", data.getTemperature()));
            ppmLabel.setText(String.format("PPM: %.2f", data.getHumidity())); // Using humidity as fake PPM for now
            phLabel.setText(String.format("pH: %.2f", data.getPH()));
        });

    }

    private void updateEnvironmentUI(com.hydrofarm.grpc.EnvironmentData data) {
        SwingUtilities.invokeLater(() -> {
            envTempLabel.setText(String.format("Temperature: %.2f °C", data.getTemperature()));
            envHumidityLabel.setText(String.format("Humidity: %.2f %%", data.getHumidity()));
            envCo2Label.setText(String.format("CO₂: %.2f ppm", data.getCo2()));
        });
    }


    // Launch GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HydroponicsSwingApp::new);
    }
}
