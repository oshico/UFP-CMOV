#include <Servo.h>

// Ultrasonic pins
const int trigPin = 8;
const int echoPin = 7;

// LEDs
const int ledVerde = 3;
const int ledVermelho = 4;

// Servos
const int servoPin = 2;
const int servoPin2 = 6;

Servo cancela;
Servo cancela2;

int distanceThreshold = 100;
long duration;
int cm;
bool cancelaAberta = false;
bool estacionamentoCheio = false;

// Non-blocking delay variables
unsigned long delayStart = 0;
bool waitingToOpen = false;

void setup() {
  Serial.begin(9600);
  
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(ledVerde, OUTPUT);
  pinMode(ledVermelho, OUTPUT);
  
  cancela.attach(servoPin);
  cancela2.attach(servoPin2);
  
  // Cancelas fechadas
  cancela.write(0);
  cancela2.write(180);
  
  digitalWrite(ledVerde, HIGH);
  digitalWrite(ledVermelho, LOW);
  
  Serial.println("SYSTEM_START");
}

void loop() {
  // Ultrasonic trigger
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  duration = pulseIn(echoPin, HIGH, 30000);
  cm = duration * 0.01723;
  
  // Check if 5 seconds have passed and open gate
  if (waitingToOpen && (millis() - delayStart >= 5000)) {
    cancela.write(90);
    cancela2.write(90);
    cancelaAberta = true;
    waitingToOpen = false;
  }
  
  // PARKING FULL
  if (cm > 0 && cm < distanceThreshold) {
    // LEDs respond immediately
    digitalWrite(ledVerde, LOW);
    digitalWrite(ledVermelho, HIGH);
    
    // Start non-blocking delay for gate
    if (!cancelaAberta && !waitingToOpen) {
      delayStart = millis();
      waitingToOpen = true;
    }
    
    // Send message once
    if (!estacionamentoCheio) {
      Serial.println("PARKING_FULL");
      estacionamentoCheio = true;
    }
  } 
  // PARKING AVAILABLE
  else {
    // LEDs respond immediately
    digitalWrite(ledVermelho, LOW);
    digitalWrite(ledVerde, HIGH);
    
    // Close gate immediately
    if (cancelaAberta) {
      cancela.write(0);
      cancela2.write(180);
      cancelaAberta = false;
    }
    
    // Cancel waiting if distance changed
    waitingToOpen = false;
    
    // Send message once
    if (estacionamentoCheio) {
      Serial.println("PARKING_AVAILABLE");
      estacionamentoCheio = false;
    }
  }
  
  delay(200);
}
