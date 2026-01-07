This project structure and README content are designed for your GitHub repository, based on the **Smart Environments Monitoring and Actuation System (SEMAS)** requirements for the **Smart Parking Lot** theme.

***

# Smart Parking Lot: Monitoring and Actuation System (SEMAS)

## Project Overview
This project involves the design and implementation of an integrated system to monitor parking spot availability and provide physical control mechanisms. It combines **IoT edge hardware**, a **cloud-based backend**, and an **Android mobile application** to create a cohesive solution for managing a physical parking environment.

The system focuses on two primary goals:
1.  **Monitoring:** Tracking real-time occupancy using ultrasonic sensors.
2.  **Actuation:** Controlling physical elements like entry gates and "Parking Full" signage.

---

## 🏗 System Architecture
The project is organized into four distinct layers as defined in the SEMAS protocol:

### 1. Edge Layer
*   **Hardware:** Developed using **Arduino with XBee** and/or **LoPy4 with WiFi**.
*   **Sensors:** **Ultrasonic presence sensors** installed at each parking spot to detect vehicle occupancy.
*   **Actuators:** Physical hardware for **gate control** and a **"parking full" sign**.

### 2. Fog Gateway Layer (if applicable)
*   For the Arduino+XBee ecosystem, a **gateway** is implemented to translate wireless signals to **MQTT/HTTP** for communication with the cloud backend.

### 3. Cloud Backend
*   **Ingestion:** Handles data incoming via MQTT/HTTP.
*   **Storage & Dispatch:** Responsible for storing environment states and **dispatching actuation commands** from the mobile app back to the edge nodes.
*   **Synchronization:** Serves as the central hub for data synchronization with the mobile layer.

### 4. Mobile Layer (Android)
*   **Architecture:** A native Android application with a local **Room/SQLite database** for persisting data about parking spots, sensors, and actuators.
*   **Visualization:** Features a **map visualization** of the parking lot and displays the **real-time state and historic info** for all components.
*   **Control:** Provides a GUI to issue **actuation commands** to trigger changes in the physical environment (e.g., opening the gate).

---

## 🛠 Tech Stack
*   **IoT:** Arduino, XBee, LoPy4, Ultrasonic Sensors.
*   **Connectivity:** WiFi, MQTT, HTTP (Pull & Push).
*   **Mobile:** Android SDK, Room/SQLite for local persistence.
*   **Documentation:** IEEE-format research paper and technical presentations.

---

## 📂 Project Structure
*   `/android`: Source code for the Android application, including SQLite schemas and UI tests.
*   `/edge-hardware`: Firmware for Arduino/LoPy4 nodes and gateway logic.
*   `/backend`: Cloud ingestion and storage implementation.
*   `/docs`: 
    *   **CMOV:** IEEE-format paper covering architecture and SOTA.
    *   **PAMO:** Project presentation including functional requirements and screenshots.

---

## 🚀 Key Features
*   **Real-time Synchronization:** The Android app synchronizes with the backend via WiFi using HTTP pull and push logic.
*   **Map-Based UI:** Users can visualize the specific location and status of every parking spot.
*   **History Tracking:** Logged history for every sensor trigger and actuation event.
*   **Physical Feedback:** Real-time actuation allows the app to physically open gates or update signs in the parking lot.

***

**Analogy for Understanding:**
Building this project is like creating a **smart nerve system for a parking garage**. The **ultrasonic sensors** are the eyes that see cars, the **Cloud** is the memory that remembers where they are, and the **Android App** is the remote control that allows you to see the whole map and physically open the gate with a touch.
