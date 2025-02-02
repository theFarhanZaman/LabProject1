#include <WiFi.h>
#include <HTTPClient.h>
#include <DHT.h>

#define DHTPIN 21         // GPIO pin connected to the DHT11
#define DHTTYPE DHT11    
#define WATER_LEVEL_PIN 4 // GPIO pin connected to the water level sensor

DHT dht(DHTPIN, DHTTYPE);

// Wi-Fi credentials
const char* ssid = "realme 9";
const char* password = "12345678";

// Server endpoint
const char* serverUrl = "http:// 192.168.53.156/Telemetry/connect.php";

void setup() {
  Serial.begin(115200);
  dht.begin();

  pinMode(WATER_LEVEL_PIN, INPUT);

  // Connect to Wi-Fi
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");
}

void loop() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int waterLevel = analogRead(WATER_LEVEL_PIN); // Read water level sensor
  float waterPercentage = (waterLevel / 4095.0) * 100; // Convert to percentage
  
  String message = (waterPercentage > 3.0) ? "Leak Detected" : "No Leak, System is Safe";

  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");

    String postData = "temperature=" + String(temperature) +
                      "&humidity=" + String(humidity) +
                      "&water_level=" + String(waterPercentage) +
                      "&message=" + message;

    int httpResponseCode = http.POST(postData);
    if (httpResponseCode > 0) {
      Serial.println("Data sent successfully.");
      Serial.println("Response: " + http.getString());
    } else {
      Serial.print("Error sending data: ");
      Serial.println(httpResponseCode);
    }

    http.end();
  } else {
    Serial.println("WiFi disconnected, reconnecting...");
    WiFi.begin(ssid, password);
  }

  delay(1000); 
}
