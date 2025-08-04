package com.hydrofarm.client;

import com.hydrofarm.grpc.PestData;
import com.hydrofarm.grpc.SensorData;


import javax.swing.*;
import java.awt.*;
import com.hydrofarm.grpc.EnvironmentData;
import com.hydrofarm.grpc.StorageData;


public class HydroponicsSwingApp {
    //Jframe
    private JFrame frame;
    //Instanciate and declare client object
    private GrpcClient grpcClient;
    //labels
    private JLabel pestLevelLabel, strainHealthLabel;
    private JLabel storageLevelLabel, pumpStatusLabel, airConditionLabel;
    //flags for alerts to make them not spaming
    private boolean pestAlertShown = false;
    private boolean isShutdown = false;



    //Add tsyling to streammed data:
    Font bigFont = new Font("SansSerif", Font.BOLD, 24);


    // Labels for nutrient tab
    private JLabel tempLabel, ppmLabel, phLabel;

    // Future: other subsystem labels go here
    private JLabel envTempLabel, envHumidityLabel, envCo2Label;
    // e.g., JLabel envTempLabel, co2Label, etc.

    public HydroponicsSwingApp() {
        grpcClient = new GrpcClient("localhost", 9090);
        initUI();
    }

    //Generate a GUI, main function starts here
    private void initUI() {
        frame = new JFrame("HydroFarm Monitoring App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        //Nutrient tab
        tabbedPane.addTab("Nutrients", createNutrientPanel());
        //Enviroment tab
        tabbedPane.addTab("Environment", createEnvironmentPanel());
        //Pest tab
        tabbedPane.addTab("Pests & Strains", createPestPanel());
        //Styorage tab
        tabbedPane.addTab("Storage", createStoragePanel());

        //Shutdown button in hydro tab
        tabbedPane.addTab("Emergency Button", createHydroPanel());

        //tabbedPane.addTab("Storage", createPlaceholder("Storage and pump system coming soon..."));

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Create Nutrient Monitoring Panel
    private JPanel createNutrientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel dataPanel = new JPanel(new GridLayout(3, 2, 10, 10));


        tempLabel = new JLabel("Temperature: -- °C");
        tempLabel.setFont(bigFont);

        ppmLabel = new JLabel("PPM: --");
        ppmLabel.setFont(bigFont);

        phLabel = new JLabel("pH: --");
        phLabel.setFont(bigFont);

        dataPanel.add(tempLabel);
        dataPanel.add(ppmLabel);
        dataPanel.add(phLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start Stream");
        JButton emergencyButton = new JButton("Emergency Shutdown");

        // Stream button logic
        startButton.addActionListener(e -> grpcClient.startStreaming(this::updateSensorUI));


        emergencyButton.addActionListener(e -> {
            String result = grpcClient.sendEmergencyShutdown();
            JOptionPane.showMessageDialog(frame, result, "Emergency Shutdown", JOptionPane.WARNING_MESSAGE);
        });

        buttonPanel.add(startButton);
        //remove button for now
        //buttonPanel.add(emergencyButton);

        panel.add(dataPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createEnvironmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        envTempLabel = new JLabel("Temperature: -- °C");
        envTempLabel.setFont(bigFont);
        envHumidityLabel = new JLabel("Humidity: -- %");
        envHumidityLabel.setFont(bigFont);
        envCo2Label = new JLabel("CO₂: -- ppm");
        envCo2Label.setFont(bigFont);

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
            float temperature = data.getTemperature();
            if (temperature > 27.0f) {
                tempLabel.setForeground(Color.RED);
                tempLabel.setText(String.format("Temperature: %.2f °C (HIGH!)", temperature));
            } else {
                tempLabel.setForeground(Color.BLACK);
                tempLabel.setText(String.format("Temperature: %.2f °C", temperature));
            }

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

    //Create pest controll panel
    private JPanel createPestPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        pestLevelLabel = new JLabel("Pest Level: --");
        pestLevelLabel.setFont(bigFont);
        strainHealthLabel = new JLabel("Strain Health: --");
        strainHealthLabel.setFont(bigFont);

        JPanel dataPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        dataPanel.add(pestLevelLabel);
        dataPanel.add(strainHealthLabel);

        JButton startPestButton = new JButton("Start Pest Stream");
        startPestButton.addActionListener(e -> grpcClient.startPestStream(this::updatePestUI));

        panel.add(dataPanel, BorderLayout.CENTER);
        panel.add(startPestButton, BorderLayout.SOUTH);

        return panel;
    }


    //extra method for updating pest UI
    private void updatePestUI(PestData data) {
        SwingUtilities.invokeLater(() -> {
            int level = data.getPestLevel();
            String pestLevelDesc;
            if (level == 0) pestLevelDesc = "No pests";
            else if (level <= 30) pestLevelDesc = "Low";
            else if (level <= 70) pestLevelDesc = "Moderate";
            else pestLevelDesc = "High";

            pestLevelLabel.setText("Pest Level: " + pestLevelDesc);

            // Alert only once unless level drops back
            if (level > 70) {
                pestLevelLabel.setForeground(Color.RED);
                if (!pestAlertShown) {
                    JOptionPane.showMessageDialog(null, "⚠️ High Pest Level Detected!", "Pest Alert", JOptionPane.WARNING_MESSAGE);
                    pestAlertShown = true;
                }
            } else {
                pestLevelLabel.setForeground(Color.BLACK);
                pestAlertShown = false;  // reset when conditions return to normal
            }

            int health = data.getStrainHealth();
            String strainHealthDesc;
            if (health <= 30) strainHealthDesc = "Poor";
            else if (health <= 70) strainHealthDesc = "Fair";
            else strainHealthDesc = "Healthy";

            strainHealthLabel.setText("Strain Health: " + strainHealthDesc);
        });
    }








    //Storage panel starts here
    private JPanel createStoragePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        storageLevelLabel = new JLabel("Storage Level: -- %");
        storageLevelLabel.setFont(bigFont);
//        pumpStatusLabel = new JLabel("Pump Status: --");
//        pumpStatusLabel.setFont(bigFont);
        airConditionLabel = new JLabel("AirCOn Status: --");
        airConditionLabel.setFont(bigFont);

        JPanel dataPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        dataPanel.add(storageLevelLabel);
        //dataPanel.add(pumpStatusLabel);
        dataPanel.add(airConditionLabel);


        JButton startStorageButton = new JButton("Start Storage Stream");
        startStorageButton.addActionListener(e -> grpcClient.startStorageStream(this::updateStorageUI));

        panel.add(dataPanel, BorderLayout.CENTER);
        panel.add(startStorageButton, BorderLayout.SOUTH);

        return panel;
    }

    //Update storage method:
    private void updateStorageUI(StorageData data) {
        SwingUtilities.invokeLater(() -> {
            storageLevelLabel.setText(String.format("Storage Level: %.2f %%", data.getCapacityPercent()));
            airConditionLabel.setText("AirCon Status: " + data.getPumpStatus());
        });
    }




    //Shut down method
    private JPanel createHydroPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton emergencyButton = new JButton("Emergency Shutdown");
        emergencyButton.setFont(bigFont);
        emergencyButton.setBackground(Color.RED);
        emergencyButton.setForeground(Color.WHITE);
        emergencyButton.setFocusPainted(false);

        emergencyButton.addActionListener(e -> {
            if (isShutdown) return;

            // try call gRPC here next
            // try {
            //     String result = grpcClient.emergencyShutdown();
            //     JOptionPane.showMessageDialog(null, result, "Shutdown", JOptionPane.INFORMATION_MESSAGE);
            // } catch (Exception ex) {
            //     JOptionPane.showMessageDialog(null, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            //     return;
            // }

            // Simulate local state change
            isShutdown = true;
            emergencyButton.setText("System Turned Off");
            emergencyButton.setEnabled(false);
            emergencyButton.setBackground(Color.DARK_GRAY);
            emergencyButton.setForeground(Color.LIGHT_GRAY);
        });

        panel.add(emergencyButton, BorderLayout.CENTER);
        return panel;
    }






    // Launch GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HydroponicsSwingApp::new);
    }
}
