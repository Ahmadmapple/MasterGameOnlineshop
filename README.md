# MasterGameOnlineshop
Repo ini dibuat untuk menyimpan projek akhir bootcamp mobile development

Anggota: Achmad Caesar Ramadhan (241401011)
Nama Aplikasi Android: Master Game Onlineshop

Deskripsi:
Merupakan Aplikasi Onlineshop yang berjualan Game, seperti Playstore dan Appstore


Fitur-fitur Aplikasi:
-Terdapat Halaman Login dan Register, untuk membuat akun dan login akun 
-Terdapat fitur User profile untuk melihat profil user, dan juga fitur logout untuk ganti akun
-Terdapat fitur Search untuk mencari game yang diinginkan
-Terdapat fitur detail game, untuk melihat komentar user-user lain mengenai game tersebut, dan juga deskripsi game 
-Terdapat fitur "My cart" untuk menyimpan game-game yang ingin dibeli, game yang sudah ditambahkan ke cart juga bisa dihapus
-Terdapat fitur "Library" untuk menyimpan game-game yang telah dibeli
-Semua data ditampilkan dengan RecycleView seperti pada komentar, dan tampilan game-game yang ada


Cara Menjalankan Aplikasi:
1. Clone Repository
2. Buka Project di Android Studio
3. Pastikan Sudah Setting Firebase
   Karena aplikasi ini memakai "Firebase Authentication" dan "Firestore Database", pengguna harus:
   - Membuat project baru di [Firebase Console](https://console.firebase.google.com/).
   - Tambahkan aplikasi Android baru di Firebase (dengan package name yang sama dari project ini).
   - Download `google-services.json` dari Firebase dan letakkan di:
     ```
     MasterGameOnlineshop/app/google-services.json
     ```
   - Aktifkan **Authentication** (Email/Password) di Firebase Console.
   - Buat **Firestore Database** baru (mode "Test" sementara).
4. Sync Gradle
   - Setelah itu klik "Sync Now" di Android Studio jika muncul notifikasi.
   - Pastikan internet aktif untuk mendownload dependency.
5. Jalankan Aplikasi
   - Pilih emulator Android atau HP fisik.
   - Klik tombol "Run ▶️" di Android Studio.
6. User Membuat akun dengan click text disamping text "Don't Have an Account"
7. User membuat akun, lalu login dengan menggunakan akun yang telah dibuat
8. User berada di "home", dengan tampilan game-game yang bisa dibeli
9. User bisa membuka sidebar, dengan klik tombol burger atau dengan swipe ke kanan
10. sidebar berisi "logout" dan "user profile"
11. user bisa membeli game dengan menekan game yang diinginkan pada bagian "home"
12. user juga bisa menggunakan "search bar" jika ingin mencari game tertentu
13. setelah memencet game yang diinginkan, user diarahkan kearah detail game dan bisa menekan tombol "add to cart" untuk menambahkan item ke cart user
14. user bisa menambahkan beberapa game, atau bisa langsung ke bagian "my cart", untuk checkout barang
15. setelah user checkout, maka game masuk ke bagian "library user"
16. jika user ingin keluar, bisa langsung keluar atau dengan logout dulu baru keluar


Sekian terima kasih

(P.S projek ini juga ikut dibantu chatgpt, dan juga jika setelah memencet tombol tidak ada apa apa - bisa coba pencet lagi atau tunggu)
