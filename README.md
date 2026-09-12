<p align="center">
  <img src="app/src/main/res/drawable/app_logo.png" alt="Ukayearn logo" width="120" />
</p>

<h1 align="center">Ukayearn</h1>

<p align="center">
  A mobile marketplace for discovering, bargaining for, and selling curated thrift finds in Cebu.
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&amp;logoColor=white" />
  <img alt="Language" src="https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&amp;logoColor=white" />
  <img alt="SDK" src="https://img.shields.io/badge/SDK-34-0B57D0" />
  <img alt="Status" src="https://img.shields.io/badge/status-academic%20prototype-F2A900" />
</p>

About the project

Ukayearn is a native Android application that brings the experience of Cebu's ukay-ukay community into one marketplace. Buyers can browse local thrift shops, reserve one-of-a-kind pieces, send a hangyo (price offer), chat with sellers, and complete a simulated cash-on-delivery checkout. Sellers receive a dedicated shop view for managing listings and processing orders.

The project was developed as a mobile development final project under the package com.citu.ukayearn.

Note: Ukayearn is currently a front-end prototype. Users, products, chats, offers, carts, and orders are stored in memory and reset when the app process restarts.

Features

Buyer experience

Create an account or sign in with a demo buyer account

Browse new collections, categories, products, and featured Cebu thrift shops

Search for products and stores, filter by category, and narrow results by price

View detailed product information, seller details, stock, and pricing

Add products to a cart with stock-aware quantity controls

Temporarily lock one-of-a-kind items during checkout

Send custom offers or quick 5%, 10%, and 15% hangyo offers

Apply approved offers to eligible cart items

Chat with sellers using text messages and images

Complete a simulated cash-on-delivery checkout with buyer-protection and delivery fees

Track orders through pending shipment, shipped, and completed states

Edit profile information, profile photo, and delivery address

Seller experience

Sign in through dedicated seeded seller accounts

Use seller-specific bottom navigation and a dedicated listing manager

Add listings with a locally selected product image, description, price, and stock

Mark products as sold out or delete existing listings

Review incoming hangyo offers and approve or decline them

Receive buyer messages and unread-conversation indicators

View pending, shipped, and completed orders

Mark pending orders as shipped for buyer confirmation

Demo accounts

The prototype ships with the following in-memory accounts:

Role

Username

Password

Buyer/Admin

admin

admin123

Buyer

buyer

password

Seller — ThriftKada

thriftkada

seller123

Seller — CebuFinds

cebufinds

seller123

Seller — UkayBoss

ukayboss

seller123

These credentials are for local demonstration only and must not be used in a production application.

Tech stack

Area

Technology

Language

Kotlin 1.9.22

Platform

Native Android

Build system

Gradle 8.4 with Kotlin DSL

Android Gradle Plugin

8.3.0

UI

XML layouts, Fragments, Material Components, RecyclerView

Navigation

AndroidX Navigation Component

Additional UI setup

Jetpack Compose and Material 3 dependencies

Local prototype data

In-memory Kotlin singleton (Database.kt)

Testing

JUnit 4, AndroidX Test, and Espresso

Although Compose is configured in the project, the current application flow is primarily implemented with XML layouts and Fragments.

Application flow

flowchart TD
    A[Splash and authentication] --> B{Account role}
    B -->|Buyer| C[Browse, haggle, chat, and cart]
    B -->|Seller| D[Listings, offers, chat, and orders]
    C --> E[COD checkout and order tracking]
    D --> E
    E --> F[In-memory application state]

Getting started

Prerequisites

A recent version of Android Studio compatible with Android Gradle Plugin 8.3

JDK 17

Android SDK 34

An emulator or physical device running Android 14 / API 34 or newer

The minimum, target, and compile SDK are currently all set to API 34.

Installation

Clone the repository:

git clone https://github.com/koi-frog143/Ukayearn.git
cd Ukayearn

Open the project directory in Android Studio.

Allow Gradle to sync and install any missing SDK components when prompted.

Select an Android 14+ emulator or connected device, then run the app configuration.

No API keys or environment variables are required for the current prototype.

Command-line build

On Windows:

.\gradlew.bat assembleDebug

On macOS or Linux:

bash ./gradlew assembleDebug

The generated debug APK will be placed in app/build/outputs/apk/debug/.

Run tests

On Windows:

.\gradlew.bat testDebugUnitTest

On macOS or Linux:

bash ./gradlew testDebugUnitTest

Project structure

Ukayearn/
├── app/
│   ├── src/main/assets/images/     # Local product, store, and brand images
│   ├── src/main/java/com/citu/ukayearn/
│   │   ├── data/                   # In-memory data source and models
│   │   ├── navigation/             # Navigation-related classes
│   │   ├── ui/components/          # Shared UI components
│   │   ├── ui/screens/             # Auth, buyer, seller, chat, and order screens
│   │   ├── ui/theme/               # Compose theme scaffolding
│   │   └── ui/util/                # Image-loading utility
│   └── src/main/res/
│       ├── drawable/               # Icons and custom backgrounds
│       ├── layout/                 # XML screen and item layouts
│       ├── navigation/             # Fragment navigation graph
│       └── values/                 # Colors, strings, and themes
├── gradle/                         # Version catalog and Gradle wrapper files
├── build.gradle.kts                # Root build configuration
└── settings.gradle.kts             # Project and repository configuration

Current limitations

All application data is held in memory; there is no Room, Firebase, or remote database.

Authentication is local and stores plain-text demo passwords.

New accounts, messages, uploaded image references, carts, and orders do not persist after a process restart.

Checkout, delivery, payments, and buyer protection are simulated; no real transaction is performed.

Product and store content is seeded locally, and the app does not call a marketplace API.

The minimum SDK is API 34, so older Android devices are not supported by the current configuration.

Suggested next steps

Add persistent storage with Room or a production-ready remote backend

Replace local authentication with secure, hashed credentials and session management

Introduce ViewModels and a repository layer for clearer state management

Persist image uploads using app-managed storage or cloud object storage

Add real-time chat, notifications, payment integration, and server-validated inventory locks

Expand unit, navigation, and end-to-end test coverage

Add accessibility checks and support a wider range of Android versions

License

No license file is currently included. Add a license before distributing or accepting external contributions.
