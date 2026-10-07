# Changelog — Royaltree

Format: `V{major}.{minor}` — increment minor untuk setiap update.
Formula versionCode: `major * 100 + minor`

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
