<?php
// Database configuration
$host = "localhost";
$username = "root";
$password = "";
$database = "Live_Telemetry";

// Connect to the database
$conn = new mysqli($host, $username, $password, $database);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Retrieve POST data
$temperature = $_POST['temperature'];
$humidity = $_POST['humidity'];
$water_level = $_POST['water_level'];
$message = $_POST['message'];

// Insert data into the database
$sql = "INSERT INTO telemetry_data (temperature, humidity, water_level, message)
        VALUES ('$temperature', '$humidity', '$water_level', '$message')";

if ($conn->query($sql) === TRUE) {
    echo "Data inserted successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// Close the connection
$conn->close();
?>
