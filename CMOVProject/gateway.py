import serial
import requests
import time

SERIAL_PORT = "/dev/ttyUSB0"
BAUDRATE = 9600
ANDROID_URL = "http://127.0.0.1:8080/sensor/update"

print("Opening serial port...")
ser = serial.Serial(SERIAL_PORT, BAUDRATE, timeout=1)
time.sleep(2)  # Give the serial connection time to initialize
ser.flushInput()  # Clear any old data
print(f"Connected to {SERIAL_PORT} at {BAUDRATE} baud")
print("Waiting for data...")

buffer = ""
while True:
    # Try reading any available bytes
    if ser.in_waiting > 0:
        try:
            # Read all available bytes
            data = ser.read(ser.in_waiting).decode('utf-8', errors='ignore')
            buffer += data
            
            print(f"RAW DATA: '{data}'")
            print(f"BUFFER: '{buffer}'")
            
            # Check for our keywords in the buffer
            if "PARKING_FULL" in buffer:
                print(">>> DETECTED: PARKING_FULL")
                payload = {"status": "PARKING_FULL"}
                try:
                    r = requests.post(ANDROID_URL, data=payload, timeout=5)
                    print(f"Sent to Android: {r.status_code} - {r.text}")
                except Exception as e:
                    print(f"HTTP error: {e}")
                buffer = ""  # Clear buffer after processing
                
            elif "PARKING_AVAILABLE" in buffer:
                print(">>> DETECTED: PARKING_AVAILABLE")
                payload = {"status": "PARKING_AVAILABLE"}
                try:
                    r = requests.post(ANDROID_URL, data=payload, timeout=5)
                    print(f"Sent to Android: {r.status_code} - {r.text}")
                except Exception as e:
                    print(f"HTTP error: {e}")
                buffer = ""  # Clear buffer after processing
            
            # Keep buffer from growing too large
            if len(buffer) > 200:
                buffer = buffer[-100:]
                
        except Exception as e:
            print(f"Error reading serial: {e}")
    
    time.sleep(0.1)
