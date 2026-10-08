# Changelog — Royaltree

Format: `V{major}.{minor}` — increment minor untuk setiap update.
Formula versionCode: `major * 100 + minor`

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
