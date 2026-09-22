# DEGANKU FINAL

Project canonical tunggal untuk package `com.deganku.app`.

## Build cloud
1. Buat repository GitHub.
2. Upload seluruh isi folder ini ke branch `main`.
3. Buka tab **Actions**.
4. Workflow **Build DEGANKU APK** akan berjalan.
5. Setelah selesai, buka run tersebut → **Artifacts** → `DEGANKU-debug-apk`.
6. Download ZIP artifact dan ambil APK di dalamnya.

## Catatan jujur
Project ini adalah canonical integration build pertama, bukan klaim bahwa seluruh fitur
spesifikasi besar sudah 100% production-ready. Fondasi Room, role, PIN hashing,
coconut tracking/FIFO/HPP/shift/permission, backup crypto, Compose UI, tests dan
GitHub Actions sudah disatukan. Kasir end-to-end, purchase UI, report UI lengkap,
restore flow, migration, dan instrumentation test masih perlu hardening sebelum release.

PIN demo awal: 1234. Ganti sebelum penggunaan nyata.
