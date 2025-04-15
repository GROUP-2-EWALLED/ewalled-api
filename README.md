# eWalled Project README

Dokumen ini memberikan instruksi cara menjalankan proyek eWalled menggunakan ./mvnw dan juga berisi API documentation testing berdasarkan koleksi Postman yang tersedia.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven Wrapper (`./mvnw`) included in the project

## Running the Project

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/GROUP-2-EWALLED/ewalled-api.git
    cd ewalled-api
    ```

2.  **Build and Run the Application:**

    Gunakan Maven Wrapper untuk membangun dan menjalankan aplikasi Spring Boot.

    ```bash
    ./mvnw spring-boot:run
    ```

Perintah ini akan mengunduh semua dependensi yang dibutuhkan, mengompilasi kode, dan menjalankan aplikasi.

## API DOCUMENTATION

## Auth API `/api/auth`

### **Register User**

**POST** `/api/auth/register`

Endpoint ini digunakan untuk mendaftarkan user baru ke dalam sistem. Informasi yang dibutuhkan antara lain:

- fullname
- username
- email
- password
- phoneNumber (opsional)
- avatarUrl (opsional)

#### Request Body

```json
{
  "email": "kautsar@gmail.com",
  "fullname": "Kautsar Wicaksono",
  "password": "Password123!",
  "username": "kautsar123",
  "phoneNumber": "085959595384",
  "avatarUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Orange_tabby_cat_sitting_on_fallen_leaves-Hisashi-01A.jpg/800px-Orange_tabby_cat_sitting_on_fallen_leaves-Hisashi-01A.jpg"
}
```

#### Example (cURL)

```bash
curl --location 'https://ewalled-api-production.up.railway.app/api/auth/register' \
--data-raw '{
  "email": "kautsar@gmail.com",
  "fullname": "Kautsar Wicaksono",
  "password": "Password123!",
  "username": "kautsar123",
  "phoneNumber": "085959595384",
  "avatarUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Orange_tabby_cat_sitting_on_fallen_leaves-Hisashi-01A.jpg/800px-Orange_tabby_cat_sitting_on_fallen_leaves-Hisashi-01A.jpg"
}'
```

#### Response (201 Created)

```plain
Registration successful. Please log in.
```

---

### **Login**

**POST** `/api/auth/login`

Endpoint untuk login user yang telah terdaftar menggunakan email dan password.
Jika berhasil, response akan mengembalikan data user seperti id, email, fullname, username, dsb.

#### Request Body

```json
{
  "email": "ex400@gmail.com",
  "password": "Password123!"
}
```

#### Response (200 OK)

```json
{
  "user": {
    "id": 52,
    "email": "ex400@gmail.com",
    "username": "ex400",
    "fullname": "exempat ratus",
    "avatarUrl": "",
    "phoneNumber": "0818129837"
  },
  "wallet": {
    "id": 27,
    "userId": 52,
    "accountNumber": "005403578070",
    "balance": 1125000,
    "createdAt": "2025-04-13T04:08:24.556012",
    "updatedAt": "2025-04-14T08:46:05.760369"
  }
}
```

---

## Transactions API `/api/transactions`

Endpoint-Endpoint dalam folder ini digunakan untuk mengelola transaksi keuangan pengguna dalam aplikasi eWalled.
Fitur Utama:

- Melihat semua transaksi berdasarkan wallet ID
- Melakukan transaksi baru, seperti:
  - Top Up
  - Transfer ke pengguna lain
- Ekspor data transaksi ke PDF

### **Get All Transactions**

**GET** `/api/transactions?walletId={walletId}`

Mengambil semua transaksi milik user berdasarkan walletId.

#### Query Params

walletId = 27

#### Example (cURL)

```bash
curl --location 'https://ewalled-api-production.up.railway.app/api/transactions?walletId=27' \
--data ''
```

#### Response (200 OK)

```json
[
  {
    "id": 47,
    "walletId": 27,
    "transactionType": "TOP_UP",
    "amount": 1000000,
    "recipientWalletId": null,
    "transactionDate": "2025-04-13T04:09:06.309758",
    "description": "Initial deposit",
    "fromTo": "-"
  },
  {
    "id": 48,
    "walletId": 27,
    "transactionType": "TRANSFER",
    "amount": 500000,
    "recipientWalletId": 25,
    "transactionDate": "2025-04-13T04:10:03.97996",
    "description": "Split bill",
    "fromTo": "To: extiga ratusdua"
  }
]
```

---

### **Top Up Wallet**

**POST** `/api/transactions`

Melakukan pengisian saldo wallet user

#### Request Body

```json
{
  "walletId": 23,
  "transactionType": "TOP_UP",
  "amount": 800000,
  "description": "Cicil emas"
}
```

#### Response (201 CREATED)

```json
{
  "id": 101,
  "walletId": 23,
  "transactionType": "TOP_UP",
  "amount": 800000,
  "recipientWalletId": null,
  "transactionDate": "2025-04-14T10:09:07.747154593",
  "description": "Cicil emas",
  "fromTo": null
}
```

---

### **Transfer to Recipient Account Number**

**POST** `/api/transactions`

Digunakan untuk transfer ke akun wallet lain

#### Request Body

```json
{
  "walletId": 23,
  "transactionType": "TRANSFER",
  "amount": 5000,
  "recipientAccountNumber": "046748771955",
  "description": "Split bill"
}
```

#### Response (201 CREATED)

```json
{
  "id": 99,
  "walletId": 23,
  "transactionType": "TRANSFER",
  "amount": 5000,
  "recipientWalletId": 25,
  "transactionDate": "2025-04-14T10:06:59.737211832",
  "description": "Split bill",
  "fromTo": null
}
```

---

### **Export Transaction History to PDF**

**GET** `/api/transactions/export/pdf?walletId={walletId}`

Mengekspor seluruh riwayat transaksi user ke file PDF. File ini dapat langsung diunduh oleh user melalui UI aplikasi.

---

## Financial Overview `/api/summary/{walletId}`

Endpoint untuk melihat ringkasan keuangan berdasarkan data transaksi user dalam berbagai periode waktu.

Fitur Utama:

- Menghitung dan menampilkan Total Pemasukan (Income)Total Pengeluaran (Outcome), dan Saldo Bersih (Net Balance).
- Menyediakan data dalam format yang siap digunakan untuk visualisasi grafik pada fitur Financial Overview.

**GET** `/api/summary/{walletId}`

#### Example Response

Mengambil data ringkasan transaksi berdasarkan walletId.
Data Ringkasan yang Disediakan:
Weekly Summary (4 minggu terakhir)
Monthly Summary (4 bulan terakhir)
Quarterly Summary (4 kuartal terakhir)

```json
{
  "weekly": {
    "totalIncome": 4325996,
    "totalOutcome": 90000,
    "netBalance": 4235996,
    "data": [...]
  },
  "monthly": { ... },
  "quarterly": { ... }
}
```

---

## Wallets API `/api/wallets`

Endpoint yang berkaitan dengan wallet user dalam aplikasi E-Walled. Wallet digunakan untuk menyimpan saldo serta mengelola transaksi keuangan user.

Fitur Utama:

- Menyediakan detail informasi dompet user berdasarkan userId.
- Melakukan pengecekan apakah nomor rekening atau wallet tertentu valid saat melakukan transfer.

### **Check Wallet by Account Number**

**GET** `/api/wallets/check?accountNumber={accountNumber}`

Digunakan untuk memvalidasi apakah accountNumber tujuan transfer valid dan tersedia.

#### Query Params

accountNumber = 060523765176

#### Example (cURL)

```bash
curl --location 'https://ewalled-api-production.up.railway.app/api/wallets/check?accountNumber=060523765176'
```

#### Response

```json
{
  "accountNumber": "060523765176",
  "fullName": "extiga ratus"
}
```

---

### **Get Wallet by User ID**

**GET** `/api/wallets/user/{userId}`

Mengambil data wallet berdasarkan userId.

#### Example Response

```json
{
  "id": 25,
  "userId": 50,
  "accountNumber": "046748771955",
  "balance": 4235332,
  "createdAt": "...",
  "updatedAt": "..."
}
```

---
