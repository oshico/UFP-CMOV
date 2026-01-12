import serial
import requests
import time
import os
from dotenv import load_dotenv

# Load .env into environment variables
load_dotenv()

# Configuration (from environment)
SERIAL_PORT = os.getenv("SERIAL_PORT", "/dev/ttyUSB0")
BAUDRATE = int(os.getenv("BAUDRATE", 9600))

THINGSPEAK_WRITE_KEY = os.getenv("THINGSPEAK_WRITE_KEY")
THINGSPEAK_CHANNEL_ID = os.getenv("THINGSPEAK_CHANNEL_ID")
THINGSPEAK_URL = os.getenv("THINGSPEAK_URL","https://api.thingspeak.com/update")

if not THINGSPEAK_WRITE_KEY or not THINGSPEAK_CHANNEL_ID:
    raise RuntimeError("Missing ThingSpeak configuration in .env")

event_id = 0


def send_to_thingspeak(status):
    global event_id

    try:
        event_id += 1
        timestamp = int(time.time())

        payload = {
            "api_key": THINGSPEAK_WRITE_KEY,
            "field1": event_id,
            "field2": status,
            "field3": timestamp,
        }

        response = requests.post(
            THINGSPEAK_URL, data=payload, timeout=10
        )

        if response.status_code == 200:
            entry_number = response.text.strip()
            print(
                f"Sent to ThingSpeak (Entry #{entry_number}): "
                f"ID={event_id}, Status={status}, Timestamp={timestamp}"
            )
            return True
        else:
            print(
                f"ThingSpeak error: "
                f"{response.status_code} - {response.text}"
            )
            return False

    except requests.exceptions.Timeout:
        print("ThingSpeak timeout - check your internet connection")
        return False
    except Exception as e:
        print(f"Error sending to ThingSpeak: {e}")
        return False


def main():
    global event_id

    print("=" * 50)
    print("Parking Gateway - ThingSpeak Integration")
    print("=" * 50)
    print(f"ThingSpeak Channel ID: {THINGSPEAK_CHANNEL_ID}")
    print(f"Serial Port: {SERIAL_PORT} @ {BAUDRATE} baud")
    print("=" * 50)

    print("\nOpening serial port...")
    try:
        ser = serial.Serial(SERIAL_PORT, BAUDRATE, timeout=1)
        time.sleep(2)
        ser.flushInput()
        print(f"Connected to {SERIAL_PORT}")
    except Exception as e:
        print(f"Failed to open serial port: {e}")
        return

    print("Waiting for data from XBee...\n")
    buffer = ""

    try:
        while True:
            if ser.in_waiting > 0:
                data = ser.read(ser.in_waiting).decode(
                    "utf-8", errors="ignore"
                )
                buffer += data

                print(f"[RAW] '{data}'")
                print(f"[BUFFER] '{buffer}'")

                if "PARKING_FULL" in buffer:
                    print("\n>>> DETECTED: PARKING_FULL")
                    send_to_thingspeak("PARKING_FULL")
                    buffer = ""

                elif "PARKING_AVAILABLE" in buffer:
                    print("\n>>> DETECTED: PARKING_AVAILABLE")
                    send_to_thingspeak("PARKING_AVAILABLE")
                    buffer = ""

                if len(buffer) > 200:
                    buffer = buffer[-100:]

            time.sleep(0.1)

    except KeyboardInterrupt:
        print("\n\nShutting down...")
        ser.close()
        print("Serial port closed")
        print(f"Total events sent: {event_id}")


if __name__ == "__main__":
    main()

