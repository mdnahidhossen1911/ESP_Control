#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#ifndef APSSID
#define APSSID "ESPap"
#define APPSK "ESP9876543210"
#endif

// Set these to your desired credentials.
const char *ssid = APSSID;
const char *password = APPSK;

ESP8266WebServer server(80);
const int ledPin = D4; // Change as necessary

void handleRoot() {
  String html = "<h1>You are connected</h1>";
  html += "<button onclick=\"location.href='/on'\">Turn ON</button><br>";
  html += "<button onclick=\"location.href='/off'\">Turn OFF</button><br>";
  html += "<button onclick=\"location.href='/status'\">Check Status</button>";
  server.send(200, "text/html", html);
}

void handleLEDOn() {
  digitalWrite(ledPin, HIGH);
  server.send(200, "text/html", "<h1>LED is ON</h1><button onclick=\"location.href='/'\">Back</button>");
}

void handleLEDOff() {
  digitalWrite(ledPin, LOW);
  server.send(200, "text/html", "<h1>LED is OFF</h1><button onclick=\"location.href='/'\">Back</button>");
}

void handleStatus() {
  String status = digitalRead(ledPin) == HIGH ? "ON" : "OFF";
  String json = "{\"status\": \"" + status + "\"}";
  server.send(200, "application/json", json);
}

void setup() {
  delay(1000);
  Serial.begin(115200);
  Serial.println();
  Serial.print("Configuring access point...");
  
  // Set up the LED pin
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW); // Ensure LED is off initially

  WiFi.softAP(ssid, password);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  
  // Define routes
  server.on("/", handleRoot);
  server.on("/on", handleLEDOn);
  server.on("/off", handleLEDOff);
  server.on("/status", handleStatus);
  
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
}
