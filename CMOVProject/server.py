from flask import Flask, request, jsonify
import requests
import serial
import time
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

THINGSPEAK_READ_KEY = os.getenv("THINGSPEAK_READ_KEY")
THINGSPEAK_WRITE_KEY = os.getenv("THINGSPEAK_WRITE_KEY")
THINGSPEAK_CHANNEL_ID = os.getenv("THINGSPEAK_CHANNEL_ID")
THINGSPEAK_BASE_URL = os.getenv("THINGSPEAK_BASE_URL","https://api.thingspeak.com")

SERIAL_PORT = os.getenv("SERIAL_PORT", "/dev/ttyUSB0")
BAUDRATE = int(os.getenv("BAUDRATE", 9600))

FLASK_HOST = os.getenv("FLASK_HOST", "0.0.0.0")
FLASK_PORT = int(os.getenv("FLASK_PORT", 8080))

required = [
    THINGSPEAK_READ_KEY,
    THINGSPEAK_WRITE_KEY,
    THINGSPEAK_CHANNEL_ID,
]
if not all(required):
    raise RuntimeError("Missing ThingSpeak configuration in .env")

ser = None


def init_serial():
    global ser
    try:
        ser = serial.Serial(SERIAL_PORT, BAUDRATE, timeout=1)
        time.sleep(2)
        ser.flushInput()
        print(f"Serial connected: {SERIAL_PORT}")
        return True
    except Exception as e:
        print(f"Serial connection failed: {e}")
        return False


@app.route("/sensor/update", methods=["POST"])
def sensor_update():
    try:
        status = request.form.get("status") or (
            request.json.get("status") if request.is_json else None
        )

        if not status:
            return jsonify({"error": "Missing 'status' parameter"}), 400

        timestamp = int(time.time())
        payload = {
            "api_key": THINGSPEAK_WRITE_KEY,
            "field1": timestamp,
            "field2": status,
            "field3": timestamp,
        }

        response = requests.post(
            f"{THINGSPEAK_BASE_URL}/update",
            data=payload,
            timeout=10,
        )

        if response.status_code == 200:
            return jsonify({"message": "ok", "entry": response.text}), 200
        else:
            return jsonify(
                {"error": "ThingSpeak error", "details": response.text}
            ), 500

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/sensor/history", methods=["GET"])
def sensor_history():
    try:
        results = request.args.get("results", 100)

        response = requests.get(
            f"{THINGSPEAK_BASE_URL}/channels/{THINGSPEAK_CHANNEL_ID}/feeds.json",
            params={
                "api_key": THINGSPEAK_READ_KEY,
                "results": results,
            },
            timeout=10,
        )

        if response.status_code != 200:
            return jsonify({"error": "Failed to fetch from ThingSpeak"}), 500

        feeds = response.json().get("feeds", [])
        events = []

        for feed in feeds:
            event_id = feed.get("field1", "")
            status = feed.get("field2", "UNKNOWN")
            timestamp = feed.get("field3", "")

            try:
                timestamp_ms = int(float(timestamp) * 1000) if timestamp else 0
            except:
                timestamp_ms = 0

            events.append(
                {
                    "id": int(event_id) if event_id else 0,
                    "status": status,
                    "timestamp": timestamp_ms,
                }
            )

        return jsonify(events), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/sensor/latest", methods=["GET"])
def sensor_latest():
    try:
        response = requests.get(
            f"{THINGSPEAK_BASE_URL}/channels/{THINGSPEAK_CHANNEL_ID}/feeds/last.json",
            params={"api_key": THINGSPEAK_READ_KEY},
            timeout=10,
        )

        if response.status_code != 200:
            return jsonify({"error": "Failed to fetch from ThingSpeak"}), 500

        data = response.json()

        event_id = data.get("field1", "")
        status = data.get("field2", "UNKNOWN")
        timestamp = data.get("field3", "")

        try:
            timestamp_ms = int(float(timestamp) * 1000) if timestamp else 0
        except:
            timestamp_ms = 0

        return jsonify(
            {
                "id": int(event_id) if event_id else 0,
                "status": status,
                "timestamp": timestamp_ms,
            }
        ), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/actuate/reset", methods=["POST"])
def actuate_reset():
    try:
        global ser

        if ser is None or not ser.is_open:
            if not init_serial():
                return jsonify({"error": "Serial port not available"}), 500

        ser.write(b"RESET\n")
        print("✓ RESET command sent to Arduino")

        return jsonify({"message": "ACTUATION SENT"}), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/health", methods=["GET"])
def health():
    thingspeak_ok = False
    try:
        response = requests.get(
            f"{THINGSPEAK_BASE_URL}/channels/{THINGSPEAK_CHANNEL_ID}/feeds/last.json",
            params={"api_key": THINGSPEAK_READ_KEY},
            timeout=5,
        )
        thingspeak_ok = response.status_code == 200
    except:
        pass

    return jsonify(
        {
            "status": "ok",
            "serial_connected": ser is not None and ser.is_open,
            "thingspeak_connected": thingspeak_ok,
            "channel_id": THINGSPEAK_CHANNEL_ID,
        }
    ), 200


if __name__ == "__main__":
    print("=" * 50)
    print("Flask API Server for Android App")
    print("=" * 50)
    print(f"ThingSpeak Channel: {THINGSPEAK_CHANNEL_ID}")
    print(f"Serial Port: {SERIAL_PORT}")
    print("=" * 50)

    init_serial()

    print(f"\nStarting Flask server on http://{FLASK_HOST}:{FLASK_PORT}\n")
    app.run(host=FLASK_HOST, port=FLASK_PORT, debug=False)
