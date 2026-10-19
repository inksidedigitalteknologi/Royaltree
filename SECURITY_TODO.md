# 🔐 SECURITY TODO — Sebelum Production

## 1. API Key (CRITICAL)
- [ ] Hapus `ROYALTREE_API_KEY` dari `build.gradle.kts`
- [ ] Hapus `DEFAULT_API_KEY` dari `ApiClient.kt`
- [ ] Ganti ke Firebase ID token (sudah ada `AuthInterceptor`)
- [ ] Backend: hapus `x-api-key` check (kecuali admin panel)

## 2. Admin Token (HIGH)
- [ ] Hapus `KEY_ADMIN_TOKEN` dari `AppThemePreferences.kt`
- [ ] Admin via web panel (tidak di app)
- [ ] Atau encrypt via `EncryptedSharedPreferences`

## 3. HTTPS (CRITICAL)
- [ ] Setup Cloudflare Tunnel / Let's Encrypt
- [ ] Update `DEFAULT_BASE_URL` → `https://`
- [ ] `usesCleartextTraffic="false"`

## 4. SSL Pinning (HIGH)
- [ ] Setup `CertificatePinner` (butuh HTTPS dulu)

## 5. SharedPreferences Encryption (MEDIUM)
- [ ] Encrypt `KEY_ADMIN_TOKEN` + `KEY_API_KEY`
- [ ] Pakai `EncryptedSharedPreferences`

## 6. ProGuard (LOW)
- [ ] Cek rule untuk Firebase, AdMob, Retrofit
- [ ] Test release build (minify)

---

**Status:** Dummy untuk development. Ganti production sebelum Play Store.
