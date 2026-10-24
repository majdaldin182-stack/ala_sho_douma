import csv
import os
import firebase_admin
from firebase_admin import credentials, firestore

CSV_FILE = "app/pharmacy_duties_october_2026.csv"

if not firebase_admin._apps:
    service_account_json = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
    if not service_account_json:
        raise RuntimeError("Missing FIREBASE_SERVICE_ACCOUNT")

    import json
    cred = credentials.Certificate(json.loads(service_account_json))
    firebase_admin.initialize_app(cred)

db = firestore.client()

with open(CSV_FILE, newline="", encoding="utf-8-sig") as file:
    reader = csv.DictReader(file)

    for row in reader:
        name = row.get("name", "").strip()
        address = row.get("address", "").strip()
        date = row.get("date", "").strip()
        days = row.get("days", "").strip()

        if not name or not date:
            continue

        doc_id = f"{date.replace('/', '-')}_{name}"
        db.collection("pharmacies").document(doc_id).set({
            "name": name,
            "address": address,
            "date": date,
            "days": days,
            "startTime": "21:00",
            "endTime": "00:00",
            "active": True
        }, merge=True)

print("Pharmacy duties imported successfully.")
