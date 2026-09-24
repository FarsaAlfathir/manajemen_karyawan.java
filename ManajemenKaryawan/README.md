# 🏢 Sistem Manajemen Karyawan

Aplikasi desktop berbasis **Java Swing** dengan database **MySQL** untuk mengelola data karyawan secara digital dan efisien.

!\[Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
!\[MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge\&logo=mysql\&logoColor=white)
!\[NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge\&logo=apache-netbeans-ide\&logoColor=white)

\---

## 📋 Deskripsi

Sistem Manajemen Karyawan adalah aplikasi desktop yang dibangun menggunakan konsep **Object-Oriented Programming (OOP)** untuk mata kuliah Pemrograman Berorientasi Obyek di UIN Walisongo Semarang. Aplikasi ini memanfaatkan Java Swing untuk tampilan GUI dan MySQL sebagai database penyimpanan data.

\---

## ✨ Fitur

|Fitur|Admin|Karyawan|
|-|-|-|
|Login dengan autentikasi database|✅|✅|
|Lihat data diri sendiri|✅|✅|
|Absen masuk \& keluar|✅|✅|
|Lihat gaji \& jadwal shift|✅|✅|
|Ajukan cuti|✅|✅|
|Lihat riwayat absensi|✅|✅|
|Kelola data karyawan (CRUD)|✅|❌|
|Lihat semua data karyawan|✅|❌|

\---

## 🛠️ Teknologi yang Digunakan

* **Java** — Bahasa pemrograman utama
* **Java Swing** — Library untuk membangun GUI
* **MySQL** — Database penyimpanan data
* **JDBC** — Koneksi Java ke MySQL
* **XAMPP / Laragon** — Menjalankan MySQL secara lokal
* **Apache NetBeans** — IDE pengembangan

\---

## 🧩 Konsep OOP yang Diterapkan

#### 1\. Encapsulation

Data karyawan disimpan sebagai `private` di class `Karyawan.java` dan hanya dapat diakses melalui method getter.

#### 2\. Inheritance

```
Absensi (Parent)
├── AbsenMasuk (Child)
└── AbsenKeluar (Child)

Karyawan (Parent)
└── Gaji (Child)
```

#### 3\. Polymorphism

Method `absen()` di-override oleh class `AbsenMasuk` dan `AbsenKeluar` sehingga menghasilkan output yang berbeda meskipun bertipe sama.

\---

## 🗂️ Struktur Project

```
ManajemenKaryawan/
├── src/manajemenkaryawan/
│   ├── Karyawan.java          # Model data karyawan (OOP)
│   ├── Absensi.java           # Parent class absensi
│   ├── AbsenMasuk.java        # Child class absen masuk
│   ├── AbsenKeluar.java       # Child class absen keluar
│   ├── Gaji.java              # Child class gaji
│   ├── Cuti.java              # Class pengajuan cuti
│   ├── Shift.java             # Class jadwal shift
│   ├── Login.java             # Validasi login (versi console)
│   ├── KoneksiDB.java         # Koneksi ke database MySQL
│   ├── KaryawanDAO.java       # Semua operasi SQL (DAO Pattern)
│   ├── Main.java              # Entry point program
│   ├── LoginFrame.java        # Halaman login GUI
│   ├── MainFrameGUI.java      # Halaman utama GUI
│   └── CRUDKaryawanFrame.java # Halaman kelola data karyawan
└── dist/
    └── ManajemenKaryawan.jar  # File JAR yang siap dijalankan
```

\---

## 🗄️ Struktur Database

```sql
DATABASE: manajemen\_karyawan

TABLE karyawan
├── id          INT PRIMARY KEY
├── nama        VARCHAR(100)
├── jabatan     VARCHAR(50)
├── no\_telp     VARCHAR(20)
├── gaji        DOUBLE
├── password    VARCHAR(50)
└── role        VARCHAR(10)   -- 'admin' atau 'karyawan'

TABLE absensi
├── id          INT PRIMARY KEY
├── id\_karyawan INT (FK → karyawan.id)
├── tipe        VARCHAR(10)   -- 'masuk' atau 'keluar'
└── waktu       DATETIME

TABLE cuti
├── id          INT PRIMARY KEY
├── id\_karyawan INT (FK → karyawan.id)
├── tanggal     DATE
└── status      VARCHAR(20)   -- 'Menunggu'
```

\---

## 🚀 Cara Menjalankan

#### Prasyarat

* Java JDK 17 atau lebih baru
* XAMPP / Laragon (untuk MySQL)
* Apache NetBeans (untuk pengembangan)
* MySQL Connector/J (JDBC Driver)

#### Langkah-langkah

**1. Clone repository ini**

```bash
git clone https://github.com/username/ManajemenKaryawan.git
cd ManajemenKaryawan
```

**2. Siapkan database**

Jalankan XAMPP/Laragon, buka phpMyAdmin, lalu jalankan query berikut:

```sql
CREATE DATABASE manajemen\_karyawan;
USE manajemen\_karyawan;

CREATE TABLE karyawan (
    id       INT PRIMARY KEY,
    nama     VARCHAR(100),
    jabatan  VARCHAR(50),
    no\_telp  VARCHAR(20),
    gaji     DOUBLE,
    password VARCHAR(50),
    role     VARCHAR(10) DEFAULT 'karyawan'
);

CREATE TABLE absensi (
    id          INT PRIMARY KEY,
    id\_karyawan INT,
    tipe        VARCHAR(10),
    waktu       DATETIME,
    FOREIGN KEY (id\_karyawan) REFERENCES karyawan(id)
);

CREATE TABLE cuti (
    id          INT PRIMARY KEY,
    id\_karyawan INT,
    tanggal     DATE,
    status      VARCHAR(20),
    FOREIGN KEY (id\_karyawan) REFERENCES karyawan(id)
);

INSERT INTO karyawan VALUES
(1, 'Sony Ghanim',   'Satpam',   '081222781548', 10000000, 'sony123',  'karyawan'),
(2, 'Ahmad Izzudin', 'Owner',    '085678910112',  5000000, 'ahmad123', 'admin'),
(3, 'M Firza',       'CEO',      '081234567890', 45000000, 'firza123', 'karyawan'),
(4, 'Rifki Raditya', 'Karyawan', '081253678677', 15000000, 'rifki123', 'karyawan'),
(5, 'M Farsa',       'Kurir',    '08157282683',     20000, 'farsa123', 'karyawan');
```

**3. Sesuaikan koneksi database**

Buka `src/manajemenkaryawan/KoneksiDB.java` dan sesuaikan:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/manajemen\_karyawan";
private static final String USER     = "root";
private static final String PASSWORD = ""; // sesuaikan password MySQL kamu
```

**4. Jalankan aplikasi**

* Buka project di NetBeans
* Klik kanan project → **Run** atau tekan `F6`
* Atau jalankan file JAR: `java -jar dist/ManajemenKaryawan.jar`

\---

## 👤 Akun Default

|Nama|Password|Role|
|-|-|-|
|Ahmad Izzudin|ahmad123|Karyawan|
|Sony Ghanim|sony123|Karyawan|
|M Firza|firza123|Karyawan|
|Rifki Raditya|rifki123|Karyawan|
|M Farsa|farsa123|Admin|



## 📄 Lisensi

Project ini dibuat untuk keperluan akademik (UAS Pemrograman Berorientasi Obyek).

