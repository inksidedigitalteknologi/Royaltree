# Changelog — Royaltree

Format: `V{major}.{minor}` — increment minor untuk setiap update.
Formula versionCode: `major * 100 + minor`

## V1.04
- **Critical:** Fix miner hilang bila install ulang (migrate game_miners → users.minersOwned)
- **Critical:** Fix beli miner sama 2× overwrite (instance ID unik)
- **Critical:** Fix butang "Aktifkan Miner" tak berfungsi (AdMob test ID match)
- Feature: Miner Token (tokenCost) sebagai currency beli miner
- Feature: Dual-write ke users.minersOwned + game_miners (backward compat)
- Feature: Unlimited miner support (beli miner sama banyak kali)
- Feature: Auto-place ke slot kosong pertama (buyAndPlaceGameMinerItem)
- UI: Pop-up Beli Miner frame custom (gradient emerald/gold) + tombol "Beli & Pasang"
- UI: Notifikasi i18n 6 bahasa (notif_item_placed_*) + tombol Hapus
- UI: Fix label "Poin" → "RTP" konsisten
- Room DB: v3 → v4 (GameMinerItemEntity: pricePoints → tokenCost + catalogId, UserEntity: minerTokens)
- Backend: /mining/buy guna tokenCost + instance ID + dual-write
- Backend: /state baca dari users.minersOwned + fallback
- Backend: /mining/place + /mining/unplace + /miners/catalog + /miners/unlock dual-write
- Migration: scripts/backup-miners.js + migrate-miners-to-user.js + verify-migration.js
- Fix: Int/Double mismatch pasca migration points Int → Double (23 lokasi)

**Commits:** `1a8388f` → `877b870` → `d46854e` → `249afe6` → V1.04

## V1.03
- Fix: Migration proper 2 → 3 (UserEntity.points Int → Double)
- Fix: Sidebar RTP decimal support
- Fix: Frame Balance Hero vertical (glow tak terpotong)
- Fix: Avatar Drawer frame tier dynamic (ProfileAvatarWithFrame)
- Fix: Language toggle tap target lebih besar (Surface onClick + height 44dp)
- Polish: Card transparan Login + Register (alpha 0.05 + border 1dp)
- Polish: Daun jatuh global + Compose BOM 2025.10.00
- Fix: Import fillMaxSize (AppDrawer)

**Commits:** `aae1fb8` → `a3fa538` → V1.03

## V1.02
- Fix: RTP naik walau miner tak aktif (Fasa A)
- Fix: Header "170 Poin" → "170 RTP"
- Fix: Kolom RTP guna user.points + tambah UNCLAIMED
- Fix: Duplicate "MINER TIDAK AKTIF" hilang
- Fix: Unplace miner auto-pasang semula
- Fix: IllegalFormatConversion crash (header RTP)
- Fix: Build error userPoints parameter (MiningRoomTab + HeroBanner)
- Polish: Visual consistency (Fasa C) — Snackbar/header/Profile emerald
- Polish: Daun jatuh visible (18 daun, size besar)
- Polish: Daun jatuh global (wrap MainAffiliateApp)
- Polish: Avatar frame Drawer (emerald/gold)
- Polish: RetroStatBox maxLines + format %.4f

**Commits:** `5c4fb12` → `d09f410` → V1.02

## V1.01
- i18n: tambah 19 key login_* + register_* (6 bahasa)
- Language toggle: fix Onboarding + Login + Register
- Login Screen: redesign elegan (daun jatuh emerald/gold)
- Register Screen: redesign konsisten dengan Login
- Login + Register: fix double shadow
- AnimatedBackground: daun jatuh lebih visible (18 daun, size besar)
- Profile: frame polish (card_frame_stats + AffiliateBalanceCard Card simple)
- Mining: fix kontradiksi "MINING AKTIF" + "MINER TIDAK AKTIF"
- Mining: fix "Rak Terisi" dynamic guna unlockedSlots
- Mining: auto-load miner catalog masa first launch (retry + observe user)
- Mining: fix unplace miner auto-pasang semula
- Boost cleanup: buang boost 2× dari semua layer (5 files)
- Room DB: version 1 → 2 (minerActiveUntil + minerActivatedAt)

**Commits:** `a47b616` → `b64819c` → V1.01

## V1.00 — Baseline
- Persistence: History + Notifications (Room DB sync)
- UI: Home Balance FramedCard + Login emerald + Status bar dark
- Mining: Auto-claim 60s + Boost 2× multiplier
- Widget: Auto-sync SEMUA transaksi (observe user StateFlow)
- Feature: Miner auto-aktif 6 jam (wajib tonton iklan)
- Fix: Build error (Flow imports, NotificationEntity signature)
- Fix: NPE crash (init order + defer load call)
- Fix: Room DB version 1 → 2 (schema bump)

**Commit:** `8baac1e` → `V1.00`
