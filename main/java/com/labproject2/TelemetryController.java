package com.labproject2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TelemetryController {

    @FXML
    private Label timeStampLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label waterLevelLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private LineChart<Number, Number> chart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final XYChart.Series<Number, Number> temperatureSeries = new XYChart.Series<>();

    private int timeCounter = 0;

    private final String DB_URL = "jdbc:mysql://localhost:3306/live_telemetry";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";

    public void initialize() {

        chart.getData().add(temperatureSeries);
        temperatureSeries.setName("Temperature");
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Pressure (value)");

        // Start updating data every 1 second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTelemetryData()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTelemetryData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM telemetry_data ORDER BY timestamp DESC LIMIT 1")) {

            if (resultSet.next()) {
                String timestamp = resultSet.getString("timestamp");
                String message = resultSet.getString("message");
                float temperature = resultSet.getFloat("temperature");
                float waterLevel = resultSet.getFloat("water_level");
                float humidity = resultSet.getFloat("humidity");


                timeStampLabel.setText(timestamp);
                messageLabel.setText(message);
                temperatureLabel.setText(String.format("%.2f °C", temperature));
                waterLevelLabel.setText(String.format("%.2f %%", waterLevel));
                humidityLabel.setText(String.format("%.2f %%", humidity));


                timeCounter++;
                temperatureSeries.getData().add(new XYChart.Data<>(timeCounter, temperature));


                if (temperatureSeries.getData().size() > 50) {
                    temperatureSeries.getData().remove(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
