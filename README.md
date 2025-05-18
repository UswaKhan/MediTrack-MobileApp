# MediTrack 
**MediTrack** is an Android application for medicine inventory management designed for small pharmacies and medical stores. It uses **Firebase** as the backend for storing user and medicine data, providing real-time sync and secure authentication.

---

## Features

- User roles: Admin and Cashier
- Add & View Users with Profile Pictures
- Add, Delete & Track Medicines
- Sell Medicines with Customer Info
- Expiry Date Calendar Selection
- Firebase Authentication
- Firebase Realtime Database or Firestore
- Secure Logout Functionality

---

## Tech Stack

| Tech           | Usage                          |
|----------------|--------------------------------|
| **Java**       | Core Android app logic         |
| **XML**        | UI layout design               |
| **Firebase**   | Backend (Auth + Database + Storage) |
| **Android Studio** | IDE for development        |
| **RecyclerView** | For dynamic lists and forms  |

---

## Firebase Setup Instructions

1. Go to [https://console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project: **MediTrack**
3. Enable the following:
   - **Authentication** (Email/Password)
   - **Realtime Database** or **Cloud Firestore**
   - **Firebase Storage**
4. Download `google-services.json` and place it in:
   
app/google-services.json

---

## How to Run the App

1. Clone the repo:
git clone https://github.com/UswaKhan/MediTrack-MobileApp.git

2. Open the project in Android Studio
3. Connect your Firebase project
4. Run on a real device or emulator

---


## Current Modules Implemented

- Add User Form 
- View Users List (with delete)
- Add Medicines
- Sell Medicines (with dynamic list)
- Customer Info & Receipt
- Firebase Auth Integration
- Logout Feature

---

## License

This project is open-source and free to use under the [MIT License](https://opensource.org/licenses/MIT).

---

## Author

Developed by **Uswa Ahmad Khan**  
BSc Computer Science – University of Engineering and Technology  

