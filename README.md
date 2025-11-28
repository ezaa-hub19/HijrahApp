# 📖 HijrahApp | Your Digital Companion for Deeper Islam 🌙

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Tech Stack](https://img.shields.io/badge/UI_Framework-Jetpack%20Compose-00CED1?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/Data_Source-Al--Quran%20&%20Prayer%20API-FF7F50?style=for-the-badge&logo=rss)](https://github.com/public-apis/public-apis)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)

-----

## 🌟 Tentang HijrahApp

**HijrahApp** adalah aplikasi Android *all-in-one* yang didedikasikan untuk mendukung perjalanan spiritual dan ibadah harian umat Muslim. Dikembangkan dengan desain minimalis, berfokus pada **kecepatan akses dan keakuratan data**.

Kami menggunakan **Jetpack Compose** untuk menciptakan antarmuka pengguna yang bersih, responsif, dan mudah diakses, memastikan Anda mendapatkan informasi spiritual yang andal dengan cepat.

### 🎯 Prinsip Desain

  * **Fokus & Minimalis:** Antarmuka yang tidak mengganggu ibadah.
  * **Data Akurat:** Menggunakan API terpercaya untuk Jadwal Sholat dan data Al-Qur'an.
  * **Modern:** Dibangun sepenuhnya menggunakan *stack* Android modern.

-----

## 🕋 Modul Utama (The Core Modules)

Aplikasi HijrahApp dibagi menjadi empat modul fungsional utama, masing-masing memiliki sumber data API spesifik:

| Modul | Deskripsi Fungsi | API/Sumber Data |
| :---: | :--- | :--- |
| **📖 Al-Qur'an** | Menyediakan teks Al-Qur'an lengkap dan terjemahan. Mendukung fitur *bookmarking*. | API Al-Qur'an Publik (mis. *Quran API*) |
| **⏱️ Jadwal Sholat** | Menampilkan waktu sholat akurat berdasarkan lokasi GPS pengguna. | API Jadwal Sholat Publik (mis. *Kemenag API*) |
| **📿 Asmaul Husna** | Daftar 99 Nama Allah dengan terjemahan dan makna, didukung fitur *dzikir* sederhana. | Data *Embedded* / API Publik |
| **🙏 Doa - Doa** | Koleksi doa-doa harian dan sunnah. | Data *Embedded* / Database Lokal |

-----

## 📸 Tampilan Antarmuka

Antarmuka dirancang agar tenang dan memudahkan fokus pada konten spiritual.

| Tampilan Doa Doa | Tampilan Al-Qur'an | Tampilan Isi Al-Qur'an |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/cf768357-7952-41a8-a870-177500bf52cd" width="280" alt="Tampilan Doa Doa"/> | <img src="https://github.com/user-attachments/assets/987226fb-eac7-4720-9c0c-8bd3c51c570d" width="280" alt="Tampilan Al-Qur'an"/> | <img src="https://github.com/user-attachments/assets/65988a99-ac71-473c-bfab-bec023ded762" width="280" alt="Tampilan Isi Al-Qur'an"/> |

-----

## ⚙️ Detail Teknologi & Arsitektur

Kami mengimplementasikan arsitektur *Clean* dan *Modular* untuk memisahkan setiap modul fungsional:

### 🧩 Arsitektur Modul

  * Setiap fitur (misalnya, `fitur:jadwal_sholat`, `fitur:quran`) adalah modul Gradle terpisah.
  * Digunakan **MVVM** di tingkat UI.
  * Flow data: **UI ⬅️ ViewModel ⬅️ Domain ⬅️ Data (API/DB)**

### 🛠️ Stack Teknis

| Layer | Teknologi | Peran Kunci |
| :--- | :--- | :--- |
| **Presentation** | **Jetpack Compose** | UI Deklaratif, Navigasi (Compose Navigation) |
| **Domain** | **Kotlin Coroutines / Flow** | Business Logic, *State Management* Asinkron |
| **Data** | **Retrofit, Room, Hilt** | *Networking*, Caching Lokal, *Dependency Management* |
| **Testing** | **JUnit, Mockito, Truth** | Unit Testing & UI Testing (Compose) |

-----

## 🚀 Persiapan Proyek

### 📥 Prerequisites

  * **Android Studio** (Versi Terbaru)
  * **JDK 17+**

### ➡️ Cara Menjalankan

1.  **Kloning Repositori:**
    ```bash
    git clone https://github.com/your-username/HijrahApp.git
    cd HijrahApp
    ```
2.  **Sinkronisasi:** Buka di Android Studio dan tunggu sinkronisasi Gradle.
3.  **Run:** Pilih perangkat dan jalankan aplikasi.

-----

## 🤝 Kontribusi

Kami menyambut kolaborasi! Jika Anda ingin berkontribusi, silakan:
1. Buka Issue untuk bug atau usulan fitur.
2. Fork repositori dan buat **Pull Request** Anda.

-----

> **Dibuat dengan Niat Baik oleh [Nama Anda]** | *Semoga Bermanfaat*

```
```
