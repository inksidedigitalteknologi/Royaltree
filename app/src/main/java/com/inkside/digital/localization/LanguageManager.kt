package com.inkside.digital.localization

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    INDONESIAN("ID", "Bahasa Indonesia", "🇮🇩"),
    ENGLISH("EN", "English", "🇺🇸"),
    SPANISH("ES", "Español", "🇪🇸"),
    CHINESE("ZH", "中文 (简体)", "🇨🇳"),
    ARABIC("AR", "العربية", "🇸🇦"),
    JAPANESE("JA", "日本語", "🇯🇵")
}

object LanguageManager {
    private val translations = mapOf(
        "nav_home" to mapOf(
            AppLanguage.INDONESIAN to "Beranda",
            AppLanguage.ENGLISH to "Home",
            AppLanguage.SPANISH to "Inicio",
            AppLanguage.CHINESE to "首页",
            AppLanguage.ARABIC to "الرئيسية",
            AppLanguage.JAPANESE to "ホーム"
        ),
        "nav_campaigns" to mapOf(
            AppLanguage.INDONESIAN to "Kampanye",
            AppLanguage.ENGLISH to "Campaigns",
            AppLanguage.SPANISH to "Campañas",
            AppLanguage.CHINESE to "营销活动",
            AppLanguage.ARABIC to "الحملات",
            AppLanguage.JAPANESE to "キャンペーン"
        ),
        "nav_missions" to mapOf(
            AppLanguage.INDONESIAN to "Misi RTP",
            AppLanguage.ENGLISH to "RTP Missions",
            AppLanguage.SPANISH to "Misiones",
            AppLanguage.CHINESE to "积分任务",
            AppLanguage.ARABIC to "مهام النقاط",
            AppLanguage.JAPANESE to "ポイントミッション"
        ),
        "nav_game" to mapOf(
            AppLanguage.INDONESIAN to "Ruang Game",
            AppLanguage.ENGLISH to "Game Room",
            AppLanguage.SPANISH to "Sala de Juegos",
            AppLanguage.CHINESE to "游戏空间",
            AppLanguage.ARABIC to "غرفة الألعاب",
            AppLanguage.JAPANESE to "ゲームルーム"
        ),
        "history_title" to mapOf(
            AppLanguage.INDONESIAN to "Riwayat Buku Besar & Transaksi",
            AppLanguage.ENGLISH to "Ledger History & Transactions",
            AppLanguage.SPANISH to "Historial de Transacciones",
            AppLanguage.CHINESE to "账本历史与交易",
            AppLanguage.ARABIC to "سجل المعاملات",
            AppLanguage.JAPANESE to "取引履歴"
        ),
        "history_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Audit lengkap penerimaan komisi, penarikan dana, dan investasi",
            AppLanguage.ENGLISH to "Complete audit of commissions, withdrawals, and investments",
            AppLanguage.SPANISH to "Auditoría completa de comisiones, retiros e inversiones",
            AppLanguage.CHINESE to "佣金、提现和投资的完整审计",
            AppLanguage.ARABIC to "مراجعة كاملة للعمولات والسحوبات والاستثمارات",
            AppLanguage.JAPANESE to "手数料、出金、投資の完全な監査"
        ),
        "history_filter_all" to mapOf(
            AppLanguage.INDONESIAN to "Semua",
            AppLanguage.ENGLISH to "All",
            AppLanguage.SPANISH to "Todos",
            AppLanguage.CHINESE to "全部",
            AppLanguage.ARABIC to "الكل",
            AppLanguage.JAPANESE to "すべて"
        ),
        "history_filter_commission" to mapOf(
            AppLanguage.INDONESIAN to "Komisi",
            AppLanguage.ENGLISH to "Commission",
            AppLanguage.SPANISH to "Comisión",
            AppLanguage.CHINESE to "佣金",
            AppLanguage.ARABIC to "عمولة",
            AppLanguage.JAPANESE to "手数料"
        ),
        "history_filter_withdraw" to mapOf(
            AppLanguage.INDONESIAN to "Tarik Dana",
            AppLanguage.ENGLISH to "Withdraw",
            AppLanguage.SPANISH to "Retirar",
            AppLanguage.CHINESE to "提现",
            AppLanguage.ARABIC to "سحب",
            AppLanguage.JAPANESE to "出金"
        ),
        "history_filter_coupon" to mapOf(
            AppLanguage.INDONESIAN to "Kupon",
            AppLanguage.ENGLISH to "Coupon",
            AppLanguage.SPANISH to "Cupón",
            AppLanguage.CHINESE to "优惠券",
            AppLanguage.ARABIC to "قسيمة",
            AppLanguage.JAPANESE to "クーポン"
        ),
        "history_filter_rtp" to mapOf(
            AppLanguage.INDONESIAN to "RTP",
            AppLanguage.ENGLISH to "RTP",
            AppLanguage.SPANISH to "RTP",
            AppLanguage.CHINESE to "RTP",
            AppLanguage.ARABIC to "RTP",
            AppLanguage.JAPANESE to "RTP"
        ),
        "inbox_filter_all" to mapOf(
            AppLanguage.INDONESIAN to "Semua",
            AppLanguage.ENGLISH to "All",
            AppLanguage.SPANISH to "Todos",
            AppLanguage.CHINESE to "全部",
            AppLanguage.ARABIC to "الكل",
            AppLanguage.JAPANESE to "すべて"
        ),
        "inbox_filter_unread" to mapOf(
            AppLanguage.INDONESIAN to "Belum Dibaca",
            AppLanguage.ENGLISH to "Unread",
            AppLanguage.SPANISH to "No leído",
            AppLanguage.CHINESE to "未读",
            AppLanguage.ARABIC to "غير مقروء",
            AppLanguage.JAPANESE to "未読"
        ),
        "snackbar_portal_connected" to mapOf(
            AppLanguage.INDONESIAN to "Berhasil terhubung ke portal Anda! 🟢",
            AppLanguage.ENGLISH to "Successfully connected to your portal! 🟢",
            AppLanguage.SPANISH to "¡Conectado exitosamente a tu portal! 🟢",
            AppLanguage.CHINESE to "成功连接到您的门户！🟢",
            AppLanguage.ARABIC to "تم الاتصال بالبوابة بنجاح! 🟢",
            AppLanguage.JAPANESE to "ポータルに接続しました！🟢"
        ),
        "snackbar_portal_failed" to mapOf(
            AppLanguage.INDONESIAN to "Koneksi gagal: %s 🔴",
            AppLanguage.ENGLISH to "Connection failed: %s 🔴",
            AppLanguage.SPANISH to "Conexión fallida: %s 🔴",
            AppLanguage.CHINESE to "连接失败：%s 🔴",
            AppLanguage.ARABIC to "فشل الاتصال: %s 🔴",
            AppLanguage.JAPANESE to "接続に失敗しました：%s 🔴"
        ),
        "snackbar_config_saved" to mapOf(
            AppLanguage.INDONESIAN to "Konfigurasi URL & API Key Portal berhasil disimpan!",
            AppLanguage.ENGLISH to "URL & API Key Portal configuration saved successfully!",
            AppLanguage.SPANISH to "¡Configuración de URL y API Key del Portal guardada!",
            AppLanguage.CHINESE to "门户 URL 和 API 密钥配置已保存！",
            AppLanguage.ARABIC to "تم حفظ إعدادات URL و API Key للبوابة!",
            AppLanguage.JAPANESE to "ポータルのURLとAPIキー設定を保存しました！"
        ),
        "snackbar_sync_started" to mapOf(
            AppLanguage.INDONESIAN to "Memulai sinkronisasi data ke portal...",
            AppLanguage.ENGLISH to "Starting data sync to portal...",
            AppLanguage.SPANISH to "Iniciando sincronización al portal...",
            AppLanguage.CHINESE to "正在同步数据到门户...",
            AppLanguage.ARABIC to "بدء مزامنة البيانات مع البوابة...",
            AppLanguage.JAPANESE to "ポータルへのデータ同期を開始しています..."
        ),
        "snackbar_sync_success" to mapOf(
            AppLanguage.INDONESIAN to "Sinkronisasi sukses! Data terkirim ke portal. ✓",
            AppLanguage.ENGLISH to "Sync success! Data sent to portal. ✓",
            AppLanguage.SPANISH to "¡Sincronización exitosa! Datos enviados. ✓",
            AppLanguage.CHINESE to "同步成功！数据已发送。✓",
            AppLanguage.ARABIC to "تمت المزامنة بنجاح! تم إرسال البيانات. ✓",
            AppLanguage.JAPANESE to "同期成功！データを送信しました。✓"
        ),
        "snackbar_sync_failed" to mapOf(
            AppLanguage.INDONESIAN to "Sinkronisasi gagal: %s (Data tetap tersimpan offline)",
            AppLanguage.ENGLISH to "Sync failed: %s (Data saved offline)",
            AppLanguage.SPANISH to "Sincronización fallida: %s (Datos guardados offline)",
            AppLanguage.CHINESE to "同步失败：%s（数据已离线保存）",
            AppLanguage.ARABIC to "فشلت المزامنة: %s (تم حفظ البيانات محلياً)",
            AppLanguage.JAPANESE to "同期に失敗：%s（データはオフライン保存）"
        ),
        "snackbar_language_changed" to mapOf(
            AppLanguage.INDONESIAN to "Bahasa diubah ke %s %s",
            AppLanguage.ENGLISH to "Language changed to %s %s",
            AppLanguage.SPANISH to "Idioma cambiado a %s %s",
            AppLanguage.CHINESE to "语言已切换为 %s %s",
            AppLanguage.ARABIC to "تم تغيير اللغة إلى %s %s",
            AppLanguage.JAPANESE to "言語を %s %s に変更しました"
        ),
        "snackbar_withdraw_sent" to mapOf(
            AppLanguage.INDONESIAN to "Pengajuan penarikan dana berhasil dikirim ke Admin!",
            AppLanguage.ENGLISH to "Withdrawal request sent to Admin successfully!",
            AppLanguage.SPANISH to "¡Solicitud de retiro enviada al Administrador!",
            AppLanguage.CHINESE to "提现请求已发送给管理员！",
            AppLanguage.ARABIC to "تم إرسال طلب السحب إلى المسؤول!",
            AppLanguage.JAPANESE to "出金リクエストを管理者に送信しました！"
        ),
        "snackbar_affiliate_link_ready" to mapOf(
            AppLanguage.INDONESIAN to "Tautan affiliasi siap: %s",
            AppLanguage.ENGLISH to "Affiliate link ready: %s",
            AppLanguage.SPANISH to "Enlace de afiliado listo: %s",
            AppLanguage.CHINESE to "联盟链接已就绪：%s",
            AppLanguage.ARABIC to "رابط الإحالة جاهز: %s",
            AppLanguage.JAPANESE to "アフィリエイトリンク準備完了：%s"
        ),
        "snackbar_redeem_success" to mapOf(
            AppLanguage.INDONESIAN to "Sukses tukar poin global! +Rp %s masuk ke saldo.",
            AppLanguage.ENGLISH to "Points redeemed! +Rp %s added to balance.",
            AppLanguage.SPANISH to "¡Puntos canjeados! +Rp %s al saldo.",
            AppLanguage.CHINESE to "积分兑换成功！+Rp %s 已添加到余额。",
            AppLanguage.ARABIC to "تم استبدال النقاط! +Rp %s إلى الرصيد.",
            AppLanguage.JAPANESE to "ポイント交換成功！+Rp %s を残高に追加。"
        ),
        "snackbar_coupon_bought" to mapOf(
            AppLanguage.INDONESIAN to "Kupon investasi berhasil dibeli! Cek portofolio Anda.",
            AppLanguage.ENGLISH to "Investment coupon purchased! Check your portfolio.",
            AppLanguage.SPANISH to "¡Cupón de inversión comprado! Revisa tu portafolio.",
            AppLanguage.CHINESE to "投资券购买成功！请查看您的投资组合。",
            AppLanguage.ARABIC to "تم شراء قسيمة الاستثمار! تحقق من محفظتك.",
            AppLanguage.JAPANESE to "投資クーポンを購入しました！ポートフォリオを確認してください。"
        ),
        "snackbar_dividend_paid" to mapOf(
            AppLanguage.INDONESIAN to "Hasil return dividen +Rp %s dicairkan ke saldo!",
            AppLanguage.ENGLISH to "Dividend return +Rp %s cashed out to balance!",
            AppLanguage.SPANISH to "¡Dividendo +Rp %s cobrado al saldo!",
            AppLanguage.CHINESE to "股息 +Rp %s 已兑现到余额！",
            AppLanguage.ARABIC to "تم صرف الأرباح +Rp %s إلى الرصيد!",
            AppLanguage.JAPANESE to "配当 +Rp %s を残高に換金しました！"
        ),
        "snackbar_reward_success" to mapOf(
            AppLanguage.INDONESIAN to "Reward berhasil, tapi gagal refresh: %s",
            AppLanguage.ENGLISH to "Reward successful, but refresh failed: %s",
            AppLanguage.SPANISH to "Recompensa exitosa, pero falló la actualización: %s",
            AppLanguage.CHINESE to "奖励成功，但刷新失败：%s",
            AppLanguage.ARABIC to "المكافأة ناجحة، لكن فشل التحديث: %s",
            AppLanguage.JAPANESE to "報酬は成功しましたが、更新に失敗：%s"
        ),
        "snackbar_mining_harvest" to mapOf(
            AppLanguage.INDONESIAN to "Panen Berhasil! +%s Poin masuk ke saldo Anda 🎉",
            AppLanguage.ENGLISH to "Harvest Success! +%s Points added to balance 🎉",
            AppLanguage.SPANISH to "¡Cosecha exitosa! +%s puntos al saldo 🎉",
            AppLanguage.CHINESE to "收获成功！+%s 积分已添加到余额 🎉",
            AppLanguage.ARABIC to "نجح الحصاد! +%s نقطة إلى رصيدك 🎉",
            AppLanguage.JAPANESE to "収穫成功！+%s ポイントを残高に追加 🎉"
        ),
        "snackbar_minigame_finished" to mapOf(
            AppLanguage.INDONESIAN to "Game Selesai! Skor: %s (+%s Poin & Power Boost +%s GH/s) 🎮",
            AppLanguage.ENGLISH to "Game Finished! Score: %s (+%s Points & Power Boost +%s GH/s) 🎮",
            AppLanguage.SPANISH to "¡Juego terminado! Puntuación: %s (+%s puntos & Power +%s GH/s) 🎮",
            AppLanguage.CHINESE to "游戏结束！分数：%s（+%s 积分 & 算力 +%s GH/s）🎮",
            AppLanguage.ARABIC to "انتهت اللعبة! النتيجة: %s (+%s نقطة و +%s GH/s) 🎮",
            AppLanguage.JAPANESE to "ゲーム終了！スコア：%s（+%s ポイント & +%s GH/s）🎮"
        ),
        "snackbar_mining_claim_success" to mapOf(
            AppLanguage.INDONESIAN to "🎉 Berhasil klaim %s RTP! Klaim lagi dalam 3 jam.",
            AppLanguage.ENGLISH to "🎉 Successfully claimed %s RTP! Claim again in 3 hours.",
            AppLanguage.SPANISH to "🎉 ¡Reclamaste %s RTP! Reclama en 3 horas.",
            AppLanguage.CHINESE to "🎉 成功领取 %s RTP！3 小时后可再次领取。",
            AppLanguage.ARABIC to "🎉 تم استلام %s RTP! المطالبة مرة أخرى في 3 ساعات.",
            AppLanguage.JAPANESE to "🎉 %s RTP を請求しました！3時間後に再度請求してください。"
        ),
        "snackbar_claim_cooldown" to mapOf(
            AppLanguage.INDONESIAN to "Tunggu %d menit lagi untuk klaim berikutnya.",
            AppLanguage.ENGLISH to "Wait %d more minutes for next claim.",
            AppLanguage.SPANISH to "Espera %d minutos más para el próximo reclamo.",
            AppLanguage.CHINESE to "等待 %d 分钟后再次领取。",
            AppLanguage.ARABIC to "انتظر %d دقيقة أخرى للمطالبة التالية.",
            AppLanguage.JAPANESE to "次の請求まで %d 分お待ちください。"
        ),
        "snackbar_ad_limit_reached" to mapOf(
            AppLanguage.INDONESIAN to "Limit klaim iklan harian (%d) telah dicapai. Sila tunggu cooldown biasa.",
            AppLanguage.ENGLISH to "Daily ad claim limit (%d) reached. Please wait for regular cooldown.",
            AppLanguage.SPANISH to "Límite diario de anuncios (%d) alcanzado. Espera el cooldown.",
            AppLanguage.CHINESE to "每日广告领取限制（%d）已达。请等待冷却。",
            AppLanguage.ARABIC to "تم الوصول إلى الحد اليومي (%d). انتظر فترة الانتظار.",
            AppLanguage.JAPANESE to "1日の広告請求上限（%d）に達しました。クールダウンをお待ちください。"
        ),
        "snackbar_network_error" to mapOf(
            AppLanguage.INDONESIAN to "Error: %s",
            AppLanguage.ENGLISH to "Error: %s",
            AppLanguage.SPANISH to "Error: %s",
            AppLanguage.CHINESE to "错误：%s",
            AppLanguage.ARABIC to "خطأ: %s",
            AppLanguage.JAPANESE to "エラー：%s"
        ),
        "snackbar_backend_error" to mapOf(
            AppLanguage.INDONESIAN to "Backend error: %s",
            AppLanguage.ENGLISH to "Backend error: %s",
            AppLanguage.SPANISH to "Error del backend: %s",
            AppLanguage.CHINESE to "后端错误：%s",
            AppLanguage.ARABIC to "خطأ في الخادم: %s",
            AppLanguage.JAPANESE to "バックエンドエラー：%s"
        ),
        "snackbar_generic_failed" to mapOf(
            AppLanguage.INDONESIAN to "Gagal: %s",
            AppLanguage.ENGLISH to "Failed: %s",
            AppLanguage.SPANISH to "Fallido: %s",
            AppLanguage.CHINESE to "失败：%s",
            AppLanguage.ARABIC to "فشل: %s",
            AppLanguage.JAPANESE to "失敗：%s"
        ),
        "snackbar_mining_rate_info" to mapOf(
            AppLanguage.INDONESIAN to "Rate mining diperbarui: %s RTP/mnt",
            AppLanguage.ENGLISH to "Mining rate updated: %s RTP/min",
            AppLanguage.SPANISH to "Tasa de minería actualizada: %s RTP/min",
            AppLanguage.CHINESE to "挖矿速率已更新：%s RTP/分钟",
            AppLanguage.ARABIC to "تم تحديث معدل التعدين: %s RTP/دقيقة",
            AppLanguage.JAPANESE to "マイニングレート更新：%s RTP/分"
        ),
        "snackbar_loading_data" to mapOf(
            AppLanguage.INDONESIAN to "Memuat data...",
            AppLanguage.ENGLISH to "Loading data...",
            AppLanguage.SPANISH to "Cargando datos...",
            AppLanguage.CHINESE to "加载数据中...",
            AppLanguage.ARABIC to "جارٍ تحميل البيانات...",
            AppLanguage.JAPANESE to "データを読み込んでいます..."
        ),
        "snackbar_mission_reward" to mapOf(
            AppLanguage.INDONESIAN to "Berhasil klaim hadiah misi! +%s RTP 🏆",
            AppLanguage.ENGLISH to "Mission reward claimed! +%s RTP 🏆",
            AppLanguage.SPANISH to "¡Recompensa de misión reclamada! +%s RTP 🏆",
            AppLanguage.CHINESE to "任务奖励已领取！+%s RTP 🏆",
            AppLanguage.ARABIC to "تم استلام مكافأة المهمة! +%s RTP 🏆",
            AppLanguage.JAPANESE to "ミッション報酬を受け取りました！+%s RTP 🏆"
        ),
        "snackbar_ad_reward" to mapOf(
            AppLanguage.INDONESIAN to "🎉 Selamat! +%s RTP dari iklan sponsor!",
            AppLanguage.ENGLISH to "🎉 Congratulations! +%s RTP from sponsor ad!",
            AppLanguage.SPANISH to "🎉 ¡Felicidades! +%s RTP de anuncio patrocinado!",
            AppLanguage.CHINESE to "🎉 恭喜！获得 %s RTP 赞助商广告奖励！",
            AppLanguage.ARABIC to "🎉 مبروك! +%s RTP من إعلان الراعي!",
            AppLanguage.JAPANESE to "🎉 おめでとう！スポンサー広告から %s RTP！"
        ),
        "snackbar_checkin_success" to mapOf(
            AppLanguage.INDONESIAN to "🎉 Check-in hari ke-%s! +%s RTP",
            AppLanguage.ENGLISH to "🎉 Check-in day %s! +%s RTP",
            AppLanguage.SPANISH to "🎉 ¡Check-in día %s! +%s RTP",
            AppLanguage.CHINESE to "🎉 第 %s 天签到！+%s RTP",
            AppLanguage.ARABIC to "🎉 تسجيل يوم %s! +%s RTP",
            AppLanguage.JAPANESE to "🎉 %s 日目のチェックイン！+%s RTP"
        ),
        "snackbar_transfer_success" to mapOf(
            AppLanguage.INDONESIAN to "Berhasil transfer %s RTP ke %s!",
            AppLanguage.ENGLISH to "Successfully transferred %s RTP to %s!",
            AppLanguage.SPANISH to "¡Transferencia exitosa de %s RTP a %s!",
            AppLanguage.CHINESE to "成功转账 %s RTP 给 %s！",
            AppLanguage.ARABIC to "تم تحويل %s RTP إلى %s بنجاح!",
            AppLanguage.JAPANESE to "%s RTP を %s に送金しました！"
        ),
        "badge_passive" to mapOf(
            AppLanguage.INDONESIAN to "PASIF",
            AppLanguage.ENGLISH to "PASSIVE",
            AppLanguage.SPANISH to "PASIVO",
            AppLanguage.CHINESE to "被动",
            AppLanguage.ARABIC to "سلبي",
            AppLanguage.JAPANESE to "パッシブ"
        ),
        "game_room_title" to mapOf(
            AppLanguage.INDONESIAN to "Royaltree Miner",
            AppLanguage.ENGLISH to "Royaltree Miner",
            AppLanguage.SPANISH to "Royaltree Miner",
            AppLanguage.CHINESE to "Royaltree Miner",
            AppLanguage.ARABIC to "Royaltree Miner",
            AppLanguage.JAPANESE to "Royaltree Miner"
        ),
        "game_room_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Beli item penambang pakai RTP & panen hasil pasif",
            AppLanguage.ENGLISH to "Buy miners with RTP & earn passive income",
            AppLanguage.SPANISH to "Compra mineros con RTP y gana ingresos pasivos",
            AppLanguage.CHINESE to "用 RTP 购买矿工并赚取被动收入",
            AppLanguage.ARABIC to "اشترِ المعدنين بـ RTP واكسب دخلاً سلبياً",
            AppLanguage.JAPANESE to "RTP でマイナーを購入し、受動的収入を得る"
        ),
        "nav_analytics" to mapOf(
            AppLanguage.INDONESIAN to "Analitik",
            AppLanguage.ENGLISH to "Analytics",
            AppLanguage.SPANISH to "Analíticas",
            AppLanguage.CHINESE to "数据分析",
            AppLanguage.ARABIC to "التحليلات",
            AppLanguage.JAPANESE to "分析"
        ),
        "nav_withdraw" to mapOf(
            AppLanguage.INDONESIAN to "Tarik Dana",
            AppLanguage.ENGLISH to "Withdraw",
            AppLanguage.SPANISH to "Retirar",
            AppLanguage.CHINESE to "提现",
            AppLanguage.ARABIC to "سحب الرصيد",
            AppLanguage.JAPANESE to "出金"
        ),
        "nav_invest" to mapOf(
            AppLanguage.INDONESIAN to "Kupon Invest",
            AppLanguage.ENGLISH to "Invest Coupon",
            AppLanguage.SPANISH to "Inversión",
            AppLanguage.CHINESE to "投资债券",
            AppLanguage.ARABIC to "كوبونات الاستثمار",
            AppLanguage.JAPANESE to "投資クーポン"
        ),
        "nav_history" to mapOf(
            AppLanguage.INDONESIAN to "Riwayat",
            AppLanguage.ENGLISH to "History",
            AppLanguage.SPANISH to "Historial",
            AppLanguage.CHINESE to "交易记录",
            AppLanguage.ARABIC to "السجل",
            AppLanguage.JAPANESE to "履歴"
        ),
        "nav_steps" to mapOf(
            AppLanguage.INDONESIAN to "Langkah Kaki",
            AppLanguage.ENGLISH to "Steps",
            AppLanguage.SPANISH to "Pasos",
            AppLanguage.CHINESE to "计步赚钱",
            AppLanguage.ARABIC to "خطوات",
            AppLanguage.JAPANESE to "歩数"
        ),
        "nav_ads" to mapOf(
            AppLanguage.INDONESIAN to "Unduh App",
            AppLanguage.ENGLISH to "App Offers",
            AppLanguage.SPANISH to "Ofertas",
            AppLanguage.CHINESE to "下载赚币",
            AppLanguage.ARABIC to "العروض",
            AppLanguage.JAPANESE to "アプリDL"
        ),
        "nav_profile" to mapOf(
            AppLanguage.INDONESIAN to "Profil",
            AppLanguage.ENGLISH to "Profile",
            AppLanguage.SPANISH to "Perfil",
            AppLanguage.CHINESE to "个人中心",
            AppLanguage.ARABIC to "الملف الشخصي",
            AppLanguage.JAPANESE to "プロフィール"
        ),
        "nav_admin" to mapOf(
            AppLanguage.INDONESIAN to "Admin Portal",
            AppLanguage.ENGLISH to "Admin Portal",
            AppLanguage.SPANISH to "Panel Admin",
            AppLanguage.CHINESE to "管理员后台",
            AppLanguage.ARABIC to "بوابة المشرف",
            AppLanguage.JAPANESE to "管理者画面"
        ),
        "total_commission" to mapOf(
            AppLanguage.INDONESIAN to "Saldo Komisi Tersedia",
            AppLanguage.ENGLISH to "Available Commission",
            AppLanguage.SPANISH to "Comisión Disponible",
            AppLanguage.CHINESE to "可用佣金余额",
            AppLanguage.ARABIC to "عمولة متاحة",
            AppLanguage.JAPANESE to "利用可能報酬残高"
        ),
        "pending_balance" to mapOf(
            AppLanguage.INDONESIAN to "Menunggu Review",
            AppLanguage.ENGLISH to "Pending Review",
            AppLanguage.SPANISH to "Pendiente",
            AppLanguage.CHINESE to "待审核提现",
            AppLanguage.ARABIC to "قيد المراجعة",
            AppLanguage.JAPANESE to "審査中"
        ),
        "total_paid" to mapOf(
            AppLanguage.INDONESIAN to "Total Telah Cair",
            AppLanguage.ENGLISH to "Total Paid Out",
            AppLanguage.SPANISH to "Total Pagado",
            AppLanguage.CHINESE to "累计已到账",
            AppLanguage.ARABIC to "إجمالي المدفوع",
            AppLanguage.JAPANESE to "累計受取額"
        ),
        "activity_points" to mapOf(
            AppLanguage.INDONESIAN to "Aktivitas RTP",
            AppLanguage.ENGLISH to "Activity Points",
            AppLanguage.SPANISH to "Puntos de Actividad",
            AppLanguage.CHINESE to "活跃积分",
            AppLanguage.ARABIC to "نقاط النشاط",
            AppLanguage.JAPANESE to "アクティビティポイント"
        ),
        "btn_withdraw" to mapOf(
            AppLanguage.INDONESIAN to "Tarik Saldo",
            AppLanguage.ENGLISH to "Withdraw Funds",
            AppLanguage.SPANISH to "Retirar Fondos",
            AppLanguage.CHINESE to "申请提现",
            AppLanguage.ARABIC to "سحب الأموال",
            AppLanguage.JAPANESE to "出金申請"
        ),
        "btn_new_link" to mapOf(
            AppLanguage.INDONESIAN to "Buat Link Baru",
            AppLanguage.ENGLISH to "Generate Link",
            AppLanguage.SPANISH to "Crear Enlace",
            AppLanguage.CHINESE to "生成推广链接",
            AppLanguage.ARABIC to "إنشاء رابط",
            AppLanguage.JAPANESE to "リンク生成"
        ),
        "btn_upgrade_vip" to mapOf(
            AppLanguage.INDONESIAN to "Upgrade VIP 2.5x",
            AppLanguage.ENGLISH to "Upgrade VIP 2.5x",
            AppLanguage.SPANISH to "Mejorar a VIP 2.5x",
            AppLanguage.CHINESE to "升级VIP (2.5倍佣金)",
            AppLanguage.ARABIC to "ترقية لـ VIP",
            AppLanguage.JAPANESE to "VIPにアップグレード"
        ),
        "simulate_conversion" to mapOf(
            AppLanguage.INDONESIAN to "⚡ Simulasikan Konversi Real-Time",
            AppLanguage.ENGLISH to "⚡ Simulate Real-Time Conversion",
            AppLanguage.SPANISH to "⚡ Simular Conversión en Vivo",
            AppLanguage.CHINESE to "⚡ 模拟实时成交转化",
            AppLanguage.ARABIC to "⚡ محاكاة تحويل فوري",
            AppLanguage.JAPANESE to "⚡ リアルタイム成果テスト"
        ),
        "secondary_market" to mapOf(
            AppLanguage.INDONESIAN to "Pasar Sekunder",
            AppLanguage.ENGLISH to "Secondary Market",
            AppLanguage.SPANISH to "Mercado Secundario",
            AppLanguage.CHINESE to "二级交易市场",
            AppLanguage.ARABIC to "السوق الثانوية",
            AppLanguage.JAPANESE to "流通市場 (セカンダリー)"
        ),
        "security_2fa" to mapOf(
            AppLanguage.INDONESIAN to "Keamanan 2FA & Enkripsi",
            AppLanguage.ENGLISH to "2FA & E2E Encryption",
            AppLanguage.SPANISH to "Seguridad 2FA y Cifrado",
            AppLanguage.CHINESE to "双重认证 & 端到端加密",
            AppLanguage.ARABIC to "المصادقة الثنائية والتشفير",
            AppLanguage.JAPANESE to "2要素認証 & 暗号化"
        ),
        // ==== RECOVERY ====
        "recover_title" to mapOf(
            AppLanguage.INDONESIAN to "Pulihkan Streak",
            AppLanguage.ENGLISH to "Recover Streak",
            AppLanguage.SPANISH to "Recuperar Racha",
            AppLanguage.CHINESE to "恢复连续记录",
            AppLanguage.ARABIC to "استعادة السلسلة",
            AppLanguage.JAPANESE to "連続記録を回復"
        ),
        "recover_date" to mapOf(
            AppLanguage.INDONESIAN to "Hari",
            AppLanguage.ENGLISH to "Date",
            AppLanguage.SPANISH to "Fecha",
            AppLanguage.CHINESE to "日期",
            AppLanguage.ARABIC to "التاريخ",
            AppLanguage.JAPANESE to "日付"
        ),
        "recover_desc" to mapOf(
            AppLanguage.INDONESIAN to "Kamu bolos di hari ini. Pulihkan streak dengan salah satu cara:",
            AppLanguage.ENGLISH to "You missed this day. Recover your streak using one of these:",
            AppLanguage.SPANISH to "Faltaste este día. Recupera tu racha con una de estas opciones:",
            AppLanguage.CHINESE to "您错过了这一天。通过以下方式之一恢复连续记录：",
            AppLanguage.ARABIC to "لقد فاتك هذا اليوم. استعد سلسلتك بإحدى الطرق التالية:",
            AppLanguage.JAPANESE to "この日を逃しました。次のいずれかで連続記録を回復できます："
        ),
        "recover_watch_ad" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan",
            AppLanguage.ENGLISH to "Watch Ad",
            AppLanguage.SPANISH to "Ver Anuncio",
            AppLanguage.CHINESE to "观看广告",
            AppLanguage.ARABIC to "مشاهدة إعلان",
            AppLanguage.JAPANESE to "広告を見る"
        ),
        "recover_pay_points" to mapOf(
            AppLanguage.INDONESIAN to "Bayar %d RTP",
            AppLanguage.ENGLISH to "Pay %d Points",
            AppLanguage.SPANISH to "Pagar %d Puntos",
            AppLanguage.CHINESE to "支付 %d 积分",
            AppLanguage.ARABIC to "دفع %d نقاط",
            AppLanguage.JAPANESE to "%d ポイントを支払う"
        ),

        // ==== HARI ====
        "day_sun" to mapOf(
            AppLanguage.INDONESIAN to "Min",
            AppLanguage.ENGLISH to "Sun",
            AppLanguage.SPANISH to "Dom",
            AppLanguage.CHINESE to "日",
            AppLanguage.ARABIC to "أحد",
            AppLanguage.JAPANESE to "日"
        ),
        "day_mon" to mapOf(
            AppLanguage.INDONESIAN to "Sen",
            AppLanguage.ENGLISH to "Mon",
            AppLanguage.SPANISH to "Lun",
            AppLanguage.CHINESE to "一",
            AppLanguage.ARABIC to "إثن",
            AppLanguage.JAPANESE to "月"
        ),
        "day_tue" to mapOf(
            AppLanguage.INDONESIAN to "Sel",
            AppLanguage.ENGLISH to "Tue",
            AppLanguage.SPANISH to "Mar",
            AppLanguage.CHINESE to "二",
            AppLanguage.ARABIC to "ثلا",
            AppLanguage.JAPANESE to "火"
        ),
        "day_wed" to mapOf(
            AppLanguage.INDONESIAN to "Rab",
            AppLanguage.ENGLISH to "Wed",
            AppLanguage.SPANISH to "Mié",
            AppLanguage.CHINESE to "三",
            AppLanguage.ARABIC to "أرب",
            AppLanguage.JAPANESE to "水"
        ),
        "day_thu" to mapOf(
            AppLanguage.INDONESIAN to "Kam",
            AppLanguage.ENGLISH to "Thu",
            AppLanguage.SPANISH to "Jue",
            AppLanguage.CHINESE to "四",
            AppLanguage.ARABIC to "خمي",
            AppLanguage.JAPANESE to "木"
        ),
        "day_fri" to mapOf(
            AppLanguage.INDONESIAN to "Jum",
            AppLanguage.ENGLISH to "Fri",
            AppLanguage.SPANISH to "Vie",
            AppLanguage.CHINESE to "五",
            AppLanguage.ARABIC to "جمع",
            AppLanguage.JAPANESE to "金"
        ),
        "day_sat" to mapOf(
            AppLanguage.INDONESIAN to "Sab",
            AppLanguage.ENGLISH to "Sat",
            AppLanguage.SPANISH to "Sáb",
            AppLanguage.CHINESE to "六",
            AppLanguage.ARABIC to "سبت",
            AppLanguage.JAPANESE to "土"
        ),

        // ==== BUTTON ====
        "btn_watch_and_checkin" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan & Check-In",
            AppLanguage.ENGLISH to "Watch Ad & Check-In",
            AppLanguage.SPANISH to "Ver Anuncio y Registrarse",
            AppLanguage.CHINESE to "观看广告并签到",
            AppLanguage.ARABIC to "شاهد الإعلان وسجل الدخول",
            AppLanguage.JAPANESE to "広告を見てチェックイン"
        ),

        // ==== ONBOARDING ====
        "onboarding_welcome_title" to mapOf(
            AppLanguage.INDONESIAN to "Selamat Datang",
            AppLanguage.ENGLISH to "Welcome",
            AppLanguage.SPANISH to "Bienvenido",
            AppLanguage.CHINESE to "欢迎",
            AppLanguage.ARABIC to "مرحبا",
            AppLanguage.JAPANESE to "ようこそ"
        ),
        "onboarding_welcome_desc" to mapOf(
            AppLanguage.INDONESIAN to "Royaltree adalah platform afiliasi rewarding yang memberi kamu RTP dari aktivitas sehari-hari.",
            AppLanguage.ENGLISH to "Royaltree is a rewarding affiliate platform that gives you points from daily activities.",
            AppLanguage.SPANISH to "Royaltree es una plataforma de afiliados que te da puntos por actividades diarias.",
            AppLanguage.CHINESE to "Royaltree 是一个奖励联盟平台，通过日常活动为您赚取积分。",
            AppLanguage.ARABIC to "رويالتري هي منصة تسويق بالعمولة تمنحك نقاطًا من الأنشطة اليومية.",
            AppLanguage.JAPANESE to "Royaltreeは日常活動でポイントが貯まるアフィリエイトプラットフォームです。"
        ),
        "onboarding_points_title" to mapOf(
            AppLanguage.INDONESIAN to "Banyak Cara Dapat RTP",
            AppLanguage.ENGLISH to "Many Ways to Earn Points",
            AppLanguage.SPANISH to "Muchas Formas de Ganar Puntos",
            AppLanguage.CHINESE to "多种赚取积分的方式",
            AppLanguage.ARABIC to "طرق عديدة لكسب النقاط",
            AppLanguage.JAPANESE to "ポイントを稼ぐ多くの方法"
        ),
        "onboarding_points_desc" to mapOf(
            AppLanguage.INDONESIAN to "Langkah kaki, misi harian, game, dan offerwall — semua bisa jadi RTP.",
            AppLanguage.ENGLISH to "Steps, daily missions, games, and offerwall — everything becomes points.",
            AppLanguage.SPANISH to "Pasos, misiones diarias, juegos y offerwall: todo se convierte en puntos.",
            AppLanguage.CHINESE to "步数、每日任务、游戏和 offerwall——一切都能变成积分。",
            AppLanguage.ARABIC to "الخطوات والمهام اليومية والألعاب والعروض — كل شيء يصبح نقاطًا.",
            AppLanguage.JAPANESE to "歩数、デイリーミッション、ゲーム、オファーウォール——すべてがポイントに。"
        ),
        "onboarding_withdraw_title" to mapOf(
            AppLanguage.INDONESIAN to "Tarik ke Rekening",
            AppLanguage.ENGLISH to "Withdraw to Bank",
            AppLanguage.SPANISH to "Retirar al Banco",
            AppLanguage.CHINESE to "提现到银行",
            AppLanguage.ARABIC to "اسحب إلى البنك",
            AppLanguage.JAPANESE to "銀行へ出金"
        ),
        "onboarding_withdraw_desc" to mapOf(
            AppLanguage.INDONESIAN to "Tukar RTP jadi Balance, lalu tarik ke e-wallet atau bank favoritmu.",
            AppLanguage.ENGLISH to "Convert points to balance, then withdraw to your favorite e-wallet or bank.",
            AppLanguage.SPANISH to "Convierte puntos en saldo y retíralos a tu billetera o banco favorito.",
            AppLanguage.CHINESE to "将积分转换为余额，然后提现到您最喜欢的电子钱包或银行。",
            AppLanguage.ARABIC to "حوّل النقاط إلى رصيد، ثم اسحب إلى محفظتك الإلكترونية أو بنكك المفضل.",
            AppLanguage.JAPANESE to "ポイントを残高に変換し、お気に入りの電子ウォレットや銀行へ出金できます。"
        ),
        "onboarding_start_title" to mapOf(
            AppLanguage.INDONESIAN to "Mulai Sekarang",
            AppLanguage.ENGLISH to "Start Now",
            AppLanguage.SPANISH to "Empezar Ahora",
            AppLanguage.CHINESE to "立即开始",
            AppLanguage.ARABIC to "ابدأ الآن",
            AppLanguage.JAPANESE to "今すぐ始める"
        ),
        "onboarding_start_desc" to mapOf(
            AppLanguage.INDONESIAN to "Daftar gratis, selesaikan misi, dan raih penghasilan tambahan!",
            AppLanguage.ENGLISH to "Sign up free, complete missions, and earn extra income!",
            AppLanguage.SPANISH to "¡Regístrate gratis, completa misiones y gana ingresos extra!",
            AppLanguage.CHINESE to "免费注册，完成任务，赚取额外收入！",
            AppLanguage.ARABIC to "سجّل مجانًا وأكمل المهام واكسب دخلًا إضافيًا!",
            AppLanguage.JAPANESE to "無料登録してミッションを完了し、追加収入を稼ぎましょう！"
        ),
        "onboarding_skip" to mapOf(
            AppLanguage.INDONESIAN to "Lewati",
            AppLanguage.ENGLISH to "Skip",
            AppLanguage.SPANISH to "Saltar",
            AppLanguage.CHINESE to "跳过",
            AppLanguage.ARABIC to "تخطي",
            AppLanguage.JAPANESE to "スキップ"
        ),
        "onboarding_next" to mapOf(
            AppLanguage.INDONESIAN to "Lanjut",
            AppLanguage.ENGLISH to "Next",
            AppLanguage.SPANISH to "Siguiente",
            AppLanguage.CHINESE to "下一步",
            AppLanguage.ARABIC to "التالي",
            AppLanguage.JAPANESE to "次へ"
        ),
        "onboarding_start" to mapOf(
            AppLanguage.INDONESIAN to "Mulai",
            AppLanguage.ENGLISH to "Start",
            AppLanguage.SPANISH to "Empezar",
            AppLanguage.CHINESE to "开始",
            AppLanguage.ARABIC to "ابدأ",
            AppLanguage.JAPANESE to "開始"
        ),

        // ============ LOGIN SCREEN KEYS ============
        "login_welcome" to mapOf(
            AppLanguage.INDONESIAN to "Selamat Datang",
            AppLanguage.ENGLISH to "Welcome Back",
            AppLanguage.SPANISH to "Bienvenido de Nuevo",
            AppLanguage.CHINESE to "欢迎回来",
            AppLanguage.ARABIC to "مرحبا بعودتك",
            AppLanguage.JAPANESE to "おかえりなさい"
        ),
        "login_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Masuk untuk melanjutkan",
            AppLanguage.ENGLISH to "Sign in to continue",
            AppLanguage.SPANISH to "Inicia sesión para continuar",
            AppLanguage.CHINESE to "登录以继续",
            AppLanguage.ARABIC to "سجّل الدخول للمتابعة",
            AppLanguage.JAPANESE to "続行するにはサインイン"
        ),
        "login_email" to mapOf(
            AppLanguage.INDONESIAN to "Email",
            AppLanguage.ENGLISH to "Email",
            AppLanguage.SPANISH to "Correo",
            AppLanguage.CHINESE to "邮箱",
            AppLanguage.ARABIC to "البريد الإلكتروني",
            AppLanguage.JAPANESE to "メール"
        ),
        "login_password" to mapOf(
            AppLanguage.INDONESIAN to "Password",
            AppLanguage.ENGLISH to "Password",
            AppLanguage.SPANISH to "Contraseña",
            AppLanguage.CHINESE to "密码",
            AppLanguage.ARABIC to "كلمة المرور",
            AppLanguage.JAPANESE to "パスワード"
        ),
        "login_forgot" to mapOf(
            AppLanguage.INDONESIAN to "Lupa Password?",
            AppLanguage.ENGLISH to "Forgot Password?",
            AppLanguage.SPANISH to "¿Olvidaste tu contraseña?",
            AppLanguage.CHINESE to "忘记密码？",
            AppLanguage.ARABIC to "نسيت كلمة المرور؟",
            AppLanguage.JAPANESE to "パスワードをお忘れですか？"
        ),
        "login_button" to mapOf(
            AppLanguage.INDONESIAN to "Masuk",
            AppLanguage.ENGLISH to "Sign In",
            AppLanguage.SPANISH to "Iniciar Sesión",
            AppLanguage.CHINESE to "登录",
            AppLanguage.ARABIC to "تسجيل الدخول",
            AppLanguage.JAPANESE to "サインイン"
        ),
        "login_google" to mapOf(
            AppLanguage.INDONESIAN to "Masuk dengan Google",
            AppLanguage.ENGLISH to "Sign in with Google",
            AppLanguage.SPANISH to "Iniciar sesión con Google",
            AppLanguage.CHINESE to "使用 Google 登录",
            AppLanguage.ARABIC to "تسجيل الدخول بـ Google",
            AppLanguage.JAPANESE to "Google でサインイン"
        ),
        "login_no_account" to mapOf(
            AppLanguage.INDONESIAN to "Belum punya akun? ",
            AppLanguage.ENGLISH to "Don't have an account? ",
            AppLanguage.SPANISH to "¿No tienes cuenta? ",
            AppLanguage.CHINESE to "还没有账户？",
            AppLanguage.ARABIC to "ليس لديك حساب؟ ",
            AppLanguage.JAPANESE to "アカウントをお持ちでないですか？"
        ),
        "login_register" to mapOf(
            AppLanguage.INDONESIAN to "Daftar",
            AppLanguage.ENGLISH to "Sign Up",
            AppLanguage.SPANISH to "Registrarse",
            AppLanguage.CHINESE to "注册",
            AppLanguage.ARABIC to "إنشاء حساب",
            AppLanguage.JAPANESE to "新規登録"
        ),

        // ============ REGISTER SCREEN KEYS ============
        "register_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Daftar Akun Baru",
            AppLanguage.ENGLISH to "Create New Account",
            AppLanguage.SPANISH to "Crear Nueva Cuenta",
            AppLanguage.CHINESE to "创建新账户",
            AppLanguage.ARABIC to "إنشاء حساب جديد",
            AppLanguage.JAPANESE to "新しいアカウントを作成"
        ),
        "register_create" to mapOf(
            AppLanguage.INDONESIAN to "Buat Akun",
            AppLanguage.ENGLISH to "Create Account",
            AppLanguage.SPANISH to "Crear Cuenta",
            AppLanguage.CHINESE to "创建账户",
            AppLanguage.ARABIC to "إنشاء حساب",
            AppLanguage.JAPANESE to "アカウント作成"
        ),
        "register_tagline" to mapOf(
            AppLanguage.INDONESIAN to "Gratis, mulai hasilkan komisi",
            AppLanguage.ENGLISH to "Free, start earning commissions",
            AppLanguage.SPANISH to "Gratis, comienza a ganar comisiones",
            AppLanguage.CHINESE to "免费，开始赚取佣金",
            AppLanguage.ARABIC to "مجانًا، ابدأ في كسب العمولات",
            AppLanguage.JAPANESE to "無料でコミッションを稼ごう"
        ),
        "register_name" to mapOf(
            AppLanguage.INDONESIAN to "Nama Lengkap",
            AppLanguage.ENGLISH to "Full Name",
            AppLanguage.SPANISH to "Nombre Completo",
            AppLanguage.CHINESE to "全名",
            AppLanguage.ARABIC to "الاسم الكامل",
            AppLanguage.JAPANESE to "フルネーム"
        ),
        "register_email" to mapOf(
            AppLanguage.INDONESIAN to "Email",
            AppLanguage.ENGLISH to "Email",
            AppLanguage.SPANISH to "Correo",
            AppLanguage.CHINESE to "邮箱",
            AppLanguage.ARABIC to "البريد الإلكتروني",
            AppLanguage.JAPANESE to "メール"
        ),
        "register_password" to mapOf(
            AppLanguage.INDONESIAN to "Password",
            AppLanguage.ENGLISH to "Password",
            AppLanguage.SPANISH to "Contraseña",
            AppLanguage.CHINESE to "密码",
            AppLanguage.ARABIC to "كلمة المرور",
            AppLanguage.JAPANESE to "パスワード"
        ),
        "register_button" to mapOf(
            AppLanguage.INDONESIAN to "Daftar",
            AppLanguage.ENGLISH to "Sign Up",
            AppLanguage.SPANISH to "Registrarse",
            AppLanguage.CHINESE to "注册",
            AppLanguage.ARABIC to "إنشاء حساب",
            AppLanguage.JAPANESE to "新規登録"
        ),
        "register_google" to mapOf(
            AppLanguage.INDONESIAN to "Daftar dengan Google",
            AppLanguage.ENGLISH to "Sign up with Google",
            AppLanguage.SPANISH to "Registrarse con Google",
            AppLanguage.CHINESE to "使用 Google 注册",
            AppLanguage.ARABIC to "التسجيل بـ Google",
            AppLanguage.JAPANESE to "Google で登録"
        ),
        "register_have_account" to mapOf(
            AppLanguage.INDONESIAN to "Sudah punya akun? ",
            AppLanguage.ENGLISH to "Already have an account? ",
            AppLanguage.SPANISH to "¿Ya tienes cuenta? ",
            AppLanguage.CHINESE to "已经有账户？",
            AppLanguage.ARABIC to "لديك حساب بالفعل؟ ",
            AppLanguage.JAPANESE to "すでにアカウントをお持ちですか？"
        ),
        "register_login" to mapOf(
            AppLanguage.INDONESIAN to "Masuk",
            AppLanguage.ENGLISH to "Sign In",
            AppLanguage.SPANISH to "Iniciar Sesión",
            AppLanguage.CHINESE to "登录",
            AppLanguage.ARABIC to "تسجيل الدخول",
            AppLanguage.JAPANESE to "サインイン"
        ),

        "sponsor_ad_title" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan",
            AppLanguage.ENGLISH to "Watch Ad",
            AppLanguage.SPANISH to "Ver Anuncio",
            AppLanguage.CHINESE to "观看广告",
            AppLanguage.ARABIC to "مشاهدة إعلان",
            AppLanguage.JAPANESE to "広告を視聴"
        ),
        "global_points_standard" to mapOf(
            AppLanguage.INDONESIAN to "🌍 Standar Nilai RTP Global: 100 RTP = $1.00 USD / USDT",
            AppLanguage.ENGLISH to "🌍 Universal Global RTP Value: 100 RTP = $1.00 USD / USDT",
            AppLanguage.SPANISH to "🌍 Valor Global Universal RTP: 100 RTP = $1.00 USD / USDT",
            AppLanguage.CHINESE to "🌍 全球统一 RTP 价值：100 RTP = $1.00 美元/泰达币",
            AppLanguage.ARABIC to "🌍 القيمة العالمية الموحدة: 100 RTP = 1.00 دولار / USDT",
            AppLanguage.JAPANESE to "🌍 世界共通 RTP レート: 100 RTP = $1.00 USD / USDT"
        ),
        "global_points_desc" to mapOf(
            AppLanguage.INDONESIAN to "Nilai tukar Royaltree Point (RTP) distandarisasi di seluruh dunia. 100 RTP bernilai $1.00 USD. Pengguna di negara manapun mendapatkan nilai daya beli yang setara tanpa diskriminasi wilayah.",
            AppLanguage.ENGLISH to "Royaltree Points (RTP) hold identical universal value worldwide (100 RTP = $1.00 USD). Global users receive standardized purchasing power and instant cross-border payouts.",
            AppLanguage.SPANISH to "Los Royaltree Points (RTP) tienen el mismo valor universal en todo el mundo con pagos transfronterizos estandarizados.",
            AppLanguage.CHINESE to "全球所有国家和地区用户享受相同的 RTP 积分购买力（100 RTP = 1.00 美元）与即时兑现标准。",
            AppLanguage.ARABIC to "تحمل نقاط رويالتري (RTP) نفس القيمة المتساوية في جميع أنحاء العالم لجميع المستخدمين.",
            AppLanguage.JAPANESE to "Royaltree Point (RTP) は世界中どこから利用しても価値は均一（100 RTP = $1.00 USD）。グローバル標準レートで即時換金可能です。"
        ),

        // ============ HOME ============
        "home_welcome" to mapOf(
            AppLanguage.INDONESIAN to "Selamat Datang",
            AppLanguage.ENGLISH to "Welcome",
            AppLanguage.SPANISH to "Bienvenido",
            AppLanguage.CHINESE to "欢迎",
            AppLanguage.ARABIC to "مرحبا",
            AppLanguage.JAPANESE to "ようこそ"
        ),
        "home_balance" to mapOf(
            AppLanguage.INDONESIAN to "Saldo",
            AppLanguage.ENGLISH to "Balance",
            AppLanguage.SPANISH to "Saldo",
            AppLanguage.CHINESE to "余额",
            AppLanguage.ARABIC to "الرصيد",
            AppLanguage.JAPANESE to "残高"
        ),
        "home_points" to mapOf(
            AppLanguage.INDONESIAN to "RTP",
            AppLanguage.ENGLISH to "RTP",
            AppLanguage.SPANISH to "Puntos",
            AppLanguage.CHINESE to "积分",
            AppLanguage.ARABIC to "النقاط",
            AppLanguage.JAPANESE to "ポイント"
        ),
        "home_steps_today" to mapOf(
            AppLanguage.INDONESIAN to "Langkah Hari Ini",
            AppLanguage.ENGLISH to "Steps Today",
            AppLanguage.SPANISH to "Pasos de Hoy",
            AppLanguage.CHINESE to "今日步数",
            AppLanguage.ARABIC to "خطوات اليوم",
            AppLanguage.JAPANESE to "今日の歩数"
        ),

        // ============ ANALYTICS ============
        "analytics_title" to mapOf(
            AppLanguage.INDONESIAN to "Analitik",
            AppLanguage.ENGLISH to "Analytics",
            AppLanguage.SPANISH to "Analíticas",
            AppLanguage.CHINESE to "数据分析",
            AppLanguage.ARABIC to "التحليلات",
            AppLanguage.JAPANESE to "分析"
        ),
        "analytics_growth_chart" to mapOf(
            AppLanguage.INDONESIAN to "Grafik Pertumbuhan Komisi",
            AppLanguage.ENGLISH to "Commission Growth Chart",
            AppLanguage.SPANISH to "Gráfico de Crecimiento",
            AppLanguage.CHINESE to "佣金增长图表",
            AppLanguage.ARABIC to "مخطط نمو العمولة",
            AppLanguage.JAPANESE to "コミッション成長グラフ"
        ),
        "analytics_traffic_source" to mapOf(
            AppLanguage.INDONESIAN to "Sumber Lalu Lintas",
            AppLanguage.ENGLISH to "Traffic Source",
            AppLanguage.SPANISH to "Fuente de Tráfico",
            AppLanguage.CHINESE to "流量来源",
            AppLanguage.ARABIC to "مصدر الزيارات",
            AppLanguage.JAPANESE to "トラフィックソース"
        ),
        "analytics_geographic" to mapOf(
            AppLanguage.INDONESIAN to "Distribusi Geografis",
            AppLanguage.ENGLISH to "Geographic Distribution",
            AppLanguage.SPANISH to "Distribución Geográfica",
            AppLanguage.CHINESE to "地理分布",
            AppLanguage.ARABIC to "التوزيع الجغرافي",
            AppLanguage.JAPANESE to "地理的分布"
        ),

        // ============ SETTINGS ============
        "settings_title" to mapOf(
            AppLanguage.INDONESIAN to "Pengaturan",
            AppLanguage.ENGLISH to "Settings",
            AppLanguage.SPANISH to "Ajustes",
            AppLanguage.CHINESE to "设置",
            AppLanguage.ARABIC to "الإعدادات",
            AppLanguage.JAPANESE to "設定"
        ),
        "settings_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Sesuaikan aplikasi sesuai preferensi kamu",
            AppLanguage.ENGLISH to "Customize the app to your preference",
            AppLanguage.SPANISH to "Personaliza la aplicación",
            AppLanguage.CHINESE to "根据您的偏好自定义应用",
            AppLanguage.ARABIC to "تخصيص التطبيق حسب تفضيلاتك",
            AppLanguage.JAPANESE to "好みに合わせてアプリをカスタマイズ"
        ),
        "settings_language" to mapOf(
            AppLanguage.INDONESIAN to "Bahasa",
            AppLanguage.ENGLISH to "Language",
            AppLanguage.SPANISH to "Idioma",
            AppLanguage.CHINESE to "语言",
            AppLanguage.ARABIC to "اللغة",
            AppLanguage.JAPANESE to "言語"
        ),
        "settings_notifications" to mapOf(
            AppLanguage.INDONESIAN to "Notifikasi",
            AppLanguage.ENGLISH to "Notifications",
            AppLanguage.SPANISH to "Notificaciones",
            AppLanguage.CHINESE to "通知",
            AppLanguage.ARABIC to "الإشعارات",
            AppLanguage.JAPANESE to "通知"
        ),
        "settings_dark_mode" to mapOf(
            AppLanguage.INDONESIAN to "Mode Gelap",
            AppLanguage.ENGLISH to "Dark Mode",
            AppLanguage.SPANISH to "Modo Oscuro",
            AppLanguage.CHINESE to "深色模式",
            AppLanguage.ARABIC to "الوضع الداكن",
            AppLanguage.JAPANESE to "ダークモード"
        ),
        "settings_location" to mapOf(
            AppLanguage.INDONESIAN to "Pelacakan Lokasi",
            AppLanguage.ENGLISH to "Location Tracking",
            AppLanguage.SPANISH to "Rastreo de Ubicación",
            AppLanguage.CHINESE to "位置追踪",
            AppLanguage.ARABIC to "تتبع الموقع",
            AppLanguage.JAPANESE to "位置追跡"
        ),
        "settings_security" to mapOf(
            AppLanguage.INDONESIAN to "Keamanan Akun",
            AppLanguage.ENGLISH to "Account Security",
            AppLanguage.SPANISH to "Seguridad de Cuenta",
            AppLanguage.CHINESE to "账户安全",
            AppLanguage.ARABIC to "أمان الحساب",
            AppLanguage.JAPANESE to "アカウントセキュリティ"
        ),
        "settings_privacy" to mapOf(
            AppLanguage.INDONESIAN to "Kebijakan Privasi",
            AppLanguage.ENGLISH to "Privacy Policy",
            AppLanguage.SPANISH to "Política de Privacidad",
            AppLanguage.CHINESE to "隐私政策",
            AppLanguage.ARABIC to "سياسة الخصوصية",
            AppLanguage.JAPANESE to "プライバシーポリシー"
        ),
        "settings_about" to mapOf(
            AppLanguage.INDONESIAN to "Tentang Royaltree",
            AppLanguage.ENGLISH to "About Royaltree",
            AppLanguage.SPANISH to "Acerca de Royaltree",
            AppLanguage.CHINESE to "关于 Royaltree",
            AppLanguage.ARABIC to "حول Royaltree",
            AppLanguage.JAPANESE to "Royaltreeについて"
        ),
        "settings_logout" to mapOf(
            AppLanguage.INDONESIAN to "Keluar",
            AppLanguage.ENGLISH to "Logout",
            AppLanguage.SPANISH to "Cerrar Sesión",
            AppLanguage.CHINESE to "退出登录",
            AppLanguage.ARABIC to "تسجيل الخروج",
            AppLanguage.JAPANESE to "ログアウト"
        ),
        "settings_logout_desc" to mapOf(
            AppLanguage.INDONESIAN to "Keluar dari akun Royaltree",
            AppLanguage.ENGLISH to "Sign out from Royaltree account",
            AppLanguage.SPANISH to "Cerrar sesión de Royaltree",
            AppLanguage.CHINESE to "从 Royaltree 账户退出",
            AppLanguage.ARABIC to "تسجيل الخروج من حساب Royaltree",
            AppLanguage.JAPANESE to "Royaltreeアカウントからログアウト"
        ),
        "settings_logout_confirm" to mapOf(
            AppLanguage.INDONESIAN to "Anda yakin ingin keluar dari Royaltree?",
            AppLanguage.ENGLISH to "Are you sure you want to logout?",
            AppLanguage.SPANISH to "¿Estás seguro de que quieres cerrar sesión?",
            AppLanguage.CHINESE to "您确定要退出吗？",
            AppLanguage.ARABIC to "هل أنت متأكد أنك تريد تسجيل الخروج؟",
            AppLanguage.JAPANESE to "ログアウトしてもよろしいですか？"
        ),

        // ============ COMMON ============
        "common_loading" to mapOf(
            AppLanguage.INDONESIAN to "Memuat...",
            AppLanguage.ENGLISH to "Loading...",
            AppLanguage.SPANISH to "Cargando...",
            AppLanguage.CHINESE to "加载中...",
            AppLanguage.ARABIC to "جار التحميل...",
            AppLanguage.JAPANESE to "読み込み中..."
        ),
        "common_empty" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada data",
            AppLanguage.ENGLISH to "No data yet",
            AppLanguage.SPANISH to "Sin datos aún",
            AppLanguage.CHINESE to "暂无数据",
            AppLanguage.ARABIC to "لا توجد بيانات بعد",
            AppLanguage.JAPANESE to "データがありません"
        ),
        "common_empty_desc" to mapOf(
            AppLanguage.INDONESIAN to "Data akan muncul di sini",
            AppLanguage.ENGLISH to "Data will appear here",
            AppLanguage.SPANISH to "Los datos aparecerán aquí",
            AppLanguage.CHINESE to "数据将显示在这里",
            AppLanguage.ARABIC to "ستظهر البيانات هنا",
            AppLanguage.JAPANESE to "データがここに表示されます"
        ),
        "common_error" to mapOf(
            AppLanguage.INDONESIAN to "Terjadi kesalahan",
            AppLanguage.ENGLISH to "An error occurred",
            AppLanguage.SPANISH to "Ocurrió un error",
            AppLanguage.CHINESE to "发生错误",
            AppLanguage.ARABIC to "حدث خطأ",
            AppLanguage.JAPANESE to "エラーが発生しました"
        ),
        "common_retry" to mapOf(
            AppLanguage.INDONESIAN to "Coba Lagi",
            AppLanguage.ENGLISH to "Try Again",
            AppLanguage.SPANISH to "Intentar de Nuevo",
            AppLanguage.CHINESE to "重试",
            AppLanguage.ARABIC to "حاول مرة أخرى",
            AppLanguage.JAPANESE to "再試行"
        ),
        "common_cancel" to mapOf(
            AppLanguage.INDONESIAN to "Batal",
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.SPANISH to "Cancelar",
            AppLanguage.CHINESE to "取消",
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.JAPANESE to "キャンセル"
        ),
        "common_yes" to mapOf(
            AppLanguage.INDONESIAN to "Ya",
            AppLanguage.ENGLISH to "Yes",
            AppLanguage.SPANISH to "Sí",
            AppLanguage.CHINESE to "是",
            AppLanguage.ARABIC to "نعم",
            AppLanguage.JAPANESE to "はい"
        ),
        "common_no" to mapOf(
            AppLanguage.INDONESIAN to "Tidak",
            AppLanguage.ENGLISH to "No",
            AppLanguage.SPANISH to "No",
            AppLanguage.CHINESE to "否",
            AppLanguage.ARABIC to "لا",
            AppLanguage.JAPANESE to "いいえ"
        ),
        "common_save" to mapOf(
            AppLanguage.INDONESIAN to "Simpan",
            AppLanguage.ENGLISH to "Save",
            AppLanguage.SPANISH to "Guardar",
            AppLanguage.CHINESE to "保存",
            AppLanguage.ARABIC to "حفظ",
            AppLanguage.JAPANESE to "保存"
        ),
        "common_close" to mapOf(
            AppLanguage.INDONESIAN to "Tutup",
            AppLanguage.ENGLISH to "Close",
            AppLanguage.SPANISH to "Cerrar",
            AppLanguage.CHINESE to "关闭",
            AppLanguage.ARABIC to "إغلاق",
            AppLanguage.JAPANESE to "閉じる"
        ),

        // ============ DAILY CHECK-IN ============
        "daily_title" to mapOf(
            AppLanguage.INDONESIAN to "Login Harian",
            AppLanguage.ENGLISH to "Daily Check-In",
            AppLanguage.SPANISH to "Registro Diario",
            AppLanguage.CHINESE to "每日签到",
            AppLanguage.ARABIC to "تسجيل الدخول اليومي",
            AppLanguage.JAPANESE to "毎日のチェックイン"
        ),
        "daily_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Klaim hadiah setiap hari",
            AppLanguage.ENGLISH to "Claim daily rewards",
            AppLanguage.SPANISH to "Reclama recompensas diarias",
            AppLanguage.CHINESE to "领取每日奖励",
            AppLanguage.ARABIC to "احصل على المكافآت اليومية",
            AppLanguage.JAPANESE to "毎日の報酬を受け取る"
        ),
        "daily_streak" to mapOf(
            AppLanguage.INDONESIAN to "Streak Kamu",
            AppLanguage.ENGLISH to "Your Streak",
            AppLanguage.SPANISH to "Tu Racha",
            AppLanguage.CHINESE to "您的连续记录",
            AppLanguage.ARABIC to "سلسلتك",
            AppLanguage.JAPANESE to "あなたの連続記録"
        ),
        "daily_claim" to mapOf(
            AppLanguage.INDONESIAN to "KLAIM HADIAH HARI INI",
            AppLanguage.ENGLISH to "CLAIM TODAY\'S REWARD",
            AppLanguage.SPANISH to "RECLAMAR RECOMPENSA DE HOY",
            AppLanguage.CHINESE to "领取今日奖励",
            AppLanguage.ARABIC to "احصل على مكافأة اليوم",
            AppLanguage.JAPANESE to "今日の報酬を受け取る"
        ),
        "daily_claimed" to mapOf(
            AppLanguage.INDONESIAN to "Kamu sudah check-in hari ini. Kembali besok!",
            AppLanguage.ENGLISH to "You\'ve checked in today. Come back tomorrow!",
            AppLanguage.SPANISH to "Ya te registraste hoy. ¡Vuelve mañana!",
            AppLanguage.CHINESE to "您今天已签到。明天再来！",
            AppLanguage.ARABIC to "لقد سجلت دخولك اليوم. عد غدًا!",
            AppLanguage.JAPANESE to "今日はチェックイン済みです。また明日！"
        ),
        "daily_weekend_info" to mapOf(
            AppLanguage.INDONESIAN to "Weekend (Sabtu/Minggu) = hadiah lebih besar!",
            AppLanguage.ENGLISH to "Weekend (Sat/Sun) = bigger rewards!",
            AppLanguage.SPANISH to "Fin de semana = ¡mayores recompensas!",
            AppLanguage.CHINESE to "周末 = 更大的奖励！",
            AppLanguage.ARABIC to "عطلة نهاية الأسبوع = مكافآت أكبر!",
            AppLanguage.JAPANESE to "週末 = より大きな報酬！"
        ),

        // ============ INBOX ============
        "inbox_title" to mapOf(
            AppLanguage.INDONESIAN to "Inbox",
            AppLanguage.ENGLISH to "Inbox",
            AppLanguage.SPANISH to "Bandeja de Entrada",
            AppLanguage.CHINESE to "收件箱",
            AppLanguage.ARABIC to "صندوق الوارد",
            AppLanguage.JAPANESE to "受信トレイ"
        ),
        "inbox_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Pesan dari admin & tim support",
            AppLanguage.ENGLISH to "Messages from admin & support",
            AppLanguage.SPANISH to "Mensajes del administrador",
            AppLanguage.CHINESE to "来自管理员的消息",
            AppLanguage.ARABIC to "رسائل من الإدارة",
            AppLanguage.JAPANESE to "管理者からのメッセージ"
        ),
        "inbox_empty" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada pesan",
            AppLanguage.ENGLISH to "No messages yet",
            AppLanguage.SPANISH to "Sin mensajes aún",
            AppLanguage.CHINESE to "暂无消息",
            AppLanguage.ARABIC to "لا توجد رسائل بعد",
            AppLanguage.JAPANESE to "メッセージはありません"
        ),

        // ============ REFERRAL ============
        "referral_title" to mapOf(
            AppLanguage.INDONESIAN to "Undang Teman",
            AppLanguage.ENGLISH to "Invite Friends",
            AppLanguage.SPANISH to "Invitar Amigos",
            AppLanguage.CHINESE to "邀请朋友",
            AppLanguage.ARABIC to "دعوة الأصدقاء",
            AppLanguage.JAPANESE to "友達を招待"
        ),
        "referral_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Dapatkan komisi pasif 5% selamanya",
            AppLanguage.ENGLISH to "Get 5% passive commission forever",
            AppLanguage.SPANISH to "Obtén 5% de comisión pasiva",
            AppLanguage.CHINESE to "永久获得5%被动佣金",
            AppLanguage.ARABIC to "احصل على 5% عمولة سلبية للأبد",
            AppLanguage.JAPANESE to "永久に5%のパッシブコミッション"
        ),
        "referral_your_code" to mapOf(
            AppLanguage.INDONESIAN to "Kode Referral Anda",
            AppLanguage.ENGLISH to "Your Referral Code",
            AppLanguage.SPANISH to "Tu Código de Referido",
            AppLanguage.CHINESE to "您的推荐码",
            AppLanguage.ARABIC to "رمز الإحالة الخاص بك",
            AppLanguage.JAPANESE to "あなたの紹介コード"
        ),
        "referral_share" to mapOf(
            AppLanguage.INDONESIAN to "Bagikan ke Teman",
            AppLanguage.ENGLISH to "Share with Friends",
            AppLanguage.SPANISH to "Compartir con Amigos",
            AppLanguage.CHINESE to "分享给朋友",
            AppLanguage.ARABIC to "شارك مع الأصدقاء",
            AppLanguage.JAPANESE to "友達と共有"
        ),
        "referral_copied" to mapOf(
            AppLanguage.INDONESIAN to "Kode disalin!",
            AppLanguage.ENGLISH to "Code copied!",
            AppLanguage.SPANISH to "¡Código copiado!",
            AppLanguage.CHINESE to "代码已复制！",
            AppLanguage.ARABIC to "تم نسخ الرمز!",
            AppLanguage.JAPANESE to "コードをコピーしました！"
        ),

        // ============ FAQ ============
        "faq_title" to mapOf(
            AppLanguage.INDONESIAN to "Bantuan",
            AppLanguage.ENGLISH to "Help",
            AppLanguage.SPANISH to "Ayuda",
            AppLanguage.CHINESE to "帮助",
            AppLanguage.ARABIC to "مساعدة",
            AppLanguage.JAPANESE to "ヘルプ"
        ),
        "faq_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Pertanyaan umum & dukungan",
            AppLanguage.ENGLISH to "Common questions & support",
            AppLanguage.SPANISH to "Preguntas frecuentes",
            AppLanguage.CHINESE to "常见问题与支持",
            AppLanguage.ARABIC to "الأسئلة الشائعة والدعم",
            AppLanguage.JAPANESE to "よくある質問とサポート"
        ),
        "faq_contact_support" to mapOf(
            AppLanguage.INDONESIAN to "Hubungi Support",
            AppLanguage.ENGLISH to "Contact Support",
            AppLanguage.SPANISH to "Contactar Soporte",
            AppLanguage.CHINESE to "联系支持",
            AppLanguage.ARABIC to "اتصل بالدعم",
            AppLanguage.JAPANESE to "サポートに連絡"
        ),

        "notif_title" to mapOf(
            AppLanguage.INDONESIAN to "Notifikasi",
            AppLanguage.ENGLISH to "Notifications",
            AppLanguage.SPANISH to "Notificaciones",
            AppLanguage.CHINESE to "通知",
            AppLanguage.ARABIC to "الإشعارات",
            AppLanguage.JAPANESE to "通知"
        ),
        "notif_today" to mapOf(
            AppLanguage.INDONESIAN to "Hari Ini",
            AppLanguage.ENGLISH to "Today",
            AppLanguage.SPANISH to "Hoy",
            AppLanguage.CHINESE to "今天",
            AppLanguage.ARABIC to "اليوم",
            AppLanguage.JAPANESE to "今日"
        ),
        "notif_yesterday" to mapOf(
            AppLanguage.INDONESIAN to "Kemarin",
            AppLanguage.ENGLISH to "Yesterday",
            AppLanguage.SPANISH to "Ayer",
            AppLanguage.CHINESE to "昨天",
            AppLanguage.ARABIC to "أمس",
            AppLanguage.JAPANESE to "昨日"
        ),
        "notif_older" to mapOf(
            AppLanguage.INDONESIAN to "Lebih Lama",
            AppLanguage.ENGLISH to "Earlier",
            AppLanguage.SPANISH to "Anterior",
            AppLanguage.CHINESE to "更早",
            AppLanguage.ARABIC to "أقدم",
            AppLanguage.JAPANESE to "以前"
        ),
        "notif_all_read" to mapOf(
            AppLanguage.INDONESIAN to "Semua sudah dibaca",
            AppLanguage.ENGLISH to "All read",
            AppLanguage.SPANISH to "Todo leído",
            AppLanguage.CHINESE to "全部已读",
            AppLanguage.ARABIC to "تمت قراءة الكل",
            AppLanguage.JAPANESE to "すべて既読"
        ),
        "notif_unread_count" to mapOf(
            AppLanguage.INDONESIAN to "%d belum dibaca",
            AppLanguage.ENGLISH to "%d unread",
            AppLanguage.SPANISH to "%d sin leer",
            AppLanguage.CHINESE to "%d 未读",
            AppLanguage.ARABIC to "%d غير مقروء",
            AppLanguage.JAPANESE to "%d 未読"
        ),
        "notif_item_placed_title" to mapOf(
            AppLanguage.INDONESIAN to "Item Baru Terpasang! 🖥️",
            AppLanguage.ENGLISH to "New Item Placed! 🖥️",
            AppLanguage.SPANISH to "¡Nuevo artículo instalado! 🖥️",
            AppLanguage.CHINESE to "新物品已安装！🖥️",
            AppLanguage.ARABIC to "تم تثبيت العنصر الجديد! 🖥️",
            AppLanguage.JAPANESE to "新しいアイテムを設置しました！🖥️"
        ),
        "notif_item_placed_active" to mapOf(
            AppLanguage.INDONESIAN to "Item '%s' berhasil dibeli dan langsung aktif di Slot %d!",
            AppLanguage.ENGLISH to "Item '%s' purchased and instantly active in Slot %d!",
            AppLanguage.SPANISH to "¡Artículo '%s' comprado y activo en la Ranura %d!",
            AppLanguage.CHINESE to "物品 '%s' 已购买并在槽位 %d 中激活！",
            AppLanguage.ARABIC to "تم شراء العنصر '%s' وتنشيطه في الفتحة %d!",
            AppLanguage.JAPANESE to "アイテム '%s' を購入し、スロット %d で有効化！"
        ),
        "notif_item_placed_inventory" to mapOf(
            AppLanguage.INDONESIAN to "Item '%s' berhasil dibeli dan tersimpan di inventaris rak.",
            AppLanguage.ENGLISH to "Item '%s' purchased and stored in rack inventory.",
            AppLanguage.SPANISH to "Artículo '%s' comprado y guardado en el inventario.",
            AppLanguage.CHINESE to "物品 '%s' 已购买并存储在货架库存中。",
            AppLanguage.ARABIC to "تم شراء العنصر '%s' وتخزينه في مخزون الرف.",
            AppLanguage.JAPANESE to "アイテム '%s' を購入し、棚のインベントリに保存しました。"
        ),
        "notif_clear_all" to mapOf(
            AppLanguage.INDONESIAN to "Hapus",
            AppLanguage.ENGLISH to "Clear",
            AppLanguage.SPANISH to "Borrar",
            AppLanguage.CHINESE to "清除",
            AppLanguage.ARABIC to "حذف",
            AppLanguage.JAPANESE to "削除"
        ),
        "notif_mark_all" to mapOf(
            AppLanguage.INDONESIAN to "Tandai",
            AppLanguage.ENGLISH to "Mark",
            AppLanguage.SPANISH to "Marcar",
            AppLanguage.CHINESE to "标记",
            AppLanguage.ARABIC to "تحديد",
            AppLanguage.JAPANESE to "マーク"
        ),
        "notif_empty_title" to mapOf(
            AppLanguage.INDONESIAN to "Belum Ada Notifikasi",
            AppLanguage.ENGLISH to "No Notifications Yet",
            AppLanguage.SPANISH to "Sin notificaciones",
            AppLanguage.CHINESE to "暂无通知",
            AppLanguage.ARABIC to "لا توجد إشعارات",
            AppLanguage.JAPANESE to "通知はありません"
        ),
        "notif_empty_desc" to mapOf(
            AppLanguage.INDONESIAN to "Update komisi, check-in, dan reward akan muncul di sini",
            AppLanguage.ENGLISH to "Commission, check-in, and reward updates will appear here",
            AppLanguage.SPANISH to "Las actualizaciones aparecerán aquí",
            AppLanguage.CHINESE to "佣金、签到和奖励更新将显示在这里",
            AppLanguage.ARABIC to "ستظهر تحديثات العمولة والحضور والمكافآت هنا",
            AppLanguage.JAPANESE to "報酬、チェックイン、報酬の更新がここに表示されます"
        ),
        "time_just_now" to mapOf(
            AppLanguage.INDONESIAN to "Baru saja",
            AppLanguage.ENGLISH to "Just now",
            AppLanguage.SPANISH to "Ahora mismo",
            AppLanguage.CHINESE to "刚刚",
            AppLanguage.ARABIC to "الآن",
            AppLanguage.JAPANESE to "たった今"
        ),
        "time_min_ago" to mapOf(
            AppLanguage.INDONESIAN to "%d menit lalu",
            AppLanguage.ENGLISH to "%d min ago",
            AppLanguage.SPANISH to "Hace %d min",
            AppLanguage.CHINESE to "%d 分钟前",
            AppLanguage.ARABIC to "قبل %d دقيقة",
            AppLanguage.JAPANESE to "%d 分前"
        ),
        "time_hour_ago" to mapOf(
            AppLanguage.INDONESIAN to "%d jam lalu",
            AppLanguage.ENGLISH to "%d h ago",
            AppLanguage.SPANISH to "Hace %d h",
            AppLanguage.CHINESE to "%d 小时前",
            AppLanguage.ARABIC to "قبل %d ساعة",
            AppLanguage.JAPANESE to "%d 時間前"
        ),
        "time_day_ago" to mapOf(
            AppLanguage.INDONESIAN to "%d hari lalu",
            AppLanguage.ENGLISH to "%d d ago",
            AppLanguage.SPANISH to "Hace %d d",
            AppLanguage.CHINESE to "%d 天前",
            AppLanguage.ARABIC to "قبل %d يوم",
            AppLanguage.JAPANESE to "%d 日前"
        ),
        "time_week_ago" to mapOf(
            AppLanguage.INDONESIAN to "%d minggu lalu",
            AppLanguage.ENGLISH to "%d w ago",
            AppLanguage.SPANISH to "Hace %d sem",
            AppLanguage.CHINESE to "%d 周前",
            AppLanguage.ARABIC to "قبل %d أسبوع",
            AppLanguage.JAPANESE to "%d 週間前"
        ),
        "time_month_ago" to mapOf(
            AppLanguage.INDONESIAN to "%d bulan lalu",
            AppLanguage.ENGLISH to "%d mo ago",
            AppLanguage.SPANISH to "Hace %d mes",
            AppLanguage.CHINESE to "%d 个月前",
            AppLanguage.ARABIC to "قبل %d شهر",
            AppLanguage.JAPANESE to "%d ヶ月前"
        ),
        "profile_tier_you" to mapOf(
            AppLanguage.INDONESIAN to "Tingkat Anda: %s",
            AppLanguage.ENGLISH to "Your Tier: %s",
            AppLanguage.SPANISH to "Tu nivel: %s",
            AppLanguage.CHINESE to "您的等级：%s",
            AppLanguage.ARABIC to "مستواك: %s",
            AppLanguage.JAPANESE to "あなたのティア: %s"
        ),
        "mission_streak" to mapOf(
            AppLanguage.INDONESIAN to "🔥 Streak: ",
            AppLanguage.ENGLISH to "🔥 Streak: ",
            AppLanguage.SPANISH to "🔥 Racha: ",
            AppLanguage.CHINESE to "🔥 连续: ",
            AppLanguage.ARABIC to "🔥 السلسلة: ",
            AppLanguage.JAPANESE to "🔥 連続: "
        ),
        "mission_social_post" to mapOf(
            AppLanguage.INDONESIAN to "📱 Postingan %s",
            AppLanguage.ENGLISH to "📱 Post on %s",
            AppLanguage.SPANISH to "📱 Publicación en %s",
            AppLanguage.CHINESE to "📱 发布到 %s",
            AppLanguage.ARABIC to "📱 نشر على %s",
            AppLanguage.JAPANESE to "📱 %s に投稿"
        ),
        "mission_official_channel" to mapOf(
            AppLanguage.INDONESIAN to "📢 Saluran Resmi %s",
            AppLanguage.ENGLISH to "📢 Official Channel %s",
            AppLanguage.SPANISH to "📢 Canal oficial %s",
            AppLanguage.CHINESE to "📢 官方频道 %s",
            AppLanguage.ARABIC to "📢 القناة الرسمية %s",
            AppLanguage.JAPANESE to "📢 公式チャンネル %s"
        ),
        "mission_watch_remaining" to mapOf(
            AppLanguage.INDONESIAN to "Sisa waktu tonton: %ds",
            AppLanguage.ENGLISH to "Watch time left: %ds",
            AppLanguage.SPANISH to "Tiempo restante: %ds",
            AppLanguage.CHINESE to "剩余观看时间: %ds",
            AppLanguage.ARABIC to "الوقت المتبقي: %ds",
            AppLanguage.JAPANESE to "残り視聴時間: %ds"
        ),
        "mission_watch_done" to mapOf(
            AppLanguage.INDONESIAN to "Selesai Ditonton! ✓",
            AppLanguage.ENGLISH to "Watch Complete! ✓",
            AppLanguage.SPANISH to "¡Visto! ✓",
            AppLanguage.CHINESE to "观看完成！✓",
            AppLanguage.ARABIC to "تم المشاهدة! ✓",
            AppLanguage.JAPANESE to "視聴完了! ✓"
        ),
        "mission_game_promo" to mapOf(
            AppLanguage.INDONESIAN to "Buka Royaltree Miner untuk menaruh item dan menghasilkan RTP pasif 24/7!",
            AppLanguage.ENGLISH to "Open Royaltree Miner to place items and generate passive RTP 24/7!",
            AppLanguage.SPANISH to "¡Abre Royaltree Miner para colocar objetos y generar RTP pasivo 24/7!",
            AppLanguage.CHINESE to "打开 Royaltree Miner 放置物品，24/7 产生被动 RTP！",
            AppLanguage.ARABIC to "افتح Royaltree Miner لوضع العناصر وتوليد RTP سلبي 24/7!",
            AppLanguage.JAPANESE to "Royaltree Minerを開いてアイテムを配置し、24時間受動的にRTPを生成しましょう！"
        ),
        "mission_confirm_like" to mapOf(
            AppLanguage.INDONESIAN to "Konfirmasi Suka (+%d RTP)",
            AppLanguage.ENGLISH to "Confirm Like (+%d RTP)",
            AppLanguage.SPANISH to "Confirmar Me gusta (+%d RTP)",
            AppLanguage.CHINESE to "确认点赞 (+%d RTP)",
            AppLanguage.ARABIC to "تأكيد الإعجاب (+%d RTP)",
            AppLanguage.JAPANESE to "いいね確認 (+%d RTP)"
        ),
        "mission_verify_subscribe" to mapOf(
            AppLanguage.INDONESIAN to "Verifikasi Subscribe (+%d RTP)",
            AppLanguage.ENGLISH to "Verify Subscribe (+%d RTP)",
            AppLanguage.SPANISH to "Verificar Suscripción (+%d RTP)",
            AppLanguage.CHINESE to "验证订阅 (+%d RTP)",
            AppLanguage.ARABIC to "التحقق من الاشتراك (+%d RTP)",
            AppLanguage.JAPANESE to "登録確認 (+%d RTP)"
        ),
        "mission_open_game_room" to mapOf(
            AppLanguage.INDONESIAN to "Buka Ruang Game",
            AppLanguage.ENGLISH to "Open Game Room",
            AppLanguage.SPANISH to "Abrir Game Room",
            AppLanguage.CHINESE to "打开游戏室",
            AppLanguage.ARABIC to "افتح غرفة الألعاب",
            AppLanguage.JAPANESE to "ゲームルームを開く"
        ),
        "mission_complete" to mapOf(
            AppLanguage.INDONESIAN to "Selesaikan (+%d RTP)",
            AppLanguage.ENGLISH to "Complete (+%d RTP)",
            AppLanguage.SPANISH to "Completar (+%d RTP)",
            AppLanguage.CHINESE to "完成 (+%d RTP)",
            AppLanguage.ARABIC to "إكمال (+%d RTP)",
            AppLanguage.JAPANESE to "完了 (+%d RTP)"
        ),
        "common_close" to mapOf(
            AppLanguage.INDONESIAN to "Tutup",
            AppLanguage.ENGLISH to "Close",
            AppLanguage.SPANISH to "Cerrar",
            AppLanguage.CHINESE to "关闭",
            AppLanguage.ARABIC to "إغلاق",
            AppLanguage.JAPANESE to "閉じる"
        ),
        "home_watch_ad" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan",
            AppLanguage.ENGLISH to "Watch Ad",
            AppLanguage.SPANISH to "Ver Anuncio",
            AppLanguage.CHINESE to "观看广告",
            AppLanguage.ARABIC to "شاهد إعلان",
            AppLanguage.JAPANESE to "広告を見る"
        ),
        "home_watch_ad_desc" to mapOf(
            AppLanguage.INDONESIAN to "Tonton untuk dapatkan hadiah",
            AppLanguage.ENGLISH to "Watch to get reward",
            AppLanguage.SPANISH to "Mira para obtener recompensa",
            AppLanguage.CHINESE to "观看以获得奖励",
            AppLanguage.ARABIC to "شاهد للحصول على مكافأة",
            AppLanguage.JAPANESE to "視聴して報酬を獲得"
        ),
        "home_loading_ad" to mapOf(
            AppLanguage.INDONESIAN to "Memuat iklan...",
            AppLanguage.ENGLISH to "Loading ad...",
            AppLanguage.SPANISH to "Cargando anuncio...",
            AppLanguage.CHINESE to "加载广告中...",
            AppLanguage.ARABIC to "جارٍ تحميل الإعلان...",
            AppLanguage.JAPANESE to "広告を読み込み中..."
        ),
        "home_please_wait" to mapOf(
            AppLanguage.INDONESIAN to "Mohon tunggu sebentar",
            AppLanguage.ENGLISH to "Please wait a moment",
            AppLanguage.SPANISH to "Por favor espera un momento",
            AppLanguage.CHINESE to "请稍等",
            AppLanguage.ARABIC to "الرجاء الانتظار لحظة",
            AppLanguage.JAPANESE to "少々お待ちください"
        ),
        "home_ad_not_available" to mapOf(
            AppLanguage.INDONESIAN to "Iklan tidak tersedia",
            AppLanguage.ENGLISH to "Ad not available",
            AppLanguage.SPANISH to "Anuncio no disponible",
            AppLanguage.CHINESE to "广告不可用",
            AppLanguage.ARABIC to "الإعلان غير متاح",
            AppLanguage.JAPANESE to "広告は利用できません"
        ),
        "home_try_later" to mapOf(
            AppLanguage.INDONESIAN to "Coba lagi nanti",
            AppLanguage.ENGLISH to "Try again later",
            AppLanguage.SPANISH to "Inténtalo más tarde",
            AppLanguage.CHINESE to "稍后再试",
            AppLanguage.ARABIC to "حاول مرة أخرى لاحقًا",
            AppLanguage.JAPANESE to "後でもう一度お試しください"
        ),
        "home_limit_reached" to mapOf(
            AppLanguage.INDONESIAN to "Limit harian tercapai",
            AppLanguage.ENGLISH to "Daily limit reached",
            AppLanguage.SPANISH to "Límite diario alcanzado",
            AppLanguage.CHINESE to "已达到每日上限",
            AppLanguage.ARABIC to "تم الوصول إلى الحد اليومي",
            AppLanguage.JAPANESE to "1日の上限に達しました"
        ),
        "home_come_back_tomorrow" to mapOf(
            AppLanguage.INDONESIAN to "Kembali besok untuk nonton lagi",
            AppLanguage.ENGLISH to "Come back tomorrow to watch again",
            AppLanguage.SPANISH to "Vuelve mañana para ver más",
            AppLanguage.CHINESE to "明天再来观看",
            AppLanguage.ARABIC to "عد غدًا للمشاهدة مرة أخرى",
            AppLanguage.JAPANESE to "明日また見に来てください"
        ),
        "home_wait_cooldown" to mapOf(
            AppLanguage.INDONESIAN to "Iklan sedang dimuat",
            AppLanguage.ENGLISH to "Ad is loading",
            AppLanguage.SPANISH to "El anuncio se está cargando",
            AppLanguage.CHINESE to "广告正在加载",
            AppLanguage.ARABIC to "جارٍ تحميل الإعلان",
            AppLanguage.JAPANESE to "広告を読み込み中"
        ),
        "home_wait_moment" to mapOf(
            AppLanguage.INDONESIAN to "Tunggu sebentar...",
            AppLanguage.ENGLISH to "Wait a moment...",
            AppLanguage.SPANISH to "Espera un momento...",
            AppLanguage.CHINESE to "请稍候...",
            AppLanguage.ARABIC to "انتظر لحظة...",
            AppLanguage.JAPANESE to "少々お待ちを..."
        ),
        "home_checking" to mapOf(
            AppLanguage.INDONESIAN to "Memeriksa...",
            AppLanguage.ENGLISH to "Checking...",
            AppLanguage.SPANISH to "Verificando...",
            AppLanguage.CHINESE to "检查中...",
            AppLanguage.ARABIC to "جارٍ التحقق...",
            AppLanguage.JAPANESE to "確認中..."
        ),
        "home_checking_status" to mapOf(
            AppLanguage.INDONESIAN to "Sedang memuat status",
            AppLanguage.ENGLISH to "Loading status",
            AppLanguage.SPANISH to "Cargando estado",
            AppLanguage.CHINESE to "加载状态中",
            AppLanguage.ARABIC to "جارٍ تحميل الحالة",
            AppLanguage.JAPANESE to "ステータスを読み込み中"
        ),
        "common_try_again" to mapOf(
            AppLanguage.INDONESIAN to "Coba Lagi",
            AppLanguage.ENGLISH to "Try Again",
            AppLanguage.SPANISH to "Reintentar",
            AppLanguage.CHINESE to "重试",
            AppLanguage.ARABIC to "حاول مرة أخرى",
            AppLanguage.JAPANESE to "再試行"
        ),
        "game_hub_mining" to mapOf(
            AppLanguage.INDONESIAN to "Mining Tycoon",
            AppLanguage.ENGLISH to "Mining Tycoon",
            AppLanguage.SPANISH to "Mining Tycoon",
            AppLanguage.CHINESE to "挖矿大亨",
            AppLanguage.ARABIC to "Mining Tycoon",
            AppLanguage.JAPANESE to "マイニングタイクーン"
        ),
        "game_hub_coinrush" to mapOf(
            AppLanguage.INDONESIAN to "Coin Rush",
            AppLanguage.ENGLISH to "Coin Rush",
            AppLanguage.SPANISH to "Coin Rush",
            AppLanguage.CHINESE to "金币冲刺",
            AppLanguage.ARABIC to "Coin Rush",
            AppLanguage.JAPANESE to "コインラッシュ"
        ),
        "game_hub_dice" to mapOf(
            AppLanguage.INDONESIAN to "Dice Roll",
            AppLanguage.ENGLISH to "Dice Roll",
            AppLanguage.SPANISH to "Dados",
            AppLanguage.CHINESE to "掷骰子",
            AppLanguage.ARABIC to "رمي النرد",
            AppLanguage.JAPANESE to "サイコロ"
        ),
        "game_hub_spin" to mapOf(
            AppLanguage.INDONESIAN to "Sweepstakes",
            AppLanguage.ENGLISH to "Sweepstakes",
            AppLanguage.SPANISH to "Sorteos",
            AppLanguage.CHINESE to "抽奖",
            AppLanguage.ARABIC to "السحوبات",
            AppLanguage.JAPANESE to "スイープステークス"
        ),
        "game_hub_shop" to mapOf(
            AppLanguage.INDONESIAN to "Shop",
            AppLanguage.ENGLISH to "Shop",
            AppLanguage.SPANISH to "Tienda",
            AppLanguage.CHINESE to "商店",
            AppLanguage.ARABIC to "المتجر",
            AppLanguage.JAPANESE to "ショップ"
        ),
        "game_hub_events" to mapOf(
            AppLanguage.INDONESIAN to "Events",
            AppLanguage.ENGLISH to "Events",
            AppLanguage.SPANISH to "Eventos",
            AppLanguage.CHINESE to "活动",
            AppLanguage.ARABIC to "الأحداث",
            AppLanguage.JAPANESE to "イベント"
        ),
        "game_hub_stats" to mapOf(
            AppLanguage.INDONESIAN to "Stats",
            AppLanguage.ENGLISH to "Stats",
            AppLanguage.SPANISH to "Estadísticas",
            AppLanguage.CHINESE to "统计",
            AppLanguage.ARABIC to "الإحصائيات",
            AppLanguage.JAPANESE to "統計"
        ),
        "game_hub_settings" to mapOf(
            AppLanguage.INDONESIAN to "Set",
            AppLanguage.ENGLISH to "Set",
            AppLanguage.SPANISH to "Ajustes",
            AppLanguage.CHINESE to "设置",
            AppLanguage.ARABIC to "الإعدادات",
            AppLanguage.JAPANESE to "設定"
        ),
        "game_hub_help" to mapOf(
            AppLanguage.INDONESIAN to "Help",
            AppLanguage.ENGLISH to "Help",
            AppLanguage.SPANISH to "Ayuda",
            AppLanguage.CHINESE to "帮助",
            AppLanguage.ARABIC to "مساعدة",
            AppLanguage.JAPANESE to "ヘルプ"
        ),
        "game_hub_view_all" to mapOf(
            AppLanguage.INDONESIAN to "Lihat →",
            AppLanguage.ENGLISH to "View →",
            AppLanguage.SPANISH to "Ver →",
            AppLanguage.CHINESE to "查看 →",
            AppLanguage.ARABIC to "عرض →",
            AppLanguage.JAPANESE to "表示 →"
        ),
        "game_hub_rank" to mapOf(
            AppLanguage.INDONESIAN to "Peringkat #--",
            AppLanguage.ENGLISH to "Rank #--",
            AppLanguage.SPANISH to "Rango #--",
            AppLanguage.CHINESE to "排名 #--",
            AppLanguage.ARABIC to "الترتيب #--",
            AppLanguage.JAPANESE to "ランク #--"
        ),
        "game_price" to mapOf(
            AppLanguage.INDONESIAN to "Harga:",
            AppLanguage.ENGLISH to "Price:",
            AppLanguage.SPANISH to "Precio:",
            AppLanguage.CHINESE to "价格：",
            AppLanguage.ARABIC to "السعر:",
            AppLanguage.JAPANESE to "価格:"
        ),
        "game_mining_tycoon" to mapOf(
            AppLanguage.INDONESIAN to "MINING TYCOON",
            AppLanguage.ENGLISH to "MINING TYCOON",
            AppLanguage.SPANISH to "MINING TYCOON",
            AppLanguage.CHINESE to "MINING TYCOON",
            AppLanguage.ARABIC to "MINING TYCOON",
            AppLanguage.JAPANESE to "MINING TYCOON"
        ),
        "game_points_miner" to mapOf(
            AppLanguage.INDONESIAN to "Royaltree Points Miner",
            AppLanguage.ENGLISH to "Royaltree Points Miner",
            AppLanguage.SPANISH to "Minero de Puntos Royaltree",
            AppLanguage.CHINESE to "Royaltree积分矿工",
            AppLanguage.ARABIC to "عامل تعدين نقاط رويالتري",
            AppLanguage.JAPANESE to "ロイヤルツリーポイントマイナー"
        ),
        "game_rack_filled" to mapOf(
            AppLanguage.INDONESIAN to "%s Rak Terisi",
            AppLanguage.ENGLISH to "%s Rack Filled",
            AppLanguage.SPANISH to "%s Estante Lleno",
            AppLanguage.CHINESE to "%s 槽位已满",
            AppLanguage.ARABIC to "%s رف ممتلئ",
            AppLanguage.JAPANESE to "%s ラック満杯"
        ),
        "game_miner_active" to mapOf(
            AppLanguage.INDONESIAN to "MINER SEDANG AKTIF",
            AppLanguage.ENGLISH to "MINER IS ACTIVE",
            AppLanguage.SPANISH to "MINERO ACTIVO",
            AppLanguage.CHINESE to "矿工活跃",
            AppLanguage.ARABIC to "عامل التعدين نشط",
            AppLanguage.JAPANESE to "マイナー稼働中"
        ),
        "game_miner_inactive" to mapOf(
            AppLanguage.INDONESIAN to "MINER TIDAK AKTIF",
            AppLanguage.ENGLISH to "MINER NOT ACTIVE",
            AppLanguage.SPANISH to "MINERO INACTIVO",
            AppLanguage.CHINESE to "矿工未活跃",
            AppLanguage.ARABIC to "عامل التعدين غير نشط",
            AppLanguage.JAPANESE to "マイナー停止中"
        ),
        "game_miner_hint" to mapOf(
            AppLanguage.INDONESIAN to "Aktifkan untuk mula mining",
            AppLanguage.ENGLISH to "Activate to start mining",
            AppLanguage.SPANISH to "Activa para comenzar a minar",
            AppLanguage.CHINESE to "激活以开始挖矿",
            AppLanguage.ARABIC to "قم بالتنشيط لبدء التعدين",
            AppLanguage.JAPANESE to "有効化してマイニングを開始"
        ),
        "game_miner_wait" to mapOf(
            AppLanguage.INDONESIAN to "Aktif — tunggu tamat",
            AppLanguage.ENGLISH to "Active — wait until end",
            AppLanguage.SPANISH to "Activo — espera el final",
            AppLanguage.CHINESE to "活跃 — 等待结束",
            AppLanguage.ARABIC to "نشط — انتظر حتى النهاية",
            AppLanguage.JAPANESE to "稼働中 — 終了まで待機"
        ),
        "game_miner_button" to mapOf(
            AppLanguage.INDONESIAN to "Aktifkan Miner (Tonton Iklan)",
            AppLanguage.ENGLISH to "Activate Miner (Watch Ad)",
            AppLanguage.SPANISH to "Activar Minero (Ver Anuncio)",
            AppLanguage.CHINESE to "激活矿工（观看广告）",
            AppLanguage.ARABIC to "تنشيط عامل التعدين (مشاهدة إعلان)",
            AppLanguage.JAPANESE to "マイナーを有効化（広告を見る）"
        ),
        "game_mining_power" to mapOf(
            AppLanguage.INDONESIAN to "Mining Power:",
            AppLanguage.ENGLISH to "Mining Power:",
            AppLanguage.SPANISH to "Poder de minería:",
            AppLanguage.CHINESE to "挖矿算力：",
            AppLanguage.ARABIC to "قوة التعدين:",
            AppLanguage.JAPANESE to "マイニングパワー:"
        ),
        "game_rate_points" to mapOf(
            AppLanguage.INDONESIAN to "Rate Poin:",
            AppLanguage.ENGLISH to "Rate RTP:",
            AppLanguage.SPANISH to "Tasa de puntos:",
            AppLanguage.CHINESE to "积分速率：",
            AppLanguage.ARABIC to "معدل النقاط:",
            AppLanguage.JAPANESE to "ポイントレート:"
        ),
        "game_your_points" to mapOf(
            AppLanguage.INDONESIAN to "Saldo Poin Anda:",
            AppLanguage.ENGLISH to "Your Point Balance:",
            AppLanguage.SPANISH to "Tu saldo de puntos:",
            AppLanguage.CHINESE to "您的积分余额：",
            AppLanguage.ARABIC to "رصيد نقاطك:",
            AppLanguage.JAPANESE to "ポイント残高:"
        ),
        "game_buy_now" to mapOf(
            AppLanguage.INDONESIAN to "Beli Sekarang",
            AppLanguage.ENGLISH to "Buy Now",
            AppLanguage.SPANISH to "Comprar ahora",
            AppLanguage.CHINESE to "立即购买",
            AppLanguage.ARABIC to "اشتر الآن",
            AppLanguage.JAPANESE to "今すぐ購入"
        ),
        "common_cancel" to mapOf(
            AppLanguage.INDONESIAN to "Batal",
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.SPANISH to "Cancelar",
            AppLanguage.CHINESE to "取消",
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.JAPANESE to "キャンセル"
        ),
        "game_install" to mapOf(
            AppLanguage.INDONESIAN to "Pasang",
            AppLanguage.ENGLISH to "Install",
            AppLanguage.SPANISH to "Instalar",
            AppLanguage.CHINESE to "安装",
            AppLanguage.ARABIC to "تثبيت",
            AppLanguage.JAPANESE to "設置"
        ),
        "game_open_shop" to mapOf(
            AppLanguage.INDONESIAN to "Buka Toko Item",
            AppLanguage.ENGLISH to "Open Item Shop",
            AppLanguage.SPANISH to "Abrir tienda",
            AppLanguage.CHINESE to "打开商店",
            AppLanguage.ARABIC to "افتح المتجر",
            AppLanguage.JAPANESE to "ショップを開く"
        ),
        "game_total_hash" to mapOf(
            AppLanguage.INDONESIAN to "Total Hash Rate",
            AppLanguage.ENGLISH to "Total Hash Rate",
            AppLanguage.SPANISH to "Tasa hash total",
            AppLanguage.CHINESE to "总算力",
            AppLanguage.ARABIC to "إجمالي معدل التجزئة",
            AppLanguage.JAPANESE to "合計ハッシュレート"
        ),
        "game_point_speed" to mapOf(
            AppLanguage.INDONESIAN to "Kecepatan RTP",
            AppLanguage.ENGLISH to "Rate RTP",
            AppLanguage.SPANISH to "Velocidad de puntos",
            AppLanguage.CHINESE to "积分速度",
            AppLanguage.ARABIC to "سرعة النقاط",
            AppLanguage.JAPANESE to "ポイント速度"
        ),
        "game_buy_item" to mapOf(
            AppLanguage.INDONESIAN to "Beli Item",
            AppLanguage.ENGLISH to "Buy Item",
            AppLanguage.SPANISH to "Comprar",
            AppLanguage.CHINESE to "购买物品",
            AppLanguage.ARABIC to "شراء عنصر",
            AppLanguage.JAPANESE to "アイテム購入"
        ),
        "game_play" to mapOf(
            AppLanguage.INDONESIAN to "Main",
            AppLanguage.ENGLISH to "Play",
            AppLanguage.SPANISH to "Jugar",
            AppLanguage.CHINESE to "玩",
            AppLanguage.ARABIC to "العب",
            AppLanguage.JAPANESE to "プレイ"
        ),
        "game_need_more_points" to mapOf(
            AppLanguage.INDONESIAN to "Butuh Poin Lebih Banyak?",
            AppLanguage.ENGLISH to "Need More Points?",
            AppLanguage.SPANISH to "¿Necesitas más puntos?",
            AppLanguage.CHINESE to "需要更多积分？",
            AppLanguage.ARABIC to "تحتاج المزيد من النقاط؟",
            AppLanguage.JAPANESE to "もっとポイントが必要？"
        ),
        "game_complete_missions" to mapOf(
            AppLanguage.INDONESIAN to "Selesaikan misi Like, Subscribe, Nonton, & Web",
            AppLanguage.ENGLISH to "Complete Like, Subscribe, Watch, & Web missions",
            AppLanguage.SPANISH to "Completa misiones de Like, Suscribir, Ver y Web",
            AppLanguage.CHINESE to "完成点赞、订阅、观看和网页任务",
            AppLanguage.ARABIC to "أكمل مهام الإعجاب والاشتراك والمشاهدة والويب",
            AppLanguage.JAPANESE to "いいね、登録、視聴、ウェブのミッションを完了"
        ),
        "game_open_missions" to mapOf(
            AppLanguage.INDONESIAN to "Buka Misi",
            AppLanguage.ENGLISH to "Open Missions",
            AppLanguage.SPANISH to "Abrir misiones",
            AppLanguage.CHINESE to "打开任务",
            AppLanguage.ARABIC to "افتح المهام",
            AppLanguage.JAPANESE to "ミッションを開く"
        ),
        "game_coinrush_title" to mapOf(
            AppLanguage.INDONESIAN to "Crypto Coin Tap Rush",
            AppLanguage.ENGLISH to "Crypto Coin Tap Rush",
            AppLanguage.SPANISH to "Crypto Coin Tap Rush",
            AppLanguage.CHINESE to "加密金币冲刺",
            AppLanguage.ARABIC to "Crypto Coin Tap Rush",
            AppLanguage.JAPANESE to "クリプトコインタップラッシュ"
        ),
        "game_coinrush_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Mini-Game Penghasil Power Royaltree",
            AppLanguage.ENGLISH to "Royaltree Power Minigame",
            AppLanguage.SPANISH to "Minijuego de poder Royaltree",
            AppLanguage.CHINESE to "Royaltree 算力小游戏",
            AppLanguage.ARABIC to "لعبة مصغرة لتوليد القوة",
            AppLanguage.JAPANESE to "Royaltree パワーミニゲーム"
        ),
        "game_start_now" to mapOf(
            AppLanguage.INDONESIAN to "Mulai Game Sekarang (15s)",
            AppLanguage.ENGLISH to "Start Game Now (15s)",
            AppLanguage.SPANISH to "Comenzar ahora (15s)",
            AppLanguage.CHINESE to "立即开始 (15秒)",
            AppLanguage.ARABIC to "ابدأ اللعبة الآن (15 ثانية)",
            AppLanguage.JAPANESE to "今すぐ開始 (15秒)"
        ),
        "coinrush_tagline" to mapOf(
            AppLanguage.INDONESIAN to "Tap koin sebelum hilang!",
            AppLanguage.ENGLISH to "Tap coins before they disappear!",
            AppLanguage.SPANISH to "¡Toca las monedas antes de que desaparezcan!",
            AppLanguage.CHINESE to "在金币消失前点击它们！",
            AppLanguage.ARABIC to "اضغط على العملات قبل أن تختفي!",
            AppLanguage.JAPANESE to "コインが消える前にタップ！"
        ),
        "coinrush_score" to mapOf(
            AppLanguage.INDONESIAN to "SKOR",
            AppLanguage.ENGLISH to "SCORE",
            AppLanguage.SPANISH to "PUNTUACIÓN",
            AppLanguage.CHINESE to "得分",
            AppLanguage.ARABIC to "النتيجة",
            AppLanguage.JAPANESE to "スコア"
        ),
        "coinrush_time" to mapOf(
            AppLanguage.INDONESIAN to "WAKTU",
            AppLanguage.ENGLISH to "TIME",
            AppLanguage.SPANISH to "TIEMPO",
            AppLanguage.CHINESE to "时间",
            AppLanguage.ARABIC to "الوقت",
            AppLanguage.JAPANESE to "時間"
        ),
        "coinrush_start_hint" to mapOf(
            AppLanguage.INDONESIAN to "Tekan MULAI untuk bermain",
            AppLanguage.ENGLISH to "Press START to play",
            AppLanguage.SPANISH to "Presiona INICIAR para jugar",
            AppLanguage.CHINESE to "按开始按钮开始游戏",
            AppLanguage.ARABIC to "اضغط ابدأ للعب",
            AppLanguage.JAPANESE to "スタートを押してプレイ"
        ),
        "coinrush_start" to mapOf(
            AppLanguage.INDONESIAN to "MULAI",
            AppLanguage.ENGLISH to "START",
            AppLanguage.SPANISH to "INICIAR",
            AppLanguage.CHINESE to "开始",
            AppLanguage.ARABIC to "ابدأ",
            AppLanguage.JAPANESE to "スタート"
        ),
        "coinrush_play_again" to mapOf(
            AppLanguage.INDONESIAN to "MAIN LAGI",
            AppLanguage.ENGLISH to "PLAY AGAIN",
            AppLanguage.SPANISH to "JUGAR DE NUEVO",
            AppLanguage.CHINESE to "再玩一次",
            AppLanguage.ARABIC to "العب مرة أخرى",
            AppLanguage.JAPANESE to "もう一度プレイ"
        ),
        "coinrush_exit" to mapOf(
            AppLanguage.INDONESIAN to "KELUAR",
            AppLanguage.ENGLISH to "EXIT",
            AppLanguage.SPANISH to "SALIR",
            AppLanguage.CHINESE to "退出",
            AppLanguage.ARABIC to "خروج",
            AppLanguage.JAPANESE to "終了"
        ),
        "chat_title" to mapOf(
            AppLanguage.INDONESIAN to "Global Chat",
            AppLanguage.ENGLISH to "Global Chat",
            AppLanguage.SPANISH to "Chat Global",
            AppLanguage.CHINESE to "全球聊天",
            AppLanguage.ARABIC to "الدردشة العالمية",
            AppLanguage.JAPANESE to "グローバルチャット"
        ),
        "chat_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "50 pesan terakhir",
            AppLanguage.ENGLISH to "Last 50 messages",
            AppLanguage.SPANISH to "Últimos 50 mensajes",
            AppLanguage.CHINESE to "最近50条消息",
            AppLanguage.ARABIC to "آخر 50 رسالة",
            AppLanguage.JAPANESE to "最後の50メッセージ"
        ),
        "chat_empty" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada pesan",
            AppLanguage.ENGLISH to "No messages yet",
            AppLanguage.SPANISH to "Aún no hay mensajes",
            AppLanguage.CHINESE to "暂无消息",
            AppLanguage.ARABIC to "لا توجد رسائل بعد",
            AppLanguage.JAPANESE to "メッセージはまだありません"
        ),
        "chat_hint" to mapOf(
            AppLanguage.INDONESIAN to "Tulis pesan...",
            AppLanguage.ENGLISH to "Type a message...",
            AppLanguage.SPANISH to "Escribe un mensaje...",
            AppLanguage.CHINESE to "输入消息...",
            AppLanguage.ARABIC to "اكتب رسالة...",
            AppLanguage.JAPANESE to "メッセージを入力..."
        ),
        "wp_title" to mapOf(
            AppLanguage.INDONESIAN to "Royaltree Whitepaper",
            AppLanguage.ENGLISH to "Royaltree Whitepaper",
            AppLanguage.SPANISH to "Royaltree Whitepaper",
            AppLanguage.CHINESE to "Royaltree 白皮书",
            AppLanguage.ARABIC to "الورقة البيضاء رويالتري",
            AppLanguage.JAPANESE to "ロイヤルツリーホワイトペーパー"
        ),
        "wp_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Versi 1.0 • 2026",
            AppLanguage.ENGLISH to "Version 1.0 • 2026",
            AppLanguage.SPANISH to "Versión 1.0 • 2026",
            AppLanguage.CHINESE to "版本 1.0 • 2026",
            AppLanguage.ARABIC to "الإصدار 1.0 • 2026",
            AppLanguage.JAPANESE to "バージョン 1.0 • 2026"
        ),
        "wp_what_title" to mapOf(
            AppLanguage.INDONESIAN to "Apa itu Royaltree?",
            AppLanguage.ENGLISH to "What is Royaltree?",
            AppLanguage.SPANISH to "¿Qué es Royaltree?",
            AppLanguage.CHINESE to "什么是Royaltree？",
            AppLanguage.ARABIC to "ما هو رويالتري؟",
            AppLanguage.JAPANESE to "Royaltreeとは？"
        ),
        "wp_what_content" to mapOf(
            AppLanguage.INDONESIAN to "Royaltree adalah platform mining berbasis mobile yang menggabungkan gamifikasi, teknologi blockchain-inspired, dan ekonomi token yang adil. Pengguna dapat menambang RTP melalui miner virtual, menyelesaikan misi, dan mengikuti mini-game.",
            AppLanguage.ENGLISH to "Royaltree is a mobile-based mining platform that combines gamification, blockchain-inspired technology, and a fair token economy. Users can mine RTP through virtual miners, complete missions, and join mini-games.",
            AppLanguage.SPANISH to "Royaltree es una plataforma de minería móvil que combina gamificación, tecnología inspirada en blockchain y una economía de tokens justa.",
            AppLanguage.CHINESE to "Royaltree是一个基于移动端的挖矿平台，结合了游戏化、区块链启发技术和公平的代币经济。",
            AppLanguage.ARABIC to "رويالتري هي منصة تعدين قائمة على الهاتف المحمول تجمع بين اللعب والتكنولوجيا المستوحاة من البلوكشين واقتصاد رمزي عادل.",
            AppLanguage.JAPANESE to "Royaltreeは、ゲーミフィケーション、ブロックチェーンにインスパイアされた技術、公平なトークン経済を組み合わせたモバイルベースのマイニングプラットフォームです。"
        ),
        "wp_mining_title" to mapOf(
            AppLanguage.INDONESIAN to "Cara Mining",
            AppLanguage.ENGLISH to "How to Mine",
            AppLanguage.SPANISH to "Cómo Minar",
            AppLanguage.CHINESE to "如何挖矿",
            AppLanguage.ARABIC to "كيفية التعدين",
            AppLanguage.JAPANESE to "マイニング方法"
        ),
        "wp_mining_content" to mapOf(
            AppLanguage.INDONESIAN to "1. Beli miner dari marketplace\n2. Tempatkan miner di rak\n3. Aktifkan miner 6 jam dengan menonton iklan\n4. Miner menghasilkan RTP secara pasif\n5. Block reward dibagikan setiap 30 menit",
            AppLanguage.ENGLISH to "1. Buy miners from marketplace\n2. Place miners on rack\n3. Activate miner for 6 hours by watching ads\n4. Miners generate RTP passively\n5. Block reward distributed every 30 minutes",
            AppLanguage.SPANISH to "1. Compra mineros\n2. Colócalos en el estante\n3. Activa 6 horas con anuncios\n4. Generan RTP pasivamente\n5. Recompensa cada 30 minutos",
            AppLanguage.CHINESE to "1. 从市场购买矿工\n2. 放置在架子上\n3. 观看广告激活6小时\n4. 矿工被动生成RTP\n5. 每30分钟分配区块奖励",
            AppLanguage.ARABIC to "1. اشترِ عمال التعدين\n2. ضعهم على الرف\n3. نشط لمدة 6 ساعات\n4. ينتجون RTP بشكل سلبي\n5. مكافأة الكتلة كل 30 دقيقة",
            AppLanguage.JAPANESE to "1. マーケットプレイスでマイナーを購入\n2. ラックに配置\n3. 広告を見て6時間有効化\n4. マイナーが受動的にRTPを生成\n5. ブロック報酬は30分ごと"
        ),
        "wp_token_title" to mapOf(
            AppLanguage.INDONESIAN to "Token RTP",
            AppLanguage.ENGLISH to "RTP Token",
            AppLanguage.SPANISH to "Token RTP",
            AppLanguage.CHINESE to "RTP 代币",
            AppLanguage.ARABIC to "رمز RTP",
            AppLanguage.JAPANESE to "RTPトークン"
        ),
        "wp_token_content" to mapOf(
            AppLanguage.INDONESIAN to "RTP adalah token utility internal Royaltree. Digunakan untuk:\n• Membeli & upgrade miner\n• Transfer ke sesama pengguna\n• Menukar dengan reward\n\nTotal supply terbatas, dengan halving setiap 1,000 block.",
            AppLanguage.ENGLISH to "RTP is Royaltree's internal utility token. Used for:\n• Buying & upgrading miners\n• Transferring to other users\n• Exchanging for rewards\n\nLimited supply, with halving every 1,000 blocks.",
            AppLanguage.SPANISH to "RTP es el token de utilidad interno. Usado para:\n• Comprar y mejorar mineros\n• Transferir a otros usuarios\n• Canjear recompensas\n\nSuministro limitado.",
            AppLanguage.CHINESE to "RTP是Royaltree的内部实用代币。用于：\n• 购买和升级矿工\n• 转账给其他用户\n• 兑换奖励\n\n供应有限，每1,000个区块减半。",
            AppLanguage.ARABIC to "RTP هو رمز المنفعة الداخلي. يُستخدم لـ:\n• شراء وترقية عمال التعدين\n• التحويل للمستخدمين الآخرين\n• استبدال المكافآت\n\nعرض محدود.",
            AppLanguage.JAPANESE to "RTPはRoyaltreeの内部ユーティリティトークンです。用途：\n• マイナーの購入とアップグレード\n• 他のユーザーへの転送\n• 報酬との交換\n\n供給量は限られており、1,000ブロックごとに半減します。"
        ),
        "wp_economy_title" to mapOf(
            AppLanguage.INDONESIAN to "Ekonomi",
            AppLanguage.ENGLISH to "Economy",
            AppLanguage.SPANISH to "Economía",
            AppLanguage.CHINESE to "经济",
            AppLanguage.ARABIC to "الاقتصاد",
            AppLanguage.JAPANESE to "経済"
        ),
        "wp_economy_content" to mapOf(
            AppLanguage.INDONESIAN to "Ekonomi Royaltree dirancang berkelanjutan:\n• 10% rate user masuk ke Pool Fund\n• Pool Fund membiayai block reward\n• Difficulty naik seiring total hashrate\n• Anti-whale: diminishing return & soft cap",
            AppLanguage.ENGLISH to "Royaltree's economy is designed for sustainability:\n• 10% of user rate goes to Pool Fund\n• Pool Fund finances block rewards\n• Difficulty rises with total hashrate\n• Anti-whale: diminishing return & soft cap",
            AppLanguage.SPANISH to "La economía de Royaltree está diseñada para la sostenibilidad:\n• 10% de la tasa del usuario va al Pool Fund\n• El Pool Fund financia las recompensas\n• La dificultad aumenta con el hashrate\n• Anti-ballena: retorno decreciente",
            AppLanguage.CHINESE to "Royaltree的经济设计为可持续：\n• 用户费率的10%进入资金池\n• 资金池为区块奖励提供资金\n• 难度随总算力上升\n• 反巨鲸：收益递减和软上限",
            AppLanguage.ARABIC to "اقتصاد رويالتري مصمم للاستدامة:\n• 10% من معدل المستخدم يذهب إلى صندوق المجمع\n• الصندوق يمول مكافآت الكتلة\n• تزداد الصعوبة مع إجمالي الهاش\n• مكافحة الحيتان: عوائد متناقصة",
            AppLanguage.JAPANESE to "Royaltreeの経済は持続可能性のために設計されています：\n• ユーザーレートの10%がプールファンドへ\n• プールファンドがブロック報酬を賄う\n• 難易度は総ハッシュレートに応じて上昇\n• アンチホエール：収穫逓減とソフトキャップ"
        ),
        "wp_roadmap_title" to mapOf(
            AppLanguage.INDONESIAN to "Roadmap",
            AppLanguage.ENGLISH to "Roadmap",
            AppLanguage.SPANISH to "Hoja de Ruta",
            AppLanguage.CHINESE to "路线图",
            AppLanguage.ARABIC to "خارطة الطريق",
            AppLanguage.JAPANESE to "ロードマップ"
        ),
        "wp_roadmap_content" to mapOf(
            AppLanguage.INDONESIAN to "Q4 2026: Rilis publik, mining tycoon\nQ1 2027: Mini-game arcade, leaderboard musiman\nQ2 2027: Secondary market, tier miner baru\nQ3 2027: Integrasi e-wallet, withdrawal langsung",
            AppLanguage.ENGLISH to "Q4 2026: Public release, mining tycoon\nQ1 2027: Mini-game arcade, seasonal leaderboard\nQ2 2027: Secondary market, new miner tiers\nQ3 2027: E-wallet integration, direct withdrawal",
            AppLanguage.SPANISH to "Q4 2026: Lanzamiento público\nQ1 2027: Mini-juegos arcade\nQ2 2027: Mercado secundario\nQ3 2027: Integración de billetera",
            AppLanguage.CHINESE to "2026年Q4：公开发布\n2027年Q1：街机小游戏、季节性排行榜\n2027年Q2：二级市场、新矿工等级\n2027年Q3：电子钱包整合、直接提现",
            AppLanguage.ARABIC to "Q4 2026: الإصدار العام\nQ1 2027: ألعاب مصغرة\nQ2 2027: السوق الثانوي\nQ3 2027: تكامل المحفظة",
            AppLanguage.JAPANESE to "2026年Q4：公開リリース\n2027年Q1：ミニゲームアーケード\n2027年Q2：セカンダリマーケット\n2027年Q3：Eウォレット統合"
        ),
        "coinrush_final_score" to mapOf(
            AppLanguage.INDONESIAN to "Skor Akhir",
            AppLanguage.ENGLISH to "Final Score",
            AppLanguage.SPANISH to "Puntuación final",
            AppLanguage.CHINESE to "最终分数",
            AppLanguage.ARABIC to "النتيجة النهائية",
            AppLanguage.JAPANESE to "最終スコア"
        ),
        "history_search_hint" to mapOf(
            AppLanguage.INDONESIAN to "Cari berdasarkan judul, ref ID, atau nama kampanye...",
            AppLanguage.ENGLISH to "Search by title, ref ID, or campaign name...",
            AppLanguage.SPANISH to "Buscar por título, ID o campaña...",
            AppLanguage.CHINESE to "按标题、参考ID或活动名称搜索...",
            AppLanguage.ARABIC to "ابحث بالعنوان أو معرّف المرجع أو اسم الحملة...",
            AppLanguage.JAPANESE to "タイトル、参照ID、キャンペーン名で検索..."
        ),
        "history_empty" to mapOf(
            AppLanguage.INDONESIAN to "Tidak Ada Catatan Transaksi",
            AppLanguage.ENGLISH to "No Transaction Records",
            AppLanguage.SPANISH to "Sin registros de transacciones",
            AppLanguage.CHINESE to "没有交易记录",
            AppLanguage.ARABIC to "لا توجد سجلات معاملات",
            AppLanguage.JAPANESE to "取引履歴がありません"
        ),
        "referral_how_it_works" to mapOf(
            AppLanguage.INDONESIAN to "Cara Kerja",
            AppLanguage.ENGLISH to "How It Works",
            AppLanguage.SPANISH to "Cómo funciona",
            AppLanguage.CHINESE to "如何运作",
            AppLanguage.ARABIC to "كيف يعمل",
            AppLanguage.JAPANESE to "仕組み"
        ),
        // ============ Referral Stats (NEW) ============
        "referral_friends" to mapOf(
            AppLanguage.INDONESIAN to "Teman",
            AppLanguage.ENGLISH to "Friends",
            AppLanguage.SPANISH to "Amigos",
            AppLanguage.CHINESE to "好友",
            AppLanguage.ARABIC to "الأصدقاء",
            AppLanguage.JAPANESE to "友達"
        ),
        "referral_invited" to mapOf(
            AppLanguage.INDONESIAN to "Diundang",
            AppLanguage.ENGLISH to "Invited",
            AppLanguage.SPANISH to "Invitados",
            AppLanguage.CHINESE to "已邀请",
            AppLanguage.ARABIC to "مدعو",
            AppLanguage.JAPANESE to "招待済み"
        ),
        "referral_commission" to mapOf(
            AppLanguage.INDONESIAN to "Komisi",
            AppLanguage.ENGLISH to "Commission",
            AppLanguage.SPANISH to "Comisión",
            AppLanguage.CHINESE to "佣金",
            AppLanguage.ARABIC to "العمولة",
            AppLanguage.JAPANESE to "コミッション"
        ),
        "referral_total" to mapOf(
            AppLanguage.INDONESIAN to "Total",
            AppLanguage.ENGLISH to "Total",
            AppLanguage.SPANISH to "Total",
            AppLanguage.CHINESE to "总计",
            AppLanguage.ARABIC to "الإجمالي",
            AppLanguage.JAPANESE to "合計"
        ),
        "referral_empty_message" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada teman yang diundang. Ayo bagikan kode kamu!",
            AppLanguage.ENGLISH to "No friends invited yet. Share your code now!",
            AppLanguage.SPANISH to "Aún no has invitado amigos. ¡Comparte tu código!",
            AppLanguage.CHINESE to "还没有邀请好友。快分享你的代码吧！",
            AppLanguage.ARABIC to "لم تدعُ أي صديق بعد. شارك رمزك الآن!",
            AppLanguage.JAPANESE to "まだ友達を招待していません。コードをシェアしよう！"
        ),
        // ============ GameRoom (NEW) ============
        "game_unlock_miner" to mapOf(
            AppLanguage.INDONESIAN to "Unlock Miner dengan Token",
            AppLanguage.ENGLISH to "Unlock Miner with Token",
            AppLanguage.SPANISH to "Desbloquear Minero con Token",
            AppLanguage.CHINESE to "用代币解锁矿工",
            AppLanguage.ARABIC to "افتح Miner بالرمز",
            AppLanguage.JAPANESE to "トークンでマイナーを解除"
        ),
        "game_unlock_miner_desc" to mapOf(
            AppLanguage.INDONESIAN to "Kumpulkan Miner Token dari iklan & misi untuk membuka miner baru!",
            AppLanguage.ENGLISH to "Collect Miner Tokens from ads & missions to unlock new miners!",
            AppLanguage.SPANISH to "¡Recoge Tokens de Minero de anuncios y misiones para desbloquear nuevos mineros!",
            AppLanguage.CHINESE to "从广告和任务中收集矿工代币来解锁新矿工！",
            AppLanguage.ARABIC to "اجمع رموز Miner من الإعلانات والمهام لفتح عمال مناجم جدد!",
            AppLanguage.JAPANESE to "広告とミッションでマイナートークンを集めて新しいマイナーを解除しよう！"
        ),
        // ============ Profile Mining (NEW) ============
        "profile_rate_day" to mapOf(
            AppLanguage.INDONESIAN to "Rate/hari",
            AppLanguage.ENGLISH to "Rate/day",
            AppLanguage.SPANISH to "Tarifa/día",
            AppLanguage.CHINESE to "每日速率",
            AppLanguage.ARABIC to "المعدل/اليوم",
            AppLanguage.JAPANESE to "レート/日"
        ),
        "profile_unclaimed" to mapOf(
            AppLanguage.INDONESIAN to "Unclaimed",
            AppLanguage.ENGLISH to "Unclaimed",
            AppLanguage.SPANISH to "No reclamado",
            AppLanguage.CHINESE to "未领取",
            AppLanguage.ARABIC to "غير مطالب به",
            AppLanguage.JAPANESE to "未請求"
        ),
        // ============ Sweepstakes (NEW) ============
        "nav_sweepstakes" to mapOf(
            AppLanguage.INDONESIAN to "Sweepstakes",
            AppLanguage.ENGLISH to "Sweepstakes",
            AppLanguage.SPANISH to "Sorteos",
            AppLanguage.CHINESE to "抽奖",
            AppLanguage.ARABIC to "السحوبات",
            AppLanguage.JAPANESE to "スイープステークス"
        ),
        "sweepstakes_ends_at" to mapOf(
            AppLanguage.INDONESIAN to "Berakhir",
            AppLanguage.ENGLISH to "Ends",
            AppLanguage.SPANISH to "Termina",
            AppLanguage.CHINESE to "结束于",
            AppLanguage.ARABIC to "ينتهي",
            AppLanguage.JAPANESE to "終了"
        ),
        "sweepstakes_no_winners" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada pemenang",
            AppLanguage.ENGLISH to "No winners yet",
            AppLanguage.SPANISH to "Aún no hay ganadores",
            AppLanguage.CHINESE to "还没有获奖者",
            AppLanguage.ARABIC to "لا يوجد فائزون بعد",
            AppLanguage.JAPANESE to "まだ当選者なし"
        ),
        "sweepstakes_first_period" to mapOf(
            AppLanguage.INDONESIAN to "Periode pertama sedang berjalan",
            AppLanguage.ENGLISH to "First period is running",
            AppLanguage.SPANISH to "El primer período está en curso",
            AppLanguage.CHINESE to "第一周期进行中",
            AppLanguage.ARABIC to "الفترة الأولى قيد التشغيل",
            AppLanguage.JAPANESE to "最初の期間が進行中"
        ),
        "sweepstakes_rank" to mapOf(
            AppLanguage.INDONESIAN to "Juara",
            AppLanguage.ENGLISH to "Rank",
            AppLanguage.SPANISH to "Puesto",
            AppLanguage.CHINESE to "排名",
            AppLanguage.ARABIC to "المرتبة",
            AppLanguage.JAPANESE to "順位"
        ),
        "sweepstakes_you_won" to mapOf(
            AppLanguage.INDONESIAN to "🎉 Anda menang!",
            AppLanguage.ENGLISH to "🎉 You won!",
            AppLanguage.SPANISH to "🎉 ¡Ganaste!",
            AppLanguage.CHINESE to "🎉 你赢了！",
            AppLanguage.ARABIC to "🎉 لقد فزت!",
            AppLanguage.JAPANESE to "🎉 当選！"
        ),
        "sweepstakes_completed" to mapOf(
            AppLanguage.INDONESIAN to "Selesai",
            AppLanguage.ENGLISH to "Completed",
            AppLanguage.SPANISH to "Completado",
            AppLanguage.CHINESE to "已完成",
            AppLanguage.ARABIC to "مكتمل",
            AppLanguage.JAPANESE to "完了"
        ),
        "sweepstakes_total" to mapOf(
            AppLanguage.INDONESIAN to "Total",
            AppLanguage.ENGLISH to "Total",
            AppLanguage.SPANISH to "Total",
            AppLanguage.CHINESE to "总计",
            AppLanguage.ARABIC to "الإجمالي",
            AppLanguage.JAPANESE to "合計"
        ),
        "sweepstakes_you_have" to mapOf(
            AppLanguage.INDONESIAN to "Anda punya",
            AppLanguage.ENGLISH to "You have",
            AppLanguage.SPANISH to "Tienes",
            AppLanguage.CHINESE to "你有",
            AppLanguage.ARABIC to "لديك",
            AppLanguage.JAPANESE to "あなたは持っています"
        ),
        "sweepstakes_buy" to mapOf(
            AppLanguage.INDONESIAN to "BELI",
            AppLanguage.ENGLISH to "BUY",
            AppLanguage.SPANISH to "COMPRAR",
            AppLanguage.CHINESE to "购买",
            AppLanguage.ARABIC to "شراء",
            AppLanguage.JAPANESE to "購入"
        ),
        "sweepstakes_cancel" to mapOf(
            AppLanguage.INDONESIAN to "Batal",
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.SPANISH to "Cancelar",
            AppLanguage.CHINESE to "取消",
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.JAPANESE to "キャンセル"
        ),
        "sweepstakes_winners_last" to mapOf(
            AppLanguage.INDONESIAN to "🏆 Pemenang Lalu",
            AppLanguage.ENGLISH to "🏆 Past Winners",
            AppLanguage.SPANISH to "🏆 Ganadores Pasados",
            AppLanguage.CHINESE to "🏆 历届获奖者",
            AppLanguage.ARABIC to "🏆 الفائزون السابقون",
            AppLanguage.JAPANESE to "🏆 過去の当選者"
        ),
        "sweepstakes_ticket_info" to mapOf(
            AppLanguage.INDONESIAN to "kupon",
            AppLanguage.ENGLISH to "tickets",
            AppLanguage.SPANISH to "boletos",
            AppLanguage.CHINESE to "票",
            AppLanguage.ARABIC to "تذاكر",
            AppLanguage.JAPANESE to "チケット"
        ),
        "sweepstakes_winner_info" to mapOf(
            AppLanguage.INDONESIAN to "pemenang",
            AppLanguage.ENGLISH to "winners",
            AppLanguage.SPANISH to "ganadores",
            AppLanguage.CHINESE to "获奖者",
            AppLanguage.ARABIC to "الفائزين",
            AppLanguage.JAPANESE to "当選者"
        ),
        "sweepstakes_title" to mapOf(
            AppLanguage.INDONESIAN to "🎟️ Sweepstakes",
            AppLanguage.ENGLISH to "🎟️ Sweepstakes",
            AppLanguage.SPANISH to "🎟️ Sorteos",
            AppLanguage.CHINESE to "🎟️ 抽奖",
            AppLanguage.ARABIC to "🎟️ السحوبات",
            AppLanguage.JAPANESE to "🎟️ スイープステークス"
        ),
        "sweepstakes_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Menang besar!",
            AppLanguage.ENGLISH to "Win big!",
            AppLanguage.SPANISH to "¡Gana en grande!",
            AppLanguage.CHINESE to "赢大奖！",
            AppLanguage.ARABIC to "اربح كثيراً!",
            AppLanguage.JAPANESE to "大当たり！"
        ),

        // ============ Transfer (NEW) ============
        "nav_transfer" to mapOf(
            AppLanguage.INDONESIAN to "Transfer",
            AppLanguage.ENGLISH to "Transfer",
            AppLanguage.SPANISH to "Transferir",
            AppLanguage.CHINESE to "转账",
            AppLanguage.ARABIC to "تحويل",
            AppLanguage.JAPANESE to "転送"
        ),
        "transfer_title" to mapOf(
            AppLanguage.INDONESIAN to "Transfer RTP",
            AppLanguage.ENGLISH to "Transfer RTP",
            AppLanguage.SPANISH to "Transferir RTP",
            AppLanguage.CHINESE to "转账 RTP",
            AppLanguage.ARABIC to "تحويل RTP",
            AppLanguage.JAPANESE to "RTP 転送"
        ),
        "transfer_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Kirim saldo poin ke user lain",
            AppLanguage.ENGLISH to "Send points to other users",
            AppLanguage.SPANISH to "Envía puntos a otros usuarios",
            AppLanguage.CHINESE to "向其他用户发送积分",
            AppLanguage.ARABIC to "أرسل النقاط لمستخدمين آخرين",
            AppLanguage.JAPANESE to "他のユーザーにポイントを送信"
        ),
        "transfer_balance" to mapOf(
            AppLanguage.INDONESIAN to "Saldo Anda",
            AppLanguage.ENGLISH to "Your Balance",
            AppLanguage.SPANISH to "Tu Saldo",
            AppLanguage.CHINESE to "您的余额",
            AppLanguage.ARABIC to "رصيدك",
            AppLanguage.JAPANESE to "あなたの残高"
        ),
        "transfer_recipient" to mapOf(
            AppLanguage.INDONESIAN to "Penerima",
            AppLanguage.ENGLISH to "Recipient",
            AppLanguage.SPANISH to "Destinatario",
            AppLanguage.CHINESE to "收件人",
            AppLanguage.ARABIC to "المستلم",
            AppLanguage.JAPANESE to "受取人"
        ),
        "transfer_search_hint" to mapOf(
            AppLanguage.INDONESIAN to "@username / kode referral",
            AppLanguage.ENGLISH to "@username / referral code",
            AppLanguage.SPANISH to "@usuario / código",
            AppLanguage.CHINESE to "@用户名 / 推荐码",
            AppLanguage.ARABIC to "@اسم المستخدم / رمز",
            AppLanguage.JAPANESE to "@ユーザー名 / コード"
        ),
        "transfer_search" to mapOf(
            AppLanguage.INDONESIAN to "Cari",
            AppLanguage.ENGLISH to "Search",
            AppLanguage.SPANISH to "Buscar",
            AppLanguage.CHINESE to "搜索",
            AppLanguage.ARABIC to "بحث",
            AppLanguage.JAPANESE to "検索"
        ),
        "transfer_amount" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Transfer",
            AppLanguage.ENGLISH to "Transfer Amount",
            AppLanguage.SPANISH to "Cantidad",
            AppLanguage.CHINESE to "转账金额",
            AppLanguage.ARABIC to "مبلغ التحويل",
            AppLanguage.JAPANESE to "転送額"
        ),
        "transfer_note" to mapOf(
            AppLanguage.INDONESIAN to "Catatan (opsional)",
            AppLanguage.ENGLISH to "Note (optional)",
            AppLanguage.SPANISH to "Nota (opcional)",
            AppLanguage.CHINESE to "备注（可选）",
            AppLanguage.ARABIC to "ملاحظة (اختياري)",
            AppLanguage.JAPANESE to "メモ（オプション）"
        ),
        "transfer_send" to mapOf(
            AppLanguage.INDONESIAN to "KIRIM",
            AppLanguage.ENGLISH to "SEND",
            AppLanguage.SPANISH to "ENVIAR",
            AppLanguage.CHINESE to "发送",
            AppLanguage.ARABIC to "إرسال",
            AppLanguage.JAPANESE to "送信"
        ),
        // ============ Slot Unlock (NEW) ============
        "slot_locked" to mapOf(
            AppLanguage.INDONESIAN to "Terkunci",
            AppLanguage.ENGLISH to "Locked",
            AppLanguage.SPANISH to "Bloqueado",
            AppLanguage.CHINESE to "已锁定",
            AppLanguage.ARABIC to "مقفل",
            AppLanguage.JAPANESE to "ロック中"
        ),
        "slot_unlock_title" to mapOf(
            AppLanguage.INDONESIAN to "Buka Slot",
            AppLanguage.ENGLISH to "Unlock Slot",
            AppLanguage.SPANISH to "Desbloquear Ranura",
            AppLanguage.CHINESE to "解锁插槽",
            AppLanguage.ARABIC to "فتح الفتحة",
            AppLanguage.JAPANESE to "スロットを解除"
        ),
        "slot_unlock_message" to mapOf(
            AppLanguage.INDONESIAN to "Buka slot ini untuk tambah kapasitas mining kamu!",
            AppLanguage.ENGLISH to "Unlock this slot to expand your mining capacity!",
            AppLanguage.SPANISH to "¡Desbloquea esta ranura para ampliar tu capacidad!",
            AppLanguage.CHINESE to "解锁此插槽以扩展您的挖矿容量！",
            AppLanguage.ARABIC to "افتح هذه الفتحة لتوسيع سعة التعدين!",
            AppLanguage.JAPANESE to "このスロットを解除してマイニング容量を拡張！"
        ),
        "slot_buy" to mapOf(
            AppLanguage.INDONESIAN to "BELI",
            AppLanguage.ENGLISH to "BUY",
            AppLanguage.SPANISH to "COMPRAR",
            AppLanguage.CHINESE to "购买",
            AppLanguage.ARABIC to "شراء",
            AppLanguage.JAPANESE to "購入"
        ),

        "transfer_history" to mapOf(
            AppLanguage.INDONESIAN to "Riwayat Transfer",
            AppLanguage.ENGLISH to "Transfer History",
            AppLanguage.SPANISH to "Historial",
            AppLanguage.CHINESE to "转账记录",
            AppLanguage.ARABIC to "سجل التحويلات",
            AppLanguage.JAPANESE to "転送履歴"
        ),

        // ============ Tier Labels (NEW) ============
        "tier_royal" to mapOf(
            AppLanguage.INDONESIAN to "Royal",
            AppLanguage.ENGLISH to "Royal",
            AppLanguage.SPANISH to "Real",
            AppLanguage.CHINESE to "皇家",
            AppLanguage.ARABIC to "ملكي",
            AppLanguage.JAPANESE to "ロイヤル"
        ),
        "tier_vip" to mapOf(
            AppLanguage.INDONESIAN to "VIP",
            AppLanguage.ENGLISH to "VIP",
            AppLanguage.SPANISH to "VIP",
            AppLanguage.CHINESE to "VIP",
            AppLanguage.ARABIC to "VIP",
            AppLanguage.JAPANESE to "VIP"
        ),
        "tier_premium" to mapOf(
            AppLanguage.INDONESIAN to "Premium",
            AppLanguage.ENGLISH to "Premium",
            AppLanguage.SPANISH to "Premium",
            AppLanguage.CHINESE to "高级",
            AppLanguage.ARABIC to "بريميوم",
            AppLanguage.JAPANESE to "プレミアム"
        ),
        "tier_starter" to mapOf(
            AppLanguage.INDONESIAN to "Pemula",
            AppLanguage.ENGLISH to "Starter",
            AppLanguage.SPANISH to "Principiante",
            AppLanguage.CHINESE to "入门",
            AppLanguage.ARABIC to "مبتدئ",
            AppLanguage.JAPANESE to "スターター"
        ),
        "tier_free" to mapOf(
            AppLanguage.INDONESIAN to "Gratis",
            AppLanguage.ENGLISH to "Free",
            AppLanguage.SPANISH to "Gratis",
            AppLanguage.CHINESE to "免费",
            AppLanguage.ARABIC to "مجاني",
            AppLanguage.JAPANESE to "無料"
        ),
        "profile_action_checkin" to mapOf(
            AppLanguage.INDONESIAN to "Check-in",
            AppLanguage.ENGLISH to "Check-in",
            AppLanguage.SPANISH to "Check-in",
            AppLanguage.CHINESE to "签到",
            AppLanguage.ARABIC to "تسجيل الوصول",
            AppLanguage.JAPANESE to "チェックイン"
        ),
        "profile_action_mining" to mapOf(
            AppLanguage.INDONESIAN to "Mining",
            AppLanguage.ENGLISH to "Mining",
            AppLanguage.SPANISH to "Minería",
            AppLanguage.CHINESE to "挖矿",
            AppLanguage.ARABIC to "التعدين",
            AppLanguage.JAPANESE to "マイニング"
        ),
        "profile_action_coinrush" to mapOf(
            AppLanguage.INDONESIAN to "Coin Rush",
            AppLanguage.ENGLISH to "Coin Rush",
            AppLanguage.SPANISH to "Coin Rush",
            AppLanguage.CHINESE to "金币冲刺",
            AppLanguage.ARABIC to "Coin Rush",
            AppLanguage.JAPANESE to "コインラッシュ"
        ),
        "profile_action_missions" to mapOf(
            AppLanguage.INDONESIAN to "Misi",
            AppLanguage.ENGLISH to "Missions",
            AppLanguage.SPANISH to "Misiones",
            AppLanguage.CHINESE to "任务",
            AppLanguage.ARABIC to "المهام",
            AppLanguage.JAPANESE to "ミッション"
        ),
        "profile_balance" to mapOf(
            AppLanguage.INDONESIAN to "Saldo",
            AppLanguage.ENGLISH to "Balance",
            AppLanguage.SPANISH to "Saldo",
            AppLanguage.CHINESE to "余额",
            AppLanguage.ARABIC to "الرصيد",
            AppLanguage.JAPANESE to "残高"
        ),
        "profile_commission" to mapOf(
            AppLanguage.INDONESIAN to "Komisi",
            AppLanguage.ENGLISH to "Commission",
            AppLanguage.SPANISH to "Comisión",
            AppLanguage.CHINESE to "佣金",
            AppLanguage.ARABIC to "عمولة",
            AppLanguage.JAPANESE to "手数料"
        ),
        "profile_edit" to mapOf(
            AppLanguage.INDONESIAN to "Edit Profil",
            AppLanguage.ENGLISH to "Edit Profile",
            AppLanguage.SPANISH to "Editar Perfil",
            AppLanguage.CHINESE to "编辑个人资料",
            AppLanguage.ARABIC to "تعديل الملف",
            AppLanguage.JAPANESE to "プロフィール編集"
        ),
        "profile_email_label" to mapOf(
            AppLanguage.INDONESIAN to "Email",
            AppLanguage.ENGLISH to "Email",
            AppLanguage.SPANISH to "Correo",
            AppLanguage.CHINESE to "邮箱",
            AppLanguage.ARABIC to "البريد الإلكتروني",
            AppLanguage.JAPANESE to "メール"
        ),
        "profile_language" to mapOf(
            AppLanguage.INDONESIAN to "Bahasa",
            AppLanguage.ENGLISH to "Language",
            AppLanguage.SPANISH to "Idioma",
            AppLanguage.CHINESE to "语言",
            AppLanguage.ARABIC to "اللغة",
            AppLanguage.JAPANESE to "言語"
        ),
        "profile_logout" to mapOf(
            AppLanguage.INDONESIAN to "Keluar",
            AppLanguage.ENGLISH to "Logout",
            AppLanguage.SPANISH to "Cerrar sesión",
            AppLanguage.CHINESE to "退出登录",
            AppLanguage.ARABIC to "تسجيل الخروج",
            AppLanguage.JAPANESE to "ログアウト"
        ),
        "profile_mining_status" to mapOf(
            AppLanguage.INDONESIAN to "Status Mining",
            AppLanguage.ENGLISH to "Mining Status",
            AppLanguage.SPANISH to "Estado de Minería",
            AppLanguage.CHINESE to "挖矿状态",
            AppLanguage.ARABIC to "حالة التعدين",
            AppLanguage.JAPANESE to "マイニング状況"
        ),
        "profile_name" to mapOf(
            AppLanguage.INDONESIAN to "Nama Lengkap",
            AppLanguage.ENGLISH to "Full Name",
            AppLanguage.SPANISH to "Nombre Completo",
            AppLanguage.CHINESE to "全名",
            AppLanguage.ARABIC to "الاسم الكامل",
            AppLanguage.JAPANESE to "氏名"
        ),
        "profile_phone" to mapOf(
            AppLanguage.INDONESIAN to "Nomor HP",
            AppLanguage.ENGLISH to "Phone Number",
            AppLanguage.SPANISH to "Número de Teléfono",
            AppLanguage.CHINESE to "电话号码",
            AppLanguage.ARABIC to "رقم الهاتف",
            AppLanguage.JAPANESE to "電話番号"
        ),
        "profile_power" to mapOf(
            AppLanguage.INDONESIAN to "Power",
            AppLanguage.ENGLISH to "Power",
            AppLanguage.SPANISH to "Potencia",
            AppLanguage.CHINESE to "算力",
            AppLanguage.ARABIC to "القوة",
            AppLanguage.JAPANESE to "パワー"
        ),
        "profile_quick_actions" to mapOf(
            AppLanguage.INDONESIAN to "Aksi Cepat",
            AppLanguage.ENGLISH to "Quick Actions",
            AppLanguage.SPANISH to "Acciones Rápidas",
            AppLanguage.CHINESE to "快捷操作",
            AppLanguage.ARABIC to "إجراءات سريعة",
            AppLanguage.JAPANESE to "クイックアクション"
        ),
        "profile_referral_friends" to mapOf(
            AppLanguage.INDONESIAN to "👥 Teman",
            AppLanguage.ENGLISH to "👥 Friends",
            AppLanguage.SPANISH to "👥 Amigos",
            AppLanguage.CHINESE to "👥 朋友",
            AppLanguage.ARABIC to "👥 الأصدقاء",
            AppLanguage.JAPANESE to "👥 友達"
        ),
        "profile_referral_invited" to mapOf(
            AppLanguage.INDONESIAN to "Diundang",
            AppLanguage.ENGLISH to "Invited",
            AppLanguage.SPANISH to "Invitados",
            AppLanguage.CHINESE to "已邀请",
            AppLanguage.ARABIC to "المدعوون",
            AppLanguage.JAPANESE to "招待済み"
        ),
        "profile_save" to mapOf(
            AppLanguage.INDONESIAN to "Simpan Perubahan",
            AppLanguage.ENGLISH to "Save Changes",
            AppLanguage.SPANISH to "Guardar Cambios",
            AppLanguage.CHINESE to "保存更改",
            AppLanguage.ARABIC to "حفظ التغييرات",
            AppLanguage.JAPANESE to "変更を保存"
        ),
        "profile_slot" to mapOf(
            AppLanguage.INDONESIAN to "Slot",
            AppLanguage.ENGLISH to "Slot",
            AppLanguage.SPANISH to "Ranura",
            AppLanguage.CHINESE to "槽位",
            AppLanguage.ARABIC to "الفتحة",
            AppLanguage.JAPANESE to "スロット"
        ),
        "profile_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Kelola akun & informasi pribadi",
            AppLanguage.ENGLISH to "Manage account & personal info",
            AppLanguage.SPANISH to "Gestiona tu cuenta e información",
            AppLanguage.CHINESE to "管理账户和个人信息",
            AppLanguage.ARABIC to "إدارة الحساب والمعلومات",
            AppLanguage.JAPANESE to "アカウントと個人情報を管理"
        ),
        "profile_tier_desc" to mapOf(
            AppLanguage.INDONESIAN to "Upgrade untuk slot miner & komisi lebih besar",
            AppLanguage.ENGLISH to "Upgrade for more slots & commission",
            AppLanguage.SPANISH to "Mejora para más ranuras y comisión",
            AppLanguage.CHINESE to "升级以获得更多槽位和佣金",
            AppLanguage.ARABIC to "ترقية للحصول على المزيد من الفتحات",
            AppLanguage.JAPANESE to "スロットと手数料をアップグレード"
        ),
        "profile_tier_max" to mapOf(
            AppLanguage.INDONESIAN to "MAX",
            AppLanguage.ENGLISH to "MAX",
            AppLanguage.SPANISH to "MÁX",
            AppLanguage.CHINESE to "最高",
            AppLanguage.ARABIC to "الأقصى",
            AppLanguage.JAPANESE to "最大"
        ),
        "profile_tier_max_msg" to mapOf(
            AppLanguage.INDONESIAN to "Tingkat tertinggi tercapai! 🎉",
            AppLanguage.ENGLISH to "Highest tier reached! 🎉",
            AppLanguage.SPANISH to "¡Nivel máximo alcanzado! 🎉",
            AppLanguage.CHINESE to "已达到最高等级！🎉",
            AppLanguage.ARABIC to "تم الوصول لأعلى مستوى! 🎉",
            AppLanguage.JAPANESE to "最高レベルに到達！🎉"
        ),
        "profile_tier_upgrade" to mapOf(
            AppLanguage.INDONESIAN to "Upgrade",
            AppLanguage.ENGLISH to "Upgrade",
            AppLanguage.SPANISH to "Mejorar",
            AppLanguage.CHINESE to "升级",
            AppLanguage.ARABIC to "ترقية",
            AppLanguage.JAPANESE to "アップグレード"
        ),
        "profile_title" to mapOf(
            AppLanguage.INDONESIAN to "Profil",
            AppLanguage.ENGLISH to "Profile",
            AppLanguage.SPANISH to "Perfil",
            AppLanguage.CHINESE to "个人资料",
            AppLanguage.ARABIC to "الملف الشخصي",
            AppLanguage.JAPANESE to "プロフィール"
        ),
        "profile_total" to mapOf(
            AppLanguage.INDONESIAN to "Total",
            AppLanguage.ENGLISH to "Total",
            AppLanguage.SPANISH to "Total",
            AppLanguage.CHINESE to "总计",
            AppLanguage.ARABIC to "الإجمالي",
            AppLanguage.JAPANESE to "合計"
        ),
        "profile_default_user" to mapOf(
            AppLanguage.INDONESIAN to "User",
            AppLanguage.ENGLISH to "User",
            AppLanguage.SPANISH to "Usuario",
            AppLanguage.CHINESE to "用户",
            AppLanguage.ARABIC to "مستخدم",
            AppLanguage.JAPANESE to "ユーザー"
        ),

        "profile_bonus" to mapOf(
            AppLanguage.INDONESIAN to "Bonus",
            AppLanguage.ENGLISH to "Bonus",
            AppLanguage.SPANISH to "Bono",
            AppLanguage.CHINESE to "奖励",
            AppLanguage.ARABIC to "مكافأة",
            AppLanguage.JAPANESE to "ボーナス"
        ),
                "referral_invited_friends" to mapOf(
            AppLanguage.INDONESIAN to "Teman yang Diundang",
            AppLanguage.ENGLISH to "Invited Friends",
            AppLanguage.SPANISH to "Amigos invitados",
            AppLanguage.CHINESE to "已邀请的好友",
            AppLanguage.ARABIC to "الأصدقاء المدعوون",
            AppLanguage.JAPANESE to "招待した友達"
        ),
        "faq_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Pertanyaan umum & dukungan",
            AppLanguage.ENGLISH to "Common questions & support",
            AppLanguage.SPANISH to "Preguntas frecuentes y soporte",
            AppLanguage.CHINESE to "常见问题和支持",
            AppLanguage.ARABIC to "الأسئلة الشائعة والدعم",
            AppLanguage.JAPANESE to "よくある質問とサポート"
        ),
        "faq_need_help" to mapOf(
            AppLanguage.INDONESIAN to "Masih butuh bantuan?",
            AppLanguage.ENGLISH to "Still need help?",
            AppLanguage.SPANISH to "¿Aún necesitas ayuda?",
            AppLanguage.CHINESE to "还需要帮助吗？",
            AppLanguage.ARABIC to "هل ما زلت بحاجة للمساعدة؟",
            AppLanguage.JAPANESE to "まだお困りですか？"
        ),
        "faq_contact_support" to mapOf(
            AppLanguage.INDONESIAN to "Hubungi tim support kami",
            AppLanguage.ENGLISH to "Contact our support team",
            AppLanguage.SPANISH to "Contacta a nuestro equipo",
            AppLanguage.CHINESE to "联系我们的支持团队",
            AppLanguage.ARABIC to "اتصل بفريق الدعم",
            AppLanguage.JAPANESE to "サポートチームにお問い合わせ"
        ),
        "leaderboard_title" to mapOf(
            AppLanguage.INDONESIAN to "Papan Peringkat",
            AppLanguage.ENGLISH to "Leaderboard",
            AppLanguage.SPANISH to "Clasificación",
            AppLanguage.CHINESE to "排行榜",
            AppLanguage.ARABIC to "لوحة المتصدرين",
            AppLanguage.JAPANESE to "リーダーボード"
        ),
        "leaderboard_empty" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada data peringkat",
            AppLanguage.ENGLISH to "No leaderboard data yet",
            AppLanguage.SPANISH to "Sin datos de clasificación",
            AppLanguage.CHINESE to "暂无排行榜数据",
            AppLanguage.ARABIC to "لا توجد بيانات الترتيب بعد",
            AppLanguage.JAPANESE to "まだリーダーボードデータがありません"
        ),
        "common_ref" to mapOf(
            AppLanguage.INDONESIAN to "Ref:",
            AppLanguage.ENGLISH to "Ref:",
            AppLanguage.SPANISH to "Ref:",
            AppLanguage.CHINESE to "参考:",
            AppLanguage.ARABIC to "المرجع:",
            AppLanguage.JAPANESE to "参照:"
        ),

        // ========== WITHDRAWAL SCREEN ==========
        "withdrawal_title" to mapOf(
            AppLanguage.INDONESIAN to "💰 Penarikan",
            AppLanguage.ENGLISH to "💰 Withdrawal",
            AppLanguage.SPANISH to "💰 Retiro",
            AppLanguage.CHINESE to "💰 提款",
            AppLanguage.ARABIC to "💰 السحب",
            AppLanguage.JAPANESE to "💰 引き出し"
        ),
        "withdrawal_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Tarik saldo ke rekening atau e-wallet kamu",
            AppLanguage.ENGLISH to "Withdraw balance to your bank account or e-wallet",
            AppLanguage.SPANISH to "Retira el saldo a tu cuenta bancaria o billetera electrónica",
            AppLanguage.CHINESE to "将余额提现到银行账户或电子钱包",
            AppLanguage.ARABIC to "اسحب الرصيد إلى حسابك البنكي أو المحفظة الإلكترونية",
            AppLanguage.JAPANESE to "残高を銀行口座またはEウォレットに引き出します"
        ),
        "withdrawal_available_balance" to mapOf(
            AppLanguage.INDONESIAN to "Saldo Tersedia",
            AppLanguage.ENGLISH to "Available Balance",
            AppLanguage.SPANISH to "Saldo Disponible",
            AppLanguage.CHINESE to "可用余额",
            AppLanguage.ARABIC to "الرصيد المتاح",
            AppLanguage.JAPANESE to "利用可能残高"
        ),
        "withdrawal_form_title" to mapOf(
            AppLanguage.INDONESIAN to "📝 Form Penarikan",
            AppLanguage.ENGLISH to "📝 Withdrawal Form",
            AppLanguage.SPANISH to "📝 Formulario de Retiro",
            AppLanguage.CHINESE to "📝 提款表格",
            AppLanguage.ARABIC to "📝 نموذج السحب",
            AppLanguage.JAPANESE to "📝 引き出しフォーム"
        ),
        "withdrawal_amount_label" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah (min Rp ",
            AppLanguage.ENGLISH to "Amount (min Rp ",
            AppLanguage.SPANISH to "Cantidad (mín Rp ",
            AppLanguage.CHINESE to "金额（最低 Rp ",
            AppLanguage.ARABIC to "المبلغ (الحد الأدنى Rp ",
            AppLanguage.JAPANESE to "金額（最低 Rp "
        ),
        "withdrawal_channel" to mapOf(
            AppLanguage.INDONESIAN to "Channel",
            AppLanguage.ENGLISH to "Channel",
            AppLanguage.SPANISH to "Canal",
            AppLanguage.CHINESE to "渠道",
            AppLanguage.ARABIC to "القناة",
            AppLanguage.JAPANESE to "チャネル"
        ),
        "withdrawal_ewallet_min" to mapOf(
            AppLanguage.INDONESIAN to "E-Wallet (min Rp 50.000)",
            AppLanguage.ENGLISH to "E-Wallet (min Rp 50,000)",
            AppLanguage.SPANISH to "E-Wallet (mín Rp 50.000)",
            AppLanguage.CHINESE to "电子钱包（最低 Rp 50,000）",
            AppLanguage.ARABIC to "المحفظة الإلكترونية (الحد الأدنى Rp 50,000)",
            AppLanguage.JAPANESE to "Eウォレット（最低 Rp 50,000）"
        ),
        "withdrawal_bank_min" to mapOf(
            AppLanguage.INDONESIAN to "Bank Transfer (min Rp 100.000)",
            AppLanguage.ENGLISH to "Bank Transfer (min Rp 100,000)",
            AppLanguage.SPANISH to "Transferencia Bancaria (mín Rp 100.000)",
            AppLanguage.CHINESE to "银行转账（最低 Rp 100,000）",
            AppLanguage.ARABIC to "التحويل البنكي (الحد الأدنى Rp 100,000)",
            AppLanguage.JAPANESE to "銀行振込（最低 Rp 100,000）"
        ),
        "withdrawal_provider" to mapOf(
            AppLanguage.INDONESIAN to "Provider",
            AppLanguage.ENGLISH to "Provider",
            AppLanguage.SPANISH to "Proveedor",
            AppLanguage.CHINESE to "提供商",
            AppLanguage.ARABIC to "المزود",
            AppLanguage.JAPANESE to "プロバイダー"
        ),
        "withdrawal_account_number" to mapOf(
            AppLanguage.INDONESIAN to "Nomor Rekening / HP",
            AppLanguage.ENGLISH to "Account / Phone Number",
            AppLanguage.SPANISH to "Número de Cuenta / Teléfono",
            AppLanguage.CHINESE to "账号 / 手机号",
            AppLanguage.ARABIC to "رقم الحساب / الهاتف",
            AppLanguage.JAPANESE to "口座番号 / 電話番号"
        ),
        "withdrawal_account_holder" to mapOf(
            AppLanguage.INDONESIAN to "Nama Pemilik Rekening",
            AppLanguage.ENGLISH to "Account Holder Name",
            AppLanguage.SPANISH to "Nombre del Titular",
            AppLanguage.CHINESE to "账户持有人姓名",
            AppLanguage.ARABIC to "اسم صاحب الحساب",
            AppLanguage.JAPANESE to "口座名義人"
        ),
        "withdrawal_submit" to mapOf(
            AppLanguage.INDONESIAN to "💸 AJUKAN PENARIKAN",
            AppLanguage.ENGLISH to "💸 SUBMIT WITHDRAWAL",
            AppLanguage.SPANISH to "💸 SOLICITAR RETIRO",
            AppLanguage.CHINESE to "💸 提交提款",
            AppLanguage.ARABIC to "💸 إرسال طلب السحب",
            AppLanguage.JAPANESE to "💸 引き出しを申請"
        ),
        "withdrawal_history" to mapOf(
            AppLanguage.INDONESIAN to "📜 Riwayat Penarikan",
            AppLanguage.ENGLISH to "📜 Withdrawal History",
            AppLanguage.SPANISH to "📜 Historial de Retiros",
            AppLanguage.CHINESE to "📜 提款历史",
            AppLanguage.ARABIC to "📜 سجل السحب",
            AppLanguage.JAPANESE to "📜 引き出し履歴"
        ),
        "withdrawal_confirm_title" to mapOf(
            AppLanguage.INDONESIAN to "Konfirmasi Penarikan",
            AppLanguage.ENGLISH to "Withdrawal Confirmation",
            AppLanguage.SPANISH to "Confirmación de Retiro",
            AppLanguage.CHINESE to "确认提款",
            AppLanguage.ARABIC to "تأكيد السحب",
            AppLanguage.JAPANESE to "引き出しの確認"
        ),
        "withdrawal_confirm_check" to mapOf(
            AppLanguage.INDONESIAN to "Pastikan data sudah benar:",
            AppLanguage.ENGLISH to "Please verify the data:",
            AppLanguage.SPANISH to "Verifica los datos:",
            AppLanguage.CHINESE to "请确认数据正确：",
            AppLanguage.ARABIC to "الرجاء التحقق من البيانات:",
            AppLanguage.JAPANESE to "データを確認してください:"
        ),
        "withdrawal_confirm_amount" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah: Rp ",
            AppLanguage.ENGLISH to "Amount: Rp ",
            AppLanguage.SPANISH to "Cantidad: Rp ",
            AppLanguage.CHINESE to "金额：Rp ",
            AppLanguage.ARABIC to "المبلغ: Rp ",
            AppLanguage.JAPANESE to "金額：Rp "
        ),
        "withdrawal_confirm_channel" to mapOf(
            AppLanguage.INDONESIAN to "Channel: ",
            AppLanguage.ENGLISH to "Channel: ",
            AppLanguage.SPANISH to "Canal: ",
            AppLanguage.CHINESE to "渠道：",
            AppLanguage.ARABIC to "القناة: ",
            AppLanguage.JAPANESE to "チャネル："
        ),
        "withdrawal_confirm_provider" to mapOf(
            AppLanguage.INDONESIAN to "Provider: ",
            AppLanguage.ENGLISH to "Provider: ",
            AppLanguage.SPANISH to "Proveedor: ",
            AppLanguage.CHINESE to "提供商：",
            AppLanguage.ARABIC to "المزود: ",
            AppLanguage.JAPANESE to "プロバイダー："
        ),
        "withdrawal_confirm_account" to mapOf(
            AppLanguage.INDONESIAN to "No. Rekening: ",
            AppLanguage.ENGLISH to "Account No.: ",
            AppLanguage.SPANISH to "No. de Cuenta: ",
            AppLanguage.CHINESE to "账号：",
            AppLanguage.ARABIC to "رقم الحساب: ",
            AppLanguage.JAPANESE to "口座番号："
        ),
        "withdrawal_confirm_holder" to mapOf(
            AppLanguage.INDONESIAN to "Nama Pemilik: ",
            AppLanguage.ENGLISH to "Holder Name: ",
            AppLanguage.SPANISH to "Titular: ",
            AppLanguage.CHINESE to "持有人姓名：",
            AppLanguage.ARABIC to "اسم صاحب الحساب: ",
            AppLanguage.JAPANESE to "名義人："
        ),
        "withdrawal_confirm_yes" to mapOf(
            AppLanguage.INDONESIAN to "Ya, Ajukan",
            AppLanguage.ENGLISH to "Yes, Submit",
            AppLanguage.SPANISH to "Sí, Enviar",
            AppLanguage.CHINESE to "是，提交",
            AppLanguage.ARABIC to "نعم، إرسال",
            AppLanguage.JAPANESE to "はい、申請"
        ),


        // ========== PROFILE SECURITY SCREEN ==========
        "profile_security_upgrade_btn" to mapOf(
            AppLanguage.INDONESIAN to "Upgrade",
            AppLanguage.ENGLISH to "Upgrade",
            AppLanguage.SPANISH to "Mejorar",
            AppLanguage.CHINESE to "升级",
            AppLanguage.ARABIC to "ترقية",
            AppLanguage.JAPANESE to "アップグレード"
        ),
        "profile_security_2fa_title" to mapOf(
            AppLanguage.INDONESIAN to "Autentikasi Dua Faktor (2FA)",
            AppLanguage.ENGLISH to "Two-Factor Authentication (2FA)",
            AppLanguage.SPANISH to "Autenticación de Dos Factores (2FA)",
            AppLanguage.CHINESE to "双重身份验证 (2FA)",
            AppLanguage.ARABIC to "المصادقة الثنائية (2FA)",
            AppLanguage.JAPANESE to "二要素認証 (2FA)"
        ),
        "profile_security_2fa_desc" to mapOf(
            AppLanguage.INDONESIAN to "Wajib untuk penarikan dana di atas batas minimum",
            AppLanguage.ENGLISH to "Required for withdrawals above minimum limit",
            AppLanguage.SPANISH to "Requerido para retiros superiores al límite mínimo",
            AppLanguage.CHINESE to "超过最低限额提款时必须",
            AppLanguage.ARABIC to "مطلوب للسحوبات التي تتجاوز الحد الأدنى",
            AppLanguage.JAPANESE to "最低限度を超える引き出しに必要"
        ),
        "profile_security_encryption_protocol" to mapOf(
            AppLanguage.INDONESIAN to "Protokol Enkripsi: ",
            AppLanguage.ENGLISH to "Encryption Protocol: ",
            AppLanguage.SPANISH to "Protocolo de Cifrado: ",
            AppLanguage.CHINESE to "加密协议：",
            AppLanguage.ARABIC to "بروتوكول التشفير: ",
            AppLanguage.JAPANESE to "暗号化プロトコル: "
        ),
        "profile_security_key_exchange" to mapOf(
            AppLanguage.INDONESIAN to "Kunci Pertukaran: ",
            AppLanguage.ENGLISH to "Key Exchange: ",
            AppLanguage.SPANISH to "Intercambio de Claves: ",
            AppLanguage.CHINESE to "密钥交换：",
            AppLanguage.ARABIC to "تبادل المفاتيح: ",
            AppLanguage.JAPANESE to "鍵交換: "
        ),
        "profile_security_location_title" to mapOf(
            AppLanguage.INDONESIAN to "Pelacakan Lokasi Provider (Pinpoint)",
            AppLanguage.ENGLISH to "Provider Location Tracking (Pinpoint)",
            AppLanguage.SPANISH to "Seguimiento de Ubicación del Proveedor (Pinpoint)",
            AppLanguage.CHINESE to "提供商位置跟踪（精确）",
            AppLanguage.ARABIC to "تتبع موقع المزود (دقيق)",
            AppLanguage.JAPANESE to "プロバイダー位置追跡（ピンポイント）"
        ),
        "profile_security_location_desc" to mapOf(
            AppLanguage.INDONESIAN to "Izinkan analitik pemetaan wilayah provider untuk mendeteksi persebaran pengguna",
            AppLanguage.ENGLISH to "Allow provider region mapping analytics to detect user distribution",
            AppLanguage.SPANISH to "Permitir análisis de mapeo regional para detectar distribución de usuarios",
            AppLanguage.CHINESE to "允许提供商区域映射分析以检测用户分布",
            AppLanguage.ARABIC to "السماح بتحليلات رسم خرائط المنطقة لاكتشاف توزيع المستخدمين",
            AppLanguage.JAPANESE to "ユーザー分布を検出するための地域マッピング分析を許可"
        ),
        "profile_security_region_detected" to mapOf(
            AppLanguage.INDONESIAN to "Posisi Wilayah Terdeteksi:",
            AppLanguage.ENGLISH to "Detected Region:",
            AppLanguage.SPANISH to "Región Detectada:",
            AppLanguage.CHINESE to "检测到的区域：",
            AppLanguage.ARABIC to "المنطقة المكتشفة:",
            AppLanguage.JAPANESE to "検出された地域:"
        ),
        "profile_security_default_city" to mapOf(
            AppLanguage.INDONESIAN to "Jakarta Pusat",
            AppLanguage.ENGLISH to "Central Jakarta",
            AppLanguage.SPANISH to "Yakarta Central",
            AppLanguage.CHINESE to "雅加达中部",
            AppLanguage.ARABIC to "جاكرتا الوسطى",
            AppLanguage.JAPANESE to "中央ジャカルタ"
        ),
        "profile_security_default_province" to mapOf(
            AppLanguage.INDONESIAN to "DKI Jakarta",
            AppLanguage.ENGLISH to "DKI Jakarta",
            AppLanguage.SPANISH to "DKI Yakarta",
            AppLanguage.CHINESE to "雅加达首都特区",
            AppLanguage.ARABIC to "جاكرتا",
            AppLanguage.JAPANESE to "ジャカルタ首都特別州"
        ),
        "profile_security_gps_coords" to mapOf(
            AppLanguage.INDONESIAN to "Koordinat GPS Presisi:",
            AppLanguage.ENGLISH to "Precise GPS Coordinates:",
            AppLanguage.SPANISH to "Coordenadas GPS Precisas:",
            AppLanguage.CHINESE to "精确 GPS 坐标：",
            AppLanguage.ARABIC to "إحداثيات GPS الدقيقة:",
            AppLanguage.JAPANESE to "正確な GPS 座標:"
        ),
        "profile_security_referral_title" to mapOf(
            AppLanguage.INDONESIAN to "Program Afiliasi Referral Bertingkat",
            AppLanguage.ENGLISH to "Multi-Tier Referral Affiliate Program",
            AppLanguage.SPANISH to "Programa de Afiliados Multinivel",
            AppLanguage.CHINESE to "多层推荐联盟计划",
            AppLanguage.ARABIC to "برنامج التسويق بالعمولة متعدد المستويات",
            AppLanguage.JAPANESE to "マルチティア紹介アフィリエイトプログラム"
        ),
        "profile_security_referral_desc" to mapOf(
            AppLanguage.INDONESIAN to "Ajak teman & peroleh komisi pasif 5% (Tier 1) dan 2% (Tier 2) selamanya.",
            AppLanguage.ENGLISH to "Invite friends & earn passive commission 5% (Tier 1) and 2% (Tier 2) forever.",
            AppLanguage.SPANISH to "Invita amigos y gana comisión pasiva 5% (Nivel 1) y 2% (Nivel 2) para siempre.",
            AppLanguage.CHINESE to "邀请好友并永久获得 5%（一级）和 2%（二级）被动佣金。",
            AppLanguage.ARABIC to "ادعُ الأصدقاء واربح عمولة سلبية 5% (المستوى 1) و2% (المستوى 2) للأبد.",
            AppLanguage.JAPANESE to "友達を招待して、5%（ティア1）と2%（ティア2）のパッシブコミッションを永久に獲得。"
        ),
        "profile_security_referral_code" to mapOf(
            AppLanguage.INDONESIAN to "Kode Referral Anda:",
            AppLanguage.ENGLISH to "Your Referral Code:",
            AppLanguage.SPANISH to "Tu Código de Referido:",
            AppLanguage.CHINESE to "您的推荐码：",
            AppLanguage.ARABIC to "رمز الإحالة الخاص بك:",
            AppLanguage.JAPANESE to "あなたの紹介コード:"
        ),
        "profile_security_referral_clip_label" to mapOf(
            AppLanguage.INDONESIAN to "Referral",
            AppLanguage.ENGLISH to "Referral",
            AppLanguage.SPANISH to "Referido",
            AppLanguage.CHINESE to "推荐",
            AppLanguage.ARABIC to "إحالة",
            AppLanguage.JAPANESE to "紹介"
        ),
        "profile_security_language_title" to mapOf(
            AppLanguage.INDONESIAN to "Bahasa Internasional & Zona Wilayah",
            AppLanguage.ENGLISH to "International Language & Region",
            AppLanguage.SPANISH to "Idioma Internacional y Región",
            AppLanguage.CHINESE to "国际语言和地区",
            AppLanguage.ARABIC to "اللغة الدولية والمنطقة",
            AppLanguage.JAPANESE to "国際言語と地域"
        ),
        "profile_security_language_desc" to mapOf(
            AppLanguage.INDONESIAN to "Pilihan: 6 Bahasa Internasional",
            AppLanguage.ENGLISH to "Options: 6 International Languages",
            AppLanguage.SPANISH to "Opciones: 6 Idiomas Internacionales",
            AppLanguage.CHINESE to "选项：6 种国际语言",
            AppLanguage.ARABIC to "الخيارات: 6 لغات دولية",
            AppLanguage.JAPANESE to "オプション：6つの国際言語"
        ),
        "profile_security_universal_title" to mapOf(
            AppLanguage.INDONESIAN to "Standar Nilai Poin Global (Universal Rate)",
            AppLanguage.ENGLISH to "Global Point Value Standard (Universal Rate)",
            AppLanguage.SPANISH to "Estándar de Valor de Puntos Global (Tarifa Universal)",
            AppLanguage.CHINESE to "全球积分价值标准（通用费率）",
            AppLanguage.ARABIC to "معيار قيمة النقاط العالمي (السعر العالمي)",
            AppLanguage.JAPANESE to "グローバルポイント価値基準（ユニバーサルレート）"
        ),
        "profile_security_universal_desc" to mapOf(
            AppLanguage.INDONESIAN to "Nilai beli poin seragam di seluruh dunia: 100 Pts = $1.00 USD / USDT",
            AppLanguage.ENGLISH to "Uniform point purchase value worldwide: 100 Pts = $1.00 USD / USDT",
            AppLanguage.SPANISH to "Valor de compra de puntos uniforme en todo el mundo: 100 Pts = $1.00 USD / USDT",
            AppLanguage.CHINESE to "全球统一积分购买价值：100 Pts = $1.00 USD / USDT",
            AppLanguage.ARABIC to "قيمة شراء النقاط الموحدة عالميًا: 100 نقطة = 1.00 دولار أمريكي / USDT",
            AppLanguage.JAPANESE to "世界中で統一されたポイント購入額: 100 Pts = $1.00 USD / USDT"
        ),
        "profile_security_ad_title" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan Sponsor Pengembang",
            AppLanguage.ENGLISH to "Watch Developer Sponsor Ad",
            AppLanguage.SPANISH to "Ver Anuncio de Patrocinador",
            AppLanguage.CHINESE to "观看开发者赞助广告",
            AppLanguage.ARABIC to "شاهد إعلان الراعي المطور",
            AppLanguage.JAPANESE to "開発者スポンサー広告を視聴"
        ),
        "profile_security_ad_desc" to mapOf(
            AppLanguage.INDONESIAN to "Dapatkan +50 Poin aktivitas setiap tontonan",
            AppLanguage.ENGLISH to "Get +50 Activity Points per view",
            AppLanguage.SPANISH to "Obtén +50 Puntos de Actividad por vista",
            AppLanguage.CHINESE to "每次观看可获得 +50 活动积分",
            AppLanguage.ARABIC to "احصل على +50 نقطة نشاط لكل مشاهدة",
            AppLanguage.JAPANESE to "視聴ごとに +50 アクティビティポイントを獲得"
        ),
        "profile_security_ad_reward" to mapOf(
            AppLanguage.INDONESIAN to "+50 Poin",
            AppLanguage.ENGLISH to "+50 Points",
            AppLanguage.SPANISH to "+50 Puntos",
            AppLanguage.CHINESE to "+50 积分",
            AppLanguage.ARABIC to "+50 نقطة",
            AppLanguage.JAPANESE to "+50 ポイント"
        ),
        "profile_security_info_title" to mapOf(
            AppLanguage.INDONESIAN to "Informasi Profil Pribadi",
            AppLanguage.ENGLISH to "Personal Profile Information",
            AppLanguage.SPANISH to "Información del Perfil Personal",
            AppLanguage.CHINESE to "个人资料信息",
            AppLanguage.ARABIC to "معلومات الملف الشخصي",
            AppLanguage.JAPANESE to "個人プロフィール情報"
        ),
        "profile_security_full_name" to mapOf(
            AppLanguage.INDONESIAN to "Nama Lengkap",
            AppLanguage.ENGLISH to "Full Name",
            AppLanguage.SPANISH to "Nombre Completo",
            AppLanguage.CHINESE to "全名",
            AppLanguage.ARABIC to "الاسم الكامل",
            AppLanguage.JAPANESE to "氏名"
        ),
        "profile_security_email" to mapOf(
            AppLanguage.INDONESIAN to "Alamat Email",
            AppLanguage.ENGLISH to "Email Address",
            AppLanguage.SPANISH to "Correo Electrónico",
            AppLanguage.CHINESE to "电子邮件地址",
            AppLanguage.ARABIC to "عنوان البريد الإلكتروني",
            AppLanguage.JAPANESE to "メールアドレス"
        ),
        "profile_security_phone" to mapOf(
            AppLanguage.INDONESIAN to "Nomor WhatsApp / HP",
            AppLanguage.ENGLISH to "WhatsApp / Phone Number",
            AppLanguage.SPANISH to "Número de WhatsApp / Teléfono",
            AppLanguage.CHINESE to "WhatsApp / 电话号码",
            AppLanguage.ARABIC to "رقم WhatsApp / الهاتف",
            AppLanguage.JAPANESE to "WhatsApp / 電話番号"
        ),
        "profile_security_save_btn" to mapOf(
            AppLanguage.INDONESIAN to "Simpan Perubahan Profil",
            AppLanguage.ENGLISH to "Save Profile Changes",
            AppLanguage.SPANISH to "Guardar Cambios del Perfil",
            AppLanguage.CHINESE to "保存个人资料更改",
            AppLanguage.ARABIC to "حفظ تغييرات الملف الشخصي",
            AppLanguage.JAPANESE to "プロフィールの変更を保存"
        ),


        // ========== INVESTMENT COUPON SCREEN ==========
        "investment_total_value" to mapOf(
            AppLanguage.INDONESIAN to "Total Nilai Investasi:",
            AppLanguage.ENGLISH to "Total Investment Value:",
            AppLanguage.SPANISH to "Valor Total de Inversión:",
            AppLanguage.CHINESE to "总投资价值：",
            AppLanguage.ARABIC to "إجمالي قيمة الاستثمار:",
            AppLanguage.JAPANESE to "総投資額:"
        ),
        "investment_dividend_return" to mapOf(
            AppLanguage.INDONESIAN to "Hasil Return Dividen:",
            AppLanguage.ENGLISH to "Dividend Return Yield:",
            AppLanguage.SPANISH to "Rendimiento de Dividendos:",
            AppLanguage.CHINESE to "股息回报：",
            AppLanguage.ARABIC to "عائد الأرباح:",
            AppLanguage.JAPANESE to "配当利回り:"
        ),
        "investment_tab_catalog" to mapOf(
            AppLanguage.INDONESIAN to "Katalog Proyek",
            AppLanguage.ENGLISH to "Project Catalog",
            AppLanguage.SPANISH to "Catálogo de Proyectos",
            AppLanguage.CHINESE to "项目目录",
            AppLanguage.ARABIC to "كتالوج المشاريع",
            AppLanguage.JAPANESE to "プロジェクトカタログ"
        ),
        "investment_tab_portfolio" to mapOf(
            AppLanguage.INDONESIAN to "Portofolio (",
            AppLanguage.ENGLISH to "Portfolio (",
            AppLanguage.SPANISH to "Portafolio (",
            AppLanguage.CHINESE to "投资组合（",
            AppLanguage.ARABIC to "المحفظة (",
            AppLanguage.JAPANESE to "ポートフォリオ ("
        ),
        "investment_tab_secondary" to mapOf(
            AppLanguage.INDONESIAN to "Pasar Sekunder (",
            AppLanguage.ENGLISH to "Secondary Market (",
            AppLanguage.SPANISH to "Mercado Secundario (",
            AppLanguage.CHINESE to "二级市场（",
            AppLanguage.ARABIC to "السوق الثانوي (",
            AppLanguage.JAPANESE to "セカンダリ市場 ("
        ),
        "investment_empty_coupon_title" to mapOf(
            AppLanguage.INDONESIAN to "Belum Ada Kupon Investasi",
            AppLanguage.ENGLISH to "No Investment Coupons Yet",
            AppLanguage.SPANISH to "Aún No Hay Cupones de Inversión",
            AppLanguage.CHINESE to "尚无投资优惠券",
            AppLanguage.ARABIC to "لا توجد قسائم استثمار بعد",
            AppLanguage.JAPANESE to "投資クーポンはまだありません"
        ),
        "investment_empty_coupon_desc" to mapOf(
            AppLanguage.INDONESIAN to "Pilih kupon di Katalog untuk mulai mendiversifikasi saldo komisi Anda.",
            AppLanguage.ENGLISH to "Pick a coupon in the Catalog to start diversifying your commission balance.",
            AppLanguage.SPANISH to "Elige un cupón en el Catálogo para diversificar tu saldo de comisiones.",
            AppLanguage.CHINESE to "在目录中选择优惠券以开始分散您的佣金余额。",
            AppLanguage.ARABIC to "اختر قسيمة من الكتالوج لبدء تنويع رصيد عمولتك.",
            AppLanguage.JAPANESE to "カタログでクーポンを選んで、手数料残高の分散を始めましょう。"
        ),
        "investment_empty_secondary_desc" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada kupon yang dipasang di pasar sekunder saat ini.",
            AppLanguage.ENGLISH to "No coupons listed on the secondary market yet.",
            AppLanguage.SPANISH to "Aún no hay cupones en el mercado secundario.",
            AppLanguage.CHINESE to "目前二级市场上没有优惠券。",
            AppLanguage.ARABIC to "لا توجد قسائم مدرجة في السوق الثانوي حتى الآن.",
            AppLanguage.JAPANESE to "現在、セカンダリ市場に出品されたクーポンはありません。"
        ),
        "investment_coupon_risk" to mapOf(
            AppLanguage.INDONESIAN to "Risiko: ",
            AppLanguage.ENGLISH to "Risk: ",
            AppLanguage.SPANISH to "Riesgo: ",
            AppLanguage.CHINESE to "风险：",
            AppLanguage.ARABIC to "المخاطر: ",
            AppLanguage.JAPANESE to "リスク: "
        ),
        "investment_coupon_unit_price" to mapOf(
            AppLanguage.INDONESIAN to "Harga Unit:",
            AppLanguage.ENGLISH to "Unit Price:",
            AppLanguage.SPANISH to "Precio Unitario:",
            AppLanguage.CHINESE to "单价：",
            AppLanguage.ARABIC to "سعر الوحدة:",
            AppLanguage.JAPANESE to "単価:"
        ),
        "investment_coupon_tenor" to mapOf(
            AppLanguage.INDONESIAN to "Tenor:",
            AppLanguage.ENGLISH to "Tenor:",
            AppLanguage.SPANISH to "Plazo:",
            AppLanguage.CHINESE to "期限：",
            AppLanguage.ARABIC to "المدة:",
            AppLanguage.JAPANESE to "期間:"
        ),
        "investment_coupon_days_suffix" to mapOf(
            AppLanguage.INDONESIAN to " Hari",
            AppLanguage.ENGLISH to " Days",
            AppLanguage.SPANISH to " Días",
            AppLanguage.CHINESE to " 天",
            AppLanguage.ARABIC to " يوم",
            AppLanguage.JAPANESE to " 日"
        ),
        "investment_coupon_available" to mapOf(
            AppLanguage.INDONESIAN to "Tersedia:",
            AppLanguage.ENGLISH to "Available:",
            AppLanguage.SPANISH to "Disponible:",
            AppLanguage.CHINESE to "可用：",
            AppLanguage.ARABIC to "متاح:",
            AppLanguage.JAPANESE to "利用可能:"
        ),
        "investment_coupon_units_suffix" to mapOf(
            AppLanguage.INDONESIAN to " Kupon",
            AppLanguage.ENGLISH to " Coupons",
            AppLanguage.SPANISH to " Cupones",
            AppLanguage.CHINESE to " 张",
            AppLanguage.ARABIC to " قسيمة",
            AppLanguage.JAPANESE to " クーポン"
        ),
        "investment_coupon_buy_btn" to mapOf(
            AppLanguage.INDONESIAN to "Beli Kupon Investasi",
            AppLanguage.ENGLISH to "Buy Investment Coupon",
            AppLanguage.SPANISH to "Comprar Cupón de Inversión",
            AppLanguage.CHINESE to "购买投资优惠券",
            AppLanguage.ARABIC to "شراء قسيمة استثمار",
            AppLanguage.JAPANESE to "投資クーポンを購入"
        ),
        "investment_portfolio_ownership" to mapOf(
            AppLanguage.INDONESIAN to "Kepemilikan: ",
            AppLanguage.ENGLISH to "Ownership: ",
            AppLanguage.SPANISH to "Propiedad: ",
            AppLanguage.CHINESE to "持有：",
            AppLanguage.ARABIC to "الملكية: ",
            AppLanguage.JAPANESE to "所有:"
        ),
        "investment_portfolio_units_suffix" to mapOf(
            AppLanguage.INDONESIAN to " Unit Kupon",
            AppLanguage.ENGLISH to " Coupon Units",
            AppLanguage.SPANISH to " Unidades de Cupón",
            AppLanguage.CHINESE to " 优惠券单位",
            AppLanguage.ARABIC to " وحدة قسيمة",
            AppLanguage.JAPANESE to " クーポン単位"
        ),
        "investment_portfolio_listed" to mapOf(
            AppLanguage.INDONESIAN to "DIPASANG JUAL",
            AppLanguage.ENGLISH to "LISTED FOR SALE",
            AppLanguage.SPANISH to "EN VENTA",
            AppLanguage.CHINESE to "已上市出售",
            AppLanguage.ARABIC to "معروض للبيع",
            AppLanguage.JAPANESE to "売り出し中"
        ),
        "investment_portfolio_total" to mapOf(
            AppLanguage.INDONESIAN to "Total Nilai:",
            AppLanguage.ENGLISH to "Total Value:",
            AppLanguage.SPANISH to "Valor Total:",
            AppLanguage.CHINESE to "总价值：",
            AppLanguage.ARABIC to "القيمة الإجمالية:",
            AppLanguage.JAPANESE to "総額:"
        ),
        "investment_portfolio_dividend" to mapOf(
            AppLanguage.INDONESIAN to "Dividen Berjalan:",
            AppLanguage.ENGLISH to "Running Dividend:",
            AppLanguage.SPANISH to "Dividendo en Curso:",
            AppLanguage.CHINESE to "累计股息：",
            AppLanguage.ARABIC to "الأرباح الجارية:",
            AppLanguage.JAPANESE to "配当進行中:"
        ),
        "investment_portfolio_claim_yield" to mapOf(
            AppLanguage.INDONESIAN to "Klaim Yield",
            AppLanguage.ENGLISH to "Claim Yield",
            AppLanguage.SPANISH to "Reclamar Rendimiento",
            AppLanguage.CHINESE to "领取收益",
            AppLanguage.ARABIC to "المطالبة بالعائد",
            AppLanguage.JAPANESE to "利回りを請求"
        ),
        "investment_secondary_apy" to mapOf(
            AppLanguage.INDONESIAN to "Return APY: ",
            AppLanguage.ENGLISH to "Return APY: ",
            AppLanguage.SPANISH to "Retorno APY: ",
            AppLanguage.CHINESE to "年化收益率：",
            AppLanguage.ARABIC to "عائد APY: ",
            AppLanguage.JAPANESE to "APYリターン: "
        ),
        "investment_secondary_apy_suffix" to mapOf(
            AppLanguage.INDONESIAN to "% p.a. • Sisa: ",
            AppLanguage.ENGLISH to "% p.a. • Remaining: ",
            AppLanguage.SPANISH to "% anual • Restante: ",
            AppLanguage.CHINESE to "% 年化 • 剩余：",
            AppLanguage.ARABIC to "% سنويًا • المتبقي: ",
            AppLanguage.JAPANESE to "% p.a. • 残り: "
        ),
        "investment_secondary_badge" to mapOf(
            AppLanguage.INDONESIAN to "SEKUNDER",
            AppLanguage.ENGLISH to "SECONDARY",
            AppLanguage.SPANISH to "SECUNDARIO",
            AppLanguage.CHINESE to "二级",
            AppLanguage.ARABIC to "ثانوي",
            AppLanguage.JAPANESE to "セカンダリ"
        ),
        "investment_secondary_sell_price" to mapOf(
            AppLanguage.INDONESIAN to "Harga Jual:",
            AppLanguage.ENGLISH to "Selling Price:",
            AppLanguage.SPANISH to "Precio de Venta:",
            AppLanguage.CHINESE to "售价：",
            AppLanguage.ARABIC to "سعر البيع:",
            AppLanguage.JAPANESE to "販売価格:"
        ),
        "investment_secondary_buy_now" to mapOf(
            AppLanguage.INDONESIAN to "Beli Sekarang",
            AppLanguage.ENGLISH to "Buy Now",
            AppLanguage.SPANISH to "Comprar Ahora",
            AppLanguage.CHINESE to "立即购买",
            AppLanguage.ARABIC to "اشتر الآن",
            AppLanguage.JAPANESE to "今すぐ購入"
        ),


        // ========== CAMPAIGNS SCREEN ==========
        "campaigns_create_link_btn" to mapOf(
            AppLanguage.INDONESIAN to "Buat Link",
            AppLanguage.ENGLISH to "Create Link",
            AppLanguage.SPANISH to "Crear Enlace",
            AppLanguage.CHINESE to "创建链接",
            AppLanguage.ARABIC to "إنشاء رابط",
            AppLanguage.JAPANESE to "リンク作成"
        ),
        "campaigns_tab_catalog" to mapOf(
            AppLanguage.INDONESIAN to "Katalog Kampanye (",
            AppLanguage.ENGLISH to "Campaign Catalog (",
            AppLanguage.SPANISH to "Catálogo de Campañas (",
            AppLanguage.CHINESE to "活动目录（",
            AppLanguage.ARABIC to "كتالوج الحملات (",
            AppLanguage.JAPANESE to "キャンペーンカタログ ("
        ),
        "campaigns_tab_my_links" to mapOf(
            AppLanguage.INDONESIAN to "Link Aktif Saya (",
            AppLanguage.ENGLISH to "My Active Links (",
            AppLanguage.SPANISH to "Mis Enlaces Activos (",
            AppLanguage.CHINESE to "我的活跃链接（",
            AppLanguage.ARABIC to "روابطي النشطة (",
            AppLanguage.JAPANESE to "自分の有効リンク ("
        ),
        "campaigns_empty_title" to mapOf(
            AppLanguage.INDONESIAN to "Belum Ada Tautan Promosi",
            AppLanguage.ENGLISH to "No Promotion Links Yet",
            AppLanguage.SPANISH to "Aún No Hay Enlaces de Promoción",
            AppLanguage.CHINESE to "尚无推广链接",
            AppLanguage.ARABIC to "لا توجد روابط ترويج بعد",
            AppLanguage.JAPANESE to "プロモーションリンクはまだありません"
        ),
        "campaigns_empty_desc" to mapOf(
            AppLanguage.INDONESIAN to "Pilih kampanye di atas untuk mulai membuat tautan promosi pertama Anda.",
            AppLanguage.ENGLISH to "Pick a campaign above to create your first promotion link.",
            AppLanguage.SPANISH to "Elige una campaña arriba para crear tu primer enlace de promoción.",
            AppLanguage.CHINESE to "在上方选择一个活动来创建您的第一个推广链接。",
            AppLanguage.ARABIC to "اختر حملة أعلاه لإنشاء أول رابط ترويج لك.",
            AppLanguage.JAPANESE to "上のキャンペーンを選んで最初のプロモーションリンクを作成しましょう。"
        ),
        "campaigns_create_now_btn" to mapOf(
            AppLanguage.INDONESIAN to "Buat Tautan Sekarang",
            AppLanguage.ENGLISH to "Create Link Now",
            AppLanguage.SPANISH to "Crear Enlace Ahora",
            AppLanguage.CHINESE to "立即创建链接",
            AppLanguage.ARABIC to "إنشاء رابط الآن",
            AppLanguage.JAPANESE to "今すぐリンクを作成"
        ),
        "campaigns_clip_label" to mapOf(
            AppLanguage.INDONESIAN to "Link",
            AppLanguage.ENGLISH to "Link",
            AppLanguage.SPANISH to "Enlace",
            AppLanguage.CHINESE to "链接",
            AppLanguage.ARABIC to "رابط",
            AppLanguage.JAPANESE to "リンク"
        ),
        "campaigns_high_ticket" to mapOf(
            AppLanguage.INDONESIAN to "HIGH TICKET",
            AppLanguage.ENGLISH to "HIGH TICKET",
            AppLanguage.SPANISH to "ALTO VALOR",
            AppLanguage.CHINESE to "高价值",
            AppLanguage.ARABIC to "قيمة عالية",
            AppLanguage.JAPANESE to "ハイチケット"
        ),
        "campaigns_commission_amount" to mapOf(
            AppLanguage.INDONESIAN to "Besaran Komisi:",
            AppLanguage.ENGLISH to "Commission Amount:",
            AppLanguage.SPANISH to "Monto de Comisión:",
            AppLanguage.CHINESE to "佣金金额：",
            AppLanguage.ARABIC to "مبلغ العمولة:",
            AppLanguage.JAPANESE to "コミッション額:"
        ),
        "campaigns_global_conversion" to mapOf(
            AppLanguage.INDONESIAN to "Konversi Global:",
            AppLanguage.ENGLISH to "Global Conversions:",
            AppLanguage.SPANISH to "Conversiones Globales:",
            AppLanguage.CHINESE to "全球转化：",
            AppLanguage.ARABIC to "التحويلات العالمية:",
            AppLanguage.JAPANESE to "グローバルコンバージョン:"
        ),
        "campaigns_transactions_suffix" to mapOf(
            AppLanguage.INDONESIAN to " Transaksi",
            AppLanguage.ENGLISH to " Transactions",
            AppLanguage.SPANISH to " Transacciones",
            AppLanguage.CHINESE to " 交易",
            AppLanguage.ARABIC to " معاملة",
            AppLanguage.JAPANESE to " 取引"
        ),
        "campaigns_create_tracking_btn" to mapOf(
            AppLanguage.INDONESIAN to "Buat Link Tracking",
            AppLanguage.ENGLISH to "Create Tracking Link",
            AppLanguage.SPANISH to "Crear Enlace de Seguimiento",
            AppLanguage.CHINESE to "创建跟踪链接",
            AppLanguage.ARABIC to "إنشاء رابط التتبع",
            AppLanguage.JAPANESE to "トラッキングリンクを作成"
        ),
        "campaigns_stat_clicks" to mapOf(
            AppLanguage.INDONESIAN to "Klik",
            AppLanguage.ENGLISH to "Clicks",
            AppLanguage.SPANISH to "Clics",
            AppLanguage.CHINESE to "点击",
            AppLanguage.ARABIC to "نقرات",
            AppLanguage.JAPANESE to "クリック"
        ),
        "campaigns_stat_conversions" to mapOf(
            AppLanguage.INDONESIAN to "Konversi",
            AppLanguage.ENGLISH to "Conversions",
            AppLanguage.SPANISH to "Conversiones",
            AppLanguage.CHINESE to "转化",
            AppLanguage.ARABIC to "تحويلات",
            AppLanguage.JAPANESE to "コンバージョン"
        ),
        "campaigns_stat_cr" to mapOf(
            AppLanguage.INDONESIAN to "CR %",
            AppLanguage.ENGLISH to "CR %",
            AppLanguage.SPANISH to "CR %",
            AppLanguage.CHINESE to "转化率 %",
            AppLanguage.ARABIC to "CR %",
            AppLanguage.JAPANESE to "CR %"
        ),
        "campaigns_stat_epc" to mapOf(
            AppLanguage.INDONESIAN to "EPC",
            AppLanguage.ENGLISH to "EPC",
            AppLanguage.SPANISH to "EPC",
            AppLanguage.CHINESE to "EPC",
            AppLanguage.ARABIC to "EPC",
            AppLanguage.JAPANESE to "EPC"
        ),
        "campaigns_test_conversion" to mapOf(
            AppLanguage.INDONESIAN to "Uji Konversi",
            AppLanguage.ENGLISH to "Test Conversion",
            AppLanguage.SPANISH to "Probar Conversión",
            AppLanguage.CHINESE to "测试转化",
            AppLanguage.ARABIC to "اختبار التحويل",
            AppLanguage.JAPANESE to "コンバージョンをテスト"
        ),


        // ========== GAME ROOM SCREEN ==========
        "game_room_processing" to mapOf(
            AppLanguage.INDONESIAN to "Memproses...",
            AppLanguage.ENGLISH to "Processing...",
            AppLanguage.SPANISH to "Procesando...",
            AppLanguage.CHINESE to "处理中...",
            AppLanguage.ARABIC to "جاري المعالجة...",
            AppLanguage.JAPANESE to "処理中..."
        ),
        "game_room_ok_understood" to mapOf(
            AppLanguage.INDONESIAN to "OK, Mengerti",
            AppLanguage.ENGLISH to "OK, Got It",
            AppLanguage.SPANISH to "OK, Entendido",
            AppLanguage.CHINESE to "好的，明白了",
            AppLanguage.ARABIC to "حسنًا، فهمت",
            AppLanguage.JAPANESE to "OK、了解しました"
        ),


        // ========== BATCH SMALL FILES ==========
        "sweepstakes_my_coupons" to mapOf(
            AppLanguage.INDONESIAN to "Kupon Anda",
            AppLanguage.ENGLISH to "Your Coupons",
            AppLanguage.SPANISH to "Tus Cupones",
            AppLanguage.CHINESE to "您的优惠券",
            AppLanguage.ARABIC to "قسائمك",
            AppLanguage.JAPANESE to "あなたのクーポン"
        ),
        "sweepstakes_no_winner" to mapOf(
            AppLanguage.INDONESIAN to "Belum ada pemenang",
            AppLanguage.ENGLISH to "No winners yet",
            AppLanguage.SPANISH to "Aún no hay ganadores",
            AppLanguage.CHINESE to "尚无中奖者",
            AppLanguage.ARABIC to "لا يوجد فائزون بعد",
            AppLanguage.JAPANESE to "まだ当選者はいません"
        ),
        "sweepstakes_buy_coupon" to mapOf(
            AppLanguage.INDONESIAN to "Beli Kupon",
            AppLanguage.ENGLISH to "Buy Coupon",
            AppLanguage.SPANISH to "Comprar Cupón",
            AppLanguage.CHINESE to "购买优惠券",
            AppLanguage.ARABIC to "شراء قسيمة",
            AppLanguage.JAPANESE to "クーポンを購入"
        ),
        "sweepstakes_buy_btn" to mapOf(
            AppLanguage.INDONESIAN to "BELI",
            AppLanguage.ENGLISH to "BUY",
            AppLanguage.SPANISH to "COMPRAR",
            AppLanguage.CHINESE to "购买",
            AppLanguage.ARABIC to "شراء",
            AppLanguage.JAPANESE to "購入"
        ),
        "transfersimple_min_amount" to mapOf(
            AppLanguage.INDONESIAN to "Min. 20 RTP",
            AppLanguage.ENGLISH to "Min. 20 RTP",
            AppLanguage.SPANISH to "Mín. 20 RTP",
            AppLanguage.CHINESE to "最低 20 RTP",
            AppLanguage.ARABIC to "الحد الأدنى 20 RTP",
            AppLanguage.JAPANESE to "最低 20 RTP"
        ),
        "transfersimple_success" to mapOf(
            AppLanguage.INDONESIAN to "Transfer Berhasil!",
            AppLanguage.ENGLISH to "Transfer Successful!",
            AppLanguage.SPANISH to "¡Transferencia Exitosa!",
            AppLanguage.CHINESE to "转账成功！",
            AppLanguage.ARABIC to "تم التحويل بنجاح!",
            AppLanguage.JAPANESE to "送金成功！"
        ),
        "referral_steps" to mapOf(
            AppLanguage.INDONESIAN to "1. Bagikan kode referral ke teman\n2. Teman daftar pakai kode kamu\n3. Kamu dapat komisi 5%\n4. Komisi masuk otomatis ke saldo",
            AppLanguage.ENGLISH to "1. Share referral code with friends\n2. Friends register with your code\n3. You get 5% commission\n4. Commission is automatically credited to balance",
            AppLanguage.SPANISH to "1. Comparte el código de referido con amigos\n2. Amigos se registran con tu código\n3. Recibes 5% de comisión\n4. La comisión se acredita automáticamente",
            AppLanguage.CHINESE to "1. 与朋友分享推荐码\n2. 朋友使用您的代码注册\n3. 您获得 5% 佣金\n4. 佣金自动计入余额",
            AppLanguage.ARABIC to "1. شارك رمز الإحالة مع الأصدقاء\n2. يسجل الأصدقاء برمزك\n3. تحصل على عمولة 5%\n4. تُضاف العمولة تلقائيًا إلى الرصيد",
            AppLanguage.JAPANESE to "1. 友達に紹介コードを共有\n2. 友達があなたのコードで登録\n3. 5% のコミッションを獲得\n4. コミッションは自動的に残高に加算"
        ),
        "analytics_geo_distribution" to mapOf(
            AppLanguage.INDONESIAN to "Distribusi Geografis Pengunjung",
            AppLanguage.ENGLISH to "Visitor Geographic Distribution",
            AppLanguage.SPANISH to "Distribución Geográfica de Visitantes",
            AppLanguage.CHINESE to "访客地理分布",
            AppLanguage.ARABIC to "التوزيع الجغرافي للزوار",
            AppLanguage.JAPANESE to "訪問者の地理的分布"
        ),
        "purchase_upgrade_tier" to mapOf(
            AppLanguage.INDONESIAN to "👑 Upgrade Tier",
            AppLanguage.ENGLISH to "👑 Upgrade Tier",
            AppLanguage.SPANISH to "👑 Mejorar Nivel",
            AppLanguage.CHINESE to "👑 升级等级",
            AppLanguage.ARABIC to "👑 ترقية المستوى",
            AppLanguage.JAPANESE to "👑 ティアアップグレード"
        ),
        "appoffers_downloading" to mapOf(
            AppLanguage.INDONESIAN to "Mengunduh & Memverifikasi...",
            AppLanguage.ENGLISH to "Downloading & Verifying...",
            AppLanguage.SPANISH to "Descargando y Verificando...",
            AppLanguage.CHINESE to "下载并验证中...",
            AppLanguage.ARABIC to "جاري التحميل والتحقق...",
            AppLanguage.JAPANESE to "ダウンロードと確認中..."
        ),


        // ========== QR TRANSFER MODAL ==========
        "qr_send_scan" to mapOf(
            AppLanguage.INDONESIAN to "Kirim RTP (Scan)",
            AppLanguage.ENGLISH to "Send RTP (Scan)",
            AppLanguage.SPANISH to "Enviar RTP (Escanear)",
            AppLanguage.CHINESE to "发送 RTP（扫描）",
            AppLanguage.ARABIC to "إرسال RTP (مسح)",
            AppLanguage.JAPANESE to "RTP送信（スキャン）"
        ),
        "qr_receive_my_qr" to mapOf(
            AppLanguage.INDONESIAN to "Terima (QR Saya)",
            AppLanguage.ENGLISH to "Receive (My QR)",
            AppLanguage.SPANISH to "Recibir (Mi QR)",
            AppLanguage.CHINESE to "接收（我的 QR）",
            AppLanguage.ARABIC to "استلام (QR الخاص بي)",
            AppLanguage.JAPANESE to "受信（マイQR）"
        ),
        "qr_open_scanner" to mapOf(
            AppLanguage.INDONESIAN to "Buka Scanner",
            AppLanguage.ENGLISH to "Open Scanner",
            AppLanguage.SPANISH to "Abrir Escáner",
            AppLanguage.CHINESE to "打开扫描仪",
            AppLanguage.ARABIC to "فتح الماسح",
            AppLanguage.JAPANESE to "スキャナーを開く"
        ),
        "qr_search_placeholder" to mapOf(
            AppLanguage.INDONESIAN to "Cari kontak, @username, atau kode referral...",
            AppLanguage.ENGLISH to "Search contacts, @username, or referral code...",
            AppLanguage.SPANISH to "Buscar contactos, @username o código de referido...",
            AppLanguage.CHINESE to "搜索联系人、@用户名或推荐码...",
            AppLanguage.ARABIC to "ابحث عن جهات الاتصال أو @username أو رمز الإحالة...",
            AppLanguage.JAPANESE to "連絡先、@ユーザー名、紹介コードを検索..."
        ),
        "qr_recipient_label" to mapOf(
            AppLanguage.INDONESIAN to "ID Pengguna, Kode Referral, atau Hasil Scan QR",
            AppLanguage.ENGLISH to "User ID, Referral Code, or QR Scan Result",
            AppLanguage.SPANISH to "ID de Usuario, Código de Referido o Resultado de Escaneo QR",
            AppLanguage.CHINESE to "用户 ID、推荐码或 QR 扫描结果",
            AppLanguage.ARABIC to "معرف المستخدم أو رمز الإحالة أو نتيجة مسح QR",
            AppLanguage.JAPANESE to "ユーザーID、紹介コード、QRスキャン結果"
        ),
        "qr_recipient_hint" to mapOf(
            AppLanguage.INDONESIAN to "Contoh: USR-8821 atau PRO8892",
            AppLanguage.ENGLISH to "Example: USR-8821 or PRO8892",
            AppLanguage.SPANISH to "Ejemplo: USR-8821 o PRO8892",
            AppLanguage.CHINESE to "示例：USR-8821 或 PRO8892",
            AppLanguage.ARABIC to "مثال: USR-8821 أو PRO8892",
            AppLanguage.JAPANESE to "例: USR-8821 または PRO8892"
        ),
        "qr_selected" to mapOf(
            AppLanguage.INDONESIAN to "Terpilih",
            AppLanguage.ENGLISH to "Selected",
            AppLanguage.SPANISH to "Seleccionado",
            AppLanguage.CHINESE to "已选择",
            AppLanguage.ARABIC to "محدد",
            AppLanguage.JAPANESE to "選択中"
        ),
        "qr_amount_title" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Transfer (RTP):",
            AppLanguage.ENGLISH to "Transfer Amount (RTP):",
            AppLanguage.SPANISH to "Monto de Transferencia (RTP):",
            AppLanguage.CHINESE to "转账金额（RTP）：",
            AppLanguage.ARABIC to "مبلغ التحويل (RTP):",
            AppLanguage.JAPANESE to "送金金額 (RTP):"
        ),
        "qr_note_label" to mapOf(
            AppLanguage.INDONESIAN to "Catatan Transfer (Opsional)",
            AppLanguage.ENGLISH to "Transfer Note (Optional)",
            AppLanguage.SPANISH to "Nota de Transferencia (Opcional)",
            AppLanguage.CHINESE to "转账备注（可选）",
            AppLanguage.ARABIC to "ملاحظة التحويل (اختياري)",
            AppLanguage.JAPANESE to "送金メモ（オプション）"
        ),
        "qr_note_hint" to mapOf(
            AppLanguage.INDONESIAN to "Contoh: Bagi reward campaign, bonus referral, dll.",
            AppLanguage.ENGLISH to "Example: Split campaign reward, referral bonus, etc.",
            AppLanguage.SPANISH to "Ejemplo: Compartir recompensa de campaña, bono de referido, etc.",
            AppLanguage.CHINESE to "示例：分享活动奖励、推荐奖金等。",
            AppLanguage.ARABIC to "مثال: مشاركة مكافأة الحملة، مكافأة الإحالة، إلخ.",
            AppLanguage.JAPANESE to "例: キャンペーン報酬の共有、紹介ボーナスなど"
        ),
        "qr_fee_terms_title" to mapOf(
            AppLanguage.INDONESIAN to "Ketentuan & Biaya Admin Transfer",
            AppLanguage.ENGLISH to "Transfer Terms & Admin Fee",
            AppLanguage.SPANISH to "Términos de Transferencia y Tarifa Administrativa",
            AppLanguage.CHINESE to "转账条款和管理费",
            AppLanguage.ARABIC to "شروط التحويل ورسوم الإدارة",
            AppLanguage.JAPANESE to "送金条件と管理手数料"
        ),
        "qr_fee_sent" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Ditransfer:",
            AppLanguage.ENGLISH to "Amount Sent:",
            AppLanguage.SPANISH to "Monto Enviado:",
            AppLanguage.CHINESE to "已发送金额：",
            AppLanguage.ARABIC to "المبلغ المرسل:",
            AppLanguage.JAPANESE to "送金金額:"
        ),
        "qr_fee_admin" to mapOf(
            AppLanguage.INDONESIAN to "Potongan Biaya Admin (Fixed):",
            AppLanguage.ENGLISH to "Admin Fee Deduction (Fixed):",
            AppLanguage.SPANISH to "Deducción de Tarifa Administrativa (Fija):",
            AppLanguage.CHINESE to "管理费扣除（固定）：",
            AppLanguage.ARABIC to "خصم رسوم الإدارة (ثابت):",
            AppLanguage.JAPANESE to "管理手数料控除（固定）:"
        ),
        "qr_fee_net_received" to mapOf(
            AppLanguage.INDONESIAN to "Bersih Diterima Penerima:",
            AppLanguage.ENGLISH to "Net Received by Recipient:",
            AppLanguage.SPANISH to "Neto Recibido por el Destinatario:",
            AppLanguage.CHINESE to "收款人实收净额：",
            AppLanguage.ARABIC to "الصافي المستلم من قبل المستلم:",
            AppLanguage.JAPANESE to "受取人の純受取額:"
        ),
        "qr_bill_amount_label" to mapOf(
            AppLanguage.INDONESIAN to "Atur Nominal Tagihan / Split Bill (Opsional)",
            AppLanguage.ENGLISH to "Set Bill / Split Bill Amount (Optional)",
            AppLanguage.SPANISH to "Establecer Monto de Factura / Split Bill (Opcional)",
            AppLanguage.CHINESE to "设置账单/分摊金额（可选）",
            AppLanguage.ARABIC to "تعيين مبلغ الفاتورة / تقسيم الفاتورة (اختياري)",
            AppLanguage.JAPANESE to "請求額/割り勘金額を設定（オプション）"
        ),
        "qr_bill_amount_hint" to mapOf(
            AppLanguage.INDONESIAN to "Contoh: 100",
            AppLanguage.ENGLISH to "Example: 100",
            AppLanguage.SPANISH to "Ejemplo: 100",
            AppLanguage.CHINESE to "示例：100",
            AppLanguage.ARABIC to "مثال: 100",
            AppLanguage.JAPANESE to "例: 100"
        ),
        "qr_bill_note_label" to mapOf(
            AppLanguage.INDONESIAN to "Catatan Tagihan / Split Bill (Opsional)",
            AppLanguage.ENGLISH to "Bill / Split Bill Note (Optional)",
            AppLanguage.SPANISH to "Nota de Factura / Split Bill (Opcional)",
            AppLanguage.CHINESE to "账单/分摊备注（可选）",
            AppLanguage.ARABIC to "ملاحظة الفاتورة / تقسيم الفاتورة (اختياري)",
            AppLanguage.JAPANESE to "請求書/割り勘メモ（オプション）"
        ),
        "qr_bill_note_hint" to mapOf(
            AppLanguage.INDONESIAN to "Cth: Iuran makan siang, patungan proyek",
            AppLanguage.ENGLISH to "E.g.: Lunch fee, project split",
            AppLanguage.SPANISH to "Ej.: Cuota de almuerzo, división de proyecto",
            AppLanguage.CHINESE to "例如：午餐费、项目分摊",
            AppLanguage.ARABIC to "على سبيل المثال: رسوم الغداء، تقسيم المشروع",
            AppLanguage.JAPANESE to "例: 昼食代、プロジェクト分担"
        ),
        "qr_copy_qr" to mapOf(
            AppLanguage.INDONESIAN to "Salin QR",
            AppLanguage.ENGLISH to "Copy QR",
            AppLanguage.SPANISH to "Copiar QR",
            AppLanguage.CHINESE to "复制 QR",
            AppLanguage.ARABIC to "نسخ QR",
            AppLanguage.JAPANESE to "QRをコピー"
        ),
        "qr_share_bill" to mapOf(
            AppLanguage.INDONESIAN to "Bagikan Tagihan",
            AppLanguage.ENGLISH to "Share Bill",
            AppLanguage.SPANISH to "Compartir Factura",
            AppLanguage.CHINESE to "分享账单",
            AppLanguage.ARABIC to "مشاركة الفاتورة",
            AppLanguage.JAPANESE to "請求書を共有"
        ),
        "qr_scan_camera_title" to mapOf(
            AppLanguage.INDONESIAN to "Pindai Kamera QR Penerima",
            AppLanguage.ENGLISH to "Scan Recipient QR Camera",
            AppLanguage.SPANISH to "Escanear Cámara QR del Destinatario",
            AppLanguage.CHINESE to "扫描收款人 QR 相机",
            AppLanguage.ARABIC to "مسح كاميرا QR المستلم",
            AppLanguage.JAPANESE to "受取人QRカメラをスキャン"
        ),
        "qr_receipt_tx_id" to mapOf(
            AppLanguage.INDONESIAN to "ID Transaksi:",
            AppLanguage.ENGLISH to "Transaction ID:",
            AppLanguage.SPANISH to "ID de Transacción:",
            AppLanguage.CHINESE to "交易 ID：",
            AppLanguage.ARABIC to "معرف المعاملة:",
            AppLanguage.JAPANESE to "取引ID:"
        ),
        "qr_receipt_time" to mapOf(
            AppLanguage.INDONESIAN to "Waktu:",
            AppLanguage.ENGLISH to "Time:",
            AppLanguage.SPANISH to "Hora:",
            AppLanguage.CHINESE to "时间：",
            AppLanguage.ARABIC to "الوقت:",
            AppLanguage.JAPANESE to "時間:"
        ),
        "qr_receipt_recipient" to mapOf(
            AppLanguage.INDONESIAN to "Penerima:",
            AppLanguage.ENGLISH to "Recipient:",
            AppLanguage.SPANISH to "Destinatario:",
            AppLanguage.CHINESE to "收款人：",
            AppLanguage.ARABIC to "المستلم:",
            AppLanguage.JAPANESE to "受取人:"
        ),
        "qr_receipt_amount_sent" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Dikirim:",
            AppLanguage.ENGLISH to "Amount Sent:",
            AppLanguage.SPANISH to "Monto Enviado:",
            AppLanguage.CHINESE to "已发送金额：",
            AppLanguage.ARABIC to "المبلغ المرسل:",
            AppLanguage.JAPANESE to "送金金額:"
        ),
        "qr_receipt_admin_fee" to mapOf(
            AppLanguage.INDONESIAN to "Potongan Biaya Admin:",
            AppLanguage.ENGLISH to "Admin Fee Deduction:",
            AppLanguage.SPANISH to "Deducción de Tarifa Administrativa:",
            AppLanguage.CHINESE to "管理费扣除：",
            AppLanguage.ARABIC to "خصم رسوم الإدارة:",
            AppLanguage.JAPANESE to "管理手数料控除:"
        ),
        "qr_receipt_net_received" to mapOf(
            AppLanguage.INDONESIAN to "Bersih Diterima Penerima:",
            AppLanguage.ENGLISH to "Net Received by Recipient:",
            AppLanguage.SPANISH to "Neto Recibido por el Destinatario:",
            AppLanguage.CHINESE to "收款人实收净额：",
            AppLanguage.ARABIC to "الصافي المستلم من قبل المستلم:",
            AppLanguage.JAPANESE to "受取人の純受取額:"
        ),
        "qr_receipt_remaining_balance" to mapOf(
            AppLanguage.INDONESIAN to "Sisa Saldo Anda:",
            AppLanguage.ENGLISH to "Your Remaining Balance:",
            AppLanguage.SPANISH to "Tu Saldo Restante:",
            AppLanguage.CHINESE to "您的剩余余额：",
            AppLanguage.ARABIC to "رصيدك المتبقي:",
            AppLanguage.JAPANESE to "残りの残高:"
        ),
        "qr_receipt_crypto_verified" to mapOf(
            AppLanguage.INDONESIAN to "Validasi Kriptografis Terverifikasi",
            AppLanguage.ENGLISH to "Cryptographic Validation Verified",
            AppLanguage.SPANISH to "Validación Criptográfica Verificada",
            AppLanguage.CHINESE to "加密验证已验证",
            AppLanguage.ARABIC to "تم التحقق من التحقق التشفيري",
            AppLanguage.JAPANESE to "暗号化検証済み"
        ),
        "qr_receipt_copy" to mapOf(
            AppLanguage.INDONESIAN to "Salin",
            AppLanguage.ENGLISH to "Copy",
            AppLanguage.SPANISH to "Copiar",
            AppLanguage.CHINESE to "复制",
            AppLanguage.ARABIC to "نسخ",
            AppLanguage.JAPANESE to "コピー"
        ),
        "qr_receipt_share" to mapOf(
            AppLanguage.INDONESIAN to "Bagikan Struk",
            AppLanguage.ENGLISH to "Share Receipt",
            AppLanguage.SPANISH to "Compartir Recibo",
            AppLanguage.CHINESE to "分享收据",
            AppLanguage.ARABIC to "مشاركة الإيصال",
            AppLanguage.JAPANESE to "領収書を共有"
        ),
        "qr_receipt_finish" to mapOf(
            AppLanguage.INDONESIAN to "Selesai",
            AppLanguage.ENGLISH to "Finish",
            AppLanguage.SPANISH to "Terminar",
            AppLanguage.CHINESE to "完成",
            AppLanguage.ARABIC to "إنهاء",
            AppLanguage.JAPANESE to "完了"
        ),
        "qr_pin_amount" to mapOf(
            AppLanguage.INDONESIAN to "Nominal Transfer:",
            AppLanguage.ENGLISH to "Transfer Amount:",
            AppLanguage.SPANISH to "Monto de Transferencia:",
            AppLanguage.CHINESE to "转账金额：",
            AppLanguage.ARABIC to "مبلغ التحويل:",
            AppLanguage.JAPANESE to "送金金額:"
        ),
        "qr_pin_admin_fee" to mapOf(
            AppLanguage.INDONESIAN to "Biaya Admin (Penerima):",
            AppLanguage.ENGLISH to "Admin Fee (Recipient):",
            AppLanguage.SPANISH to "Tarifa Administrativa (Destinatario):",
            AppLanguage.CHINESE to "管理费（收款人）：",
            AppLanguage.ARABIC to "رسوم الإدارة (المستلم):",
            AppLanguage.JAPANESE to "管理手数料（受取人）:"
        ),
        "qr_pin_cancel" to mapOf(
            AppLanguage.INDONESIAN to "Batal Transaksi",
            AppLanguage.ENGLISH to "Cancel Transaction",
            AppLanguage.SPANISH to "Cancelar Transacción",
            AppLanguage.CHINESE to "取消交易",
            AppLanguage.ARABIC to "إلغاء المعاملة",
            AppLanguage.JAPANESE to "取引をキャンセル"
        ),


        // ========== APP MODALS ==========
        "appmodal_withdraw_account_holder" to mapOf(
            AppLanguage.INDONESIAN to "Nama Pemilik Rekening / Akun",
            AppLanguage.ENGLISH to "Account Holder Name",
            AppLanguage.SPANISH to "Nombre del Titular",
            AppLanguage.CHINESE to "账户持有人姓名",
            AppLanguage.ARABIC to "اسم صاحب الحساب",
            AppLanguage.JAPANESE to "口座名義人"
        ),
        "appmodal_withdraw_amount" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Penarikan (Rp)",
            AppLanguage.ENGLISH to "Withdrawal Amount (Rp)",
            AppLanguage.SPANISH to "Monto de Retiro (Rp)",
            AppLanguage.CHINESE to "提款金额（Rp）",
            AppLanguage.ARABIC to "مبلغ السحب (Rp)",
            AppLanguage.JAPANESE to "引き出し金額 (Rp)"
        ),
        "appmodal_withdraw_ref_id" to mapOf(
            AppLanguage.INDONESIAN to "Ref ID:",
            AppLanguage.ENGLISH to "Ref ID:",
            AppLanguage.SPANISH to "ID de Referencia:",
            AppLanguage.CHINESE to "参考 ID：",
            AppLanguage.ARABIC to "معرف المرجع:",
            AppLanguage.JAPANESE to "参照ID:"
        ),
        "appmodal_withdraw_destination" to mapOf(
            AppLanguage.INDONESIAN to "Tujuan:",
            AppLanguage.ENGLISH to "Destination:",
            AppLanguage.SPANISH to "Destino:",
            AppLanguage.CHINESE to "目的地：",
            AppLanguage.ARABIC to "الوجهة:",
            AppLanguage.JAPANESE to "送金先:"
        ),
        "appmodal_withdraw_net" to mapOf(
            AppLanguage.INDONESIAN to "Nominal Net:",
            AppLanguage.ENGLISH to "Net Amount:",
            AppLanguage.SPANISH to "Monto Neto:",
            AppLanguage.CHINESE to "净额：",
            AppLanguage.ARABIC to "المبلغ الصافي:",
            AppLanguage.JAPANESE to "純額:"
        ),
        "appmodal_withdraw_hmac" to mapOf(
            AppLanguage.INDONESIAN to "HMAC Sig:",
            AppLanguage.ENGLISH to "HMAC Sig:",
            AppLanguage.SPANISH to "Firma HMAC:",
            AppLanguage.CHINESE to "HMAC 签名：",
            AppLanguage.ARABIC to "توقيع HMAC:",
            AppLanguage.JAPANESE to "HMAC署名:"
        ),
        "appmodal_withdraw_done_close" to mapOf(
            AppLanguage.INDONESIAN to "Selesai & Tutup",
            AppLanguage.ENGLISH to "Done & Close",
            AppLanguage.SPANISH to "Listo y Cerrar",
            AppLanguage.CHINESE to "完成并关闭",
            AppLanguage.ARABIC to "إنهاء وإغلاق",
            AppLanguage.JAPANESE to "完了して閉じる"
        ),
        "appmodal_newlink_slug" to mapOf(
            AppLanguage.INDONESIAN to "Custom Slug URL",
            AppLanguage.ENGLISH to "Custom Slug URL",
            AppLanguage.SPANISH to "URL Slug Personalizado",
            AppLanguage.CHINESE to "自定义 Slug URL",
            AppLanguage.ARABIC to "URL Slug مخصص",
            AppLanguage.JAPANESE to "カスタムSlug URL"
        ),
        "appmodal_newlink_subid" to mapOf(
            AppLanguage.INDONESIAN to "SubID Tracking (opsional: ig, tiktok, wa)",
            AppLanguage.ENGLISH to "SubID Tracking (optional: ig, tiktok, wa)",
            AppLanguage.SPANISH to "Seguimiento SubID (opcional: ig, tiktok, wa)",
            AppLanguage.CHINESE to "SubID 跟踪（可选：ig、tiktok、wa）",
            AppLanguage.ARABIC to "تتبع SubID (اختياري: ig، tiktok، wa)",
            AppLanguage.JAPANESE to "SubIDトラッキング（オプション: ig、tiktok、wa）"
        ),
        "appmodal_newlink_create" to mapOf(
            AppLanguage.INDONESIAN to "Buat Tautan & Mulai Melacak",
            AppLanguage.ENGLISH to "Create Link & Start Tracking",
            AppLanguage.SPANISH to "Crear Enlace y Comenzar a Rastrear",
            AppLanguage.CHINESE to "创建链接并开始跟踪",
            AppLanguage.ARABIC to "إنشاء رابط وبدء التتبع",
            AppLanguage.JAPANESE to "リンク作成と追跡開始"
        ),
        "appmodal_upgrade_basic" to mapOf(
            AppLanguage.INDONESIAN to "Paket dasar",
            AppLanguage.ENGLISH to "Basic Package",
            AppLanguage.SPANISH to "Paquete Básico",
            AppLanguage.CHINESE to "基础套餐",
            AppLanguage.ARABIC to "الحزمة الأساسية",
            AppLanguage.JAPANESE to "ベーシックパッケージ"
        ),
        "appmodal_upgrade_coming_soon" to mapOf(
            AppLanguage.INDONESIAN to "Coming Soon",
            AppLanguage.ENGLISH to "Coming Soon",
            AppLanguage.SPANISH to "Próximamente",
            AppLanguage.CHINESE to "即将推出",
            AppLanguage.ARABIC to "قريبًا",
            AppLanguage.JAPANESE to "近日公開"
        ),
        "appmodal_upgrade_retry" to mapOf(
            AppLanguage.INDONESIAN to "Cuba Lagi",
            AppLanguage.ENGLISH to "Try Again",
            AppLanguage.SPANISH to "Intentar de Nuevo",
            AppLanguage.CHINESE to "重试",
            AppLanguage.ARABIC to "حاول مرة أخرى",
            AppLanguage.JAPANESE to "もう一度"
        ),
        "appmodal_redeem_title" to mapOf(
            AppLanguage.INDONESIAN to "Tukar RTP (Royaltree Point) Global",
            AppLanguage.ENGLISH to "Redeem Global RTP (Royaltree Point)",
            AppLanguage.SPANISH to "Canjear RTP (Royaltree Point) Global",
            AppLanguage.CHINESE to "兑换全球 RTP（Royaltree Point）",
            AppLanguage.ARABIC to "استبدال RTP (Royaltree Point) العالمي",
            AppLanguage.JAPANESE to "グローバルRTP（Royaltree Point）を交換"
        ),
        "appmodal_redeem_global_standard" to mapOf(
            AppLanguage.INDONESIAN to "🌍 Standar Nilai RTP Global",
            AppLanguage.ENGLISH to "🌍 Global RTP Value Standard",
            AppLanguage.SPANISH to "🌍 Estándar de Valor RTP Global",
            AppLanguage.CHINESE to "🌍 全球 RTP 价值标准",
            AppLanguage.ARABIC to "🌍 معيار قيمة RTP العالمي",
            AppLanguage.JAPANESE to "🌍 グローバルRTP価値基準"
        ),
        "appmodal_redeem_send_to_other" to mapOf(
            AppLanguage.INDONESIAN to "Mau kirim poin ke pengguna lain?",
            AppLanguage.ENGLISH to "Want to send points to other users?",
            AppLanguage.SPANISH to "¿Quieres enviar puntos a otros usuarios?",
            AppLanguage.CHINESE to "想向其他用户发送积分吗？",
            AppLanguage.ARABIC to "هل تريد إرسال نقاط لمستخدمين آخرين؟",
            AppLanguage.JAPANESE to "他のユーザーにポイントを送りたいですか？"
        ),
        "appmodal_redeem_use_qr" to mapOf(
            AppLanguage.INDONESIAN to "Gunakan Transfer RTP via Scan QR",
            AppLanguage.ENGLISH to "Use RTP Transfer via QR Scan",
            AppLanguage.SPANISH to "Usar Transferencia RTP vía Escaneo QR",
            AppLanguage.CHINESE to "通过 QR 扫描使用 RTP 转账",
            AppLanguage.ARABIC to "استخدام تحويل RTP عبر مسح QR",
            AppLanguage.JAPANESE to "QRスキャンでRTP送金を使用"
        ),
        "appmodal_redeem_transfer_arrow" to mapOf(
            AppLanguage.INDONESIAN to "Transfer >",
            AppLanguage.ENGLISH to "Transfer >",
            AppLanguage.SPANISH to "Transferir >",
            AppLanguage.CHINESE to "转账 >",
            AppLanguage.ARABIC to "تحويل >",
            AppLanguage.JAPANESE to "送金 >"
        ),
        "appmodal_redeem_currency" to mapOf(
            AppLanguage.INDONESIAN to "Pilih Mata Uang Pencairan Poin:",
            AppLanguage.ENGLISH to "Select Point Withdrawal Currency:",
            AppLanguage.SPANISH to "Seleccionar Moneda de Retiro de Puntos:",
            AppLanguage.CHINESE to "选择积分提取货币：",
            AppLanguage.ARABIC to "اختر عملة سحب النقاط:",
            AppLanguage.JAPANESE to "ポイント換金通貨を選択:"
        ),
        "appmodal_redeem_payment_channel" to mapOf(
            AppLanguage.INDONESIAN to "Saluran Pembayaran Poin:",
            AppLanguage.ENGLISH to "Point Payment Channel:",
            AppLanguage.SPANISH to "Canal de Pago de Puntos:",
            AppLanguage.CHINESE to "积分支付渠道：",
            AppLanguage.ARABIC to "قناة دفع النقاط:",
            AppLanguage.JAPANESE to "ポイント支払いチャネル:"
        ),
        "appmodal_redeem_amount" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Poin yang Ditukar:",
            AppLanguage.ENGLISH to "Points Amount to Redeem:",
            AppLanguage.SPANISH to "Cantidad de Puntos a Canjear:",
            AppLanguage.CHINESE to "兑换积分数量：",
            AppLanguage.ARABIC to "مبلغ النقاط للاستبدال:",
            AppLanguage.JAPANESE to "交換するポイント数:"
        ),
        "appmodal_redeem_total_received" to mapOf(
            AppLanguage.INDONESIAN to "Total yang Diterima:",
            AppLanguage.ENGLISH to "Total Received:",
            AppLanguage.SPANISH to "Total Recibido:",
            AppLanguage.CHINESE to "总收款：",
            AppLanguage.ARABIC to "الإجمالي المستلم:",
            AppLanguage.JAPANESE to "受取総額:"
        ),
        "appmodal_redeem_global_usd" to mapOf(
            AppLanguage.INDONESIAN to "Nilai Acuan Global (USD):",
            AppLanguage.ENGLISH to "Global Reference Value (USD):",
            AppLanguage.SPANISH to "Valor de Referencia Global (USD):",
            AppLanguage.CHINESE to "全球参考价值（USD）：",
            AppLanguage.ARABIC to "القيمة المرجعية العالمية (USD):",
            AppLanguage.JAPANESE to "グローバル参照値 (USD):"
        ),
        "appmodal_redeem_confirm" to mapOf(
            AppLanguage.INDONESIAN to "Konfirmasi Penukaran Poin Global",
            AppLanguage.ENGLISH to "Confirm Global Point Exchange",
            AppLanguage.SPANISH to "Confirmar Canje de Puntos Global",
            AppLanguage.CHINESE to "确认全球积分兑换",
            AppLanguage.ARABIC to "تأكيد استبدال النقاط العالمي",
            AppLanguage.JAPANESE to "グローバルポイント交換の確認"
        ),
        "appmodal_coupon_buy_title" to mapOf(
            AppLanguage.INDONESIAN to "Beli Kupon Investasi",
            AppLanguage.ENGLISH to "Buy Investment Coupon",
            AppLanguage.SPANISH to "Comprar Cupón de Inversión",
            AppLanguage.CHINESE to "购买投资优惠券",
            AppLanguage.ARABIC to "شراء قسيمة استثمار",
            AppLanguage.JAPANESE to "投資クーポンを購入"
        ),
        "appmodal_coupon_apy" to mapOf(
            AppLanguage.INDONESIAN to "Estimasi Return (APY):",
            AppLanguage.ENGLISH to "Estimated Return (APY):",
            AppLanguage.SPANISH to "Retorno Estimado (APY):",
            AppLanguage.CHINESE to "预计回报（APY）：",
            AppLanguage.ARABIC to "العائد المتوقع (APY):",
            AppLanguage.JAPANESE to "予想リターン (APY):"
        ),
        "appmodal_coupon_unit_price" to mapOf(
            AppLanguage.INDONESIAN to "Harga per Kupon:",
            AppLanguage.ENGLISH to "Price per Coupon:",
            AppLanguage.SPANISH to "Precio por Cupón:",
            AppLanguage.CHINESE to "每张优惠券价格：",
            AppLanguage.ARABIC to "السعر لكل قسيمة:",
            AppLanguage.JAPANESE to "クーポン単価:"
        ),
        "appmodal_coupon_duration" to mapOf(
            AppLanguage.INDONESIAN to "Durasi Jatuh Tempo:",
            AppLanguage.ENGLISH to "Maturity Duration:",
            AppLanguage.SPANISH to "Duración de Vencimiento:",
            AppLanguage.CHINESE to "到期期限：",
            AppLanguage.ARABIC to "مدة الاستحقاق:",
            AppLanguage.JAPANESE to "満期期間:"
        ),
        "common_days_suffix" to mapOf(
            AppLanguage.INDONESIAN to " Hari",
            AppLanguage.ENGLISH to " Days",
            AppLanguage.SPANISH to " Días",
            AppLanguage.CHINESE to " 天",
            AppLanguage.ARABIC to " يوم",
            AppLanguage.JAPANESE to " 日"
        ),
        "appmodal_coupon_quantity" to mapOf(
            AppLanguage.INDONESIAN to "Jumlah Kupon:",
            AppLanguage.ENGLISH to "Coupon Quantity:",
            AppLanguage.SPANISH to "Cantidad de Cupones:",
            AppLanguage.CHINESE to "优惠券数量：",
            AppLanguage.ARABIC to "عدد القسائم:",
            AppLanguage.JAPANESE to "クーポン数:"
        ),
        "appmodal_coupon_total_cost" to mapOf(
            AppLanguage.INDONESIAN to "Total Biaya:",
            AppLanguage.ENGLISH to "Total Cost:",
            AppLanguage.SPANISH to "Costo Total:",
            AppLanguage.CHINESE to "总成本：",
            AppLanguage.ARABIC to "التكلفة الإجمالية:",
            AppLanguage.JAPANESE to "総費用:"
        ),
        "appmodal_coupon_buy_portfolio" to mapOf(
            AppLanguage.INDONESIAN to "Beli & Tambah ke Portofolio",
            AppLanguage.ENGLISH to "Buy & Add to Portfolio",
            AppLanguage.SPANISH to "Comprar y Agregar al Portafolio",
            AppLanguage.CHINESE to "购买并添加到投资组合",
            AppLanguage.ARABIC to "شراء وإضافة إلى المحفظة",
            AppLanguage.JAPANESE to "購入してポートフォリオに追加"
        ),
        "appmodal_2fa_otp_label" to mapOf(
            AppLanguage.INDONESIAN to "Masukkan 6 Digit OTP",
            AppLanguage.ENGLISH to "Enter 6-Digit OTP",
            AppLanguage.SPANISH to "Ingrese OTP de 6 Dígitos",
            AppLanguage.CHINESE to "输入 6 位 OTP",
            AppLanguage.ARABIC to "أدخل OTP المكون من 6 أرقام",
            AppLanguage.JAPANESE to "6桁のOTPを入力"
        ),
        "appmodal_2fa_disable" to mapOf(
            AppLanguage.INDONESIAN to "Matikan 2FA",
            AppLanguage.ENGLISH to "Disable 2FA",
            AppLanguage.SPANISH to "Desactivar 2FA",
            AppLanguage.CHINESE to "禁用 2FA",
            AppLanguage.ARABIC to "تعطيل 2FA",
            AppLanguage.JAPANESE to "2FAを無効化"
        ),
        "appmodal_2fa_verify_save" to mapOf(
            AppLanguage.INDONESIAN to "Verifikasi & Simpan",
            AppLanguage.ENGLISH to "Verify & Save",
            AppLanguage.SPANISH to "Verificar y Guardar",
            AppLanguage.CHINESE to "验证并保存",
            AppLanguage.ARABIC to "التحقق والحفظ",
            AppLanguage.JAPANESE to "確認して保存"
        ),


        // ========== APP MODALS (SISA) ==========
        "appmodal_listcoupon_title" to mapOf(
            AppLanguage.INDONESIAN to "Jual ke Pasar Sekunder",
            AppLanguage.ENGLISH to "Sell to Secondary Market",
            AppLanguage.SPANISH to "Vender al Mercado Secundario",
            AppLanguage.CHINESE to "出售到二级市场",
            AppLanguage.ARABIC to "البيع في السوق الثانوي",
            AppLanguage.JAPANESE to "セカンダリ市場に売却"
        ),
        "appmodal_listcoupon_price" to mapOf(
            AppLanguage.INDONESIAN to "Harga Jual di Pasar Sekunder (Rp)",
            AppLanguage.ENGLISH to "Selling Price in Secondary Market (Rp)",
            AppLanguage.SPANISH to "Precio de Venta en el Mercado Secundario (Rp)",
            AppLanguage.CHINESE to "二级市场售价（Rp）",
            AppLanguage.ARABIC to "سعر البيع في السوق الثانوي (Rp)",
            AppLanguage.JAPANESE to "セカンダリ市場の販売価格 (Rp)"
        ),
        "appmodal_listcoupon_cancel" to mapOf(
            AppLanguage.INDONESIAN to "Batal Jual",
            AppLanguage.ENGLISH to "Cancel Sell",
            AppLanguage.SPANISH to "Cancelar Venta",
            AppLanguage.CHINESE to "取消出售",
            AppLanguage.ARABIC to "إلغاء البيع",
            AppLanguage.JAPANESE to "売却をキャンセル"
        ),
        "appmodal_adminreview_title" to mapOf(
            AppLanguage.INDONESIAN to "Verifikasi Admin Manual",
            AppLanguage.ENGLISH to "Manual Admin Verification",
            AppLanguage.SPANISH to "Verificación Manual del Administrador",
            AppLanguage.CHINESE to "手动管理员验证",
            AppLanguage.ARABIC to "التحقق اليدوي من المسؤول",
            AppLanguage.JAPANESE to "手動管理者認証"
        ),
        "appmodal_adminreview_gross" to mapOf(
            AppLanguage.INDONESIAN to "Nominal Kotor:",
            AppLanguage.ENGLISH to "Gross Amount:",
            AppLanguage.SPANISH to "Monto Bruto:",
            AppLanguage.CHINESE to "总金额：",
            AppLanguage.ARABIC to "المبلغ الإجمالي:",
            AppLanguage.JAPANESE to "総額:"
        ),
        "appmodal_adminreview_fee" to mapOf(
            AppLanguage.INDONESIAN to "Biaya:",
            AppLanguage.ENGLISH to "Fee:",
            AppLanguage.SPANISH to "Tarifa:",
            AppLanguage.CHINESE to "费用：",
            AppLanguage.ARABIC to "الرسوم:",
            AppLanguage.JAPANESE to "手数料:"
        ),
        "appmodal_adminreview_net" to mapOf(
            AppLanguage.INDONESIAN to "Bersih Dibayarkan:",
            AppLanguage.ENGLISH to "Net Paid:",
            AppLanguage.SPANISH to "Neto Pagado:",
            AppLanguage.CHINESE to "净支付额：",
            AppLanguage.ARABIC to "الصافي المدفوع:",
            AppLanguage.JAPANESE to "純支払額:"
        ),
        "appmodal_adminreview_note" to mapOf(
            AppLanguage.INDONESIAN to "Catatan Validasi Admin",
            AppLanguage.ENGLISH to "Admin Validation Note",
            AppLanguage.SPANISH to "Nota de Validación del Administrador",
            AppLanguage.CHINESE to "管理员验证备注",
            AppLanguage.ARABIC to "ملاحظة التحقق من المسؤول",
            AppLanguage.JAPANESE to "管理者検証メモ"
        ),
        "appmodal_adminreview_reject" to mapOf(
            AppLanguage.INDONESIAN to "Tolak & Revert",
            AppLanguage.ENGLISH to "Reject & Revert",
            AppLanguage.SPANISH to "Rechazar y Revertir",
            AppLanguage.CHINESE to "拒绝并回滚",
            AppLanguage.ARABIC to "رفض وإرجاع",
            AppLanguage.JAPANESE to "拒否して元に戻す"
        ),
        "appmodal_adminreview_approve" to mapOf(
            AppLanguage.INDONESIAN to "Setujui & Bayar",
            AppLanguage.ENGLISH to "Approve & Pay",
            AppLanguage.SPANISH to "Aprobar y Pagar",
            AppLanguage.CHINESE to "批准并支付",
            AppLanguage.ARABIC to "الموافقة والدفع",
            AppLanguage.JAPANESE to "承認して支払う"
        ),
        "appmodal_language_title" to mapOf(
            AppLanguage.INDONESIAN to "Pilih Bahasa Internasional",
            AppLanguage.ENGLISH to "Select International Language",
            AppLanguage.SPANISH to "Seleccionar Idioma Internacional",
            AppLanguage.CHINESE to "选择国际语言",
            AppLanguage.ARABIC to "اختر اللغة الدولية",
            AppLanguage.JAPANESE to "国際言語を選択"
        ),
        "appmodal_2fa_secret_key" to mapOf(
            AppLanguage.INDONESIAN to "Secret Key: ",
            AppLanguage.ENGLISH to "Secret Key: ",
            AppLanguage.SPANISH to "Clave Secreta: ",
            AppLanguage.CHINESE to "密钥：",
            AppLanguage.ARABIC to "المفتاح السري: ",
            AppLanguage.JAPANESE to "シークレットキー: "
        ),


        // ========== ADMIN SCREEN ==========
        "admin_header_title" to mapOf(
            AppLanguage.INDONESIAN to "Portal Admin & Provider Royaltree",
            AppLanguage.ENGLISH to "Royaltree Admin & Provider Portal",
            AppLanguage.SPANISH to "Portal de Administración y Proveedor Royaltree",
            AppLanguage.CHINESE to "Royaltree 管理员和提供商门户",
            AppLanguage.ARABIC to "بوابة مسؤول ومزود Royaltree",
            AppLanguage.JAPANESE to "Royaltree管理者・プロバイダーポータル"
        ),
        "admin_header_subtitle" to mapOf(
            AppLanguage.INDONESIAN to "Pusat Pengaturan, Keuangan & Pelacakan Wilayah",
            AppLanguage.ENGLISH to "Settings, Finance & Region Tracking Center",
            AppLanguage.SPANISH to "Centro de Configuración, Finanzas y Seguimiento Regional",
            AppLanguage.CHINESE to "设置、财务和区域跟踪中心",
            AppLanguage.ARABIC to "مركز الإعدادات والمالية وتتبع المنطقة",
            AppLanguage.JAPANESE to "設定、財務、地域追跡センター"
        ),
        "admin_exit_btn" to mapOf(
            AppLanguage.INDONESIAN to "Keluar Admin",
            AppLanguage.ENGLISH to "Exit Admin",
            AppLanguage.SPANISH to "Salir de Admin",
            AppLanguage.CHINESE to "退出管理员",
            AppLanguage.ARABIC to "خروج المسؤول",
            AppLanguage.JAPANESE to "管理者を終了"
        ),
        "admin_tab_payout" to mapOf(
            AppLanguage.INDONESIAN to "Antrean Payout (",
            AppLanguage.ENGLISH to "Payout Queue (",
            AppLanguage.SPANISH to "Cola de Pagos (",
            AppLanguage.CHINESE to "付款队列（",
            AppLanguage.ARABIC to "قائمة الانتظار (",
            AppLanguage.JAPANESE to "支払キュー ("
        ),
        "admin_tab_users" to mapOf(
            AppLanguage.INDONESIAN to "Data Pengguna (",
            AppLanguage.ENGLISH to "User Data (",
            AppLanguage.SPANISH to "Datos de Usuario (",
            AppLanguage.CHINESE to "用户数据（",
            AppLanguage.ARABIC to "بيانات المستخدم (",
            AppLanguage.JAPANESE to "ユーザーデータ ("
        ),
        "admin_tab_location" to mapOf(
            AppLanguage.INDONESIAN to "📍 Lacak Lokasi & Heatmap",
            AppLanguage.ENGLISH to "📍 Location Tracking & Heatmap",
            AppLanguage.SPANISH to "📍 Seguimiento de Ubicación y Mapa de Calor",
            AppLanguage.CHINESE to "📍 位置跟踪和热力图",
            AppLanguage.ARABIC to "📍 تتبع الموقع والخريطة الحرارية",
            AppLanguage.JAPANESE to "📍 位置追跡とヒートマップ"
        ),
        "admin_tab_settings" to mapOf(
            AppLanguage.INDONESIAN to "⚙️ Pengaturan Platform",
            AppLanguage.ENGLISH to "⚙️ Platform Settings",
            AppLanguage.SPANISH to "⚙️ Configuración de la Plataforma",
            AppLanguage.CHINESE to "⚙️ 平台设置",
            AppLanguage.ARABIC to "⚙️ إعدادات المنصة",
            AppLanguage.JAPANESE to "⚙️ プラットフォーム設定"
        ),
        "admin_tab_endpoint" to mapOf(
            AppLanguage.INDONESIAN to "🔌 Endpoint & Portal",
            AppLanguage.ENGLISH to "🔌 Endpoint & Portal",
            AppLanguage.SPANISH to "🔌 Endpoint y Portal",
            AppLanguage.CHINESE to "🔌 端点与门户",
            AppLanguage.ARABIC to "🔌 نقطة النهاية والبوابة",
            AppLanguage.JAPANESE to "🔌 エンドポイントとポータル"
        ),
        "admin_payout_empty_title" to mapOf(
            AppLanguage.INDONESIAN to "Semua Penarikan Telah Diproses!",
            AppLanguage.ENGLISH to "All Withdrawals Have Been Processed!",
            AppLanguage.SPANISH to "¡Todos los Retiros Han Sido Procesados!",
            AppLanguage.CHINESE to "所有提款已处理！",
            AppLanguage.ARABIC to "تمت معالجة جميع السحوبات!",
            AppLanguage.JAPANESE to "すべての引き出しが処理されました！"
        ),
        "admin_payout_empty_desc" to mapOf(
            AppLanguage.INDONESIAN to "Tidak ada permintaan penarikan yang tertunda saat ini.",
            AppLanguage.ENGLISH to "No pending withdrawal requests at this time.",
            AppLanguage.SPANISH to "No hay solicitudes de retiro pendientes en este momento.",
            AppLanguage.CHINESE to "目前没有待处理的提款请求。",
            AppLanguage.ARABIC to "لا توجد طلبات سحب معلقة في الوقت الحالي.",
            AppLanguage.JAPANESE to "現在、保留中の引き出しリクエストはありません。"
        ),
        "admin_settings_title" to mapOf(
            AppLanguage.INDONESIAN to "Pusat Pengaturan Platform",
            AppLanguage.ENGLISH to "Platform Settings Center",
            AppLanguage.SPANISH to "Centro de Configuración de la Plataforma",
            AppLanguage.CHINESE to "平台设置中心",
            AppLanguage.ARABIC to "مركز إعدادات المنصة",
            AppLanguage.JAPANESE to "プラットフォーム設定センター"
        ),
        "admin_settings_desc" to mapOf(
            AppLanguage.INDONESIAN to "Kelola semua parameter sistem, batas penarikan & keamanan",
            AppLanguage.ENGLISH to "Manage all system parameters, withdrawal limits & security",
            AppLanguage.SPANISH to "Administrar todos los parámetros del sistema, límites de retiro y seguridad",
            AppLanguage.CHINESE to "管理所有系统参数、提款限制和安全",
            AppLanguage.ARABIC to "إدارة جميع معلمات النظام وحدود السحب والأمان",
            AppLanguage.JAPANESE to "すべてのシステムパラメータ、引き出し制限、セキュリティを管理"
        ),
        "admin_section_min_withdraw" to mapOf(
            AppLanguage.INDONESIAN to "1. Batas Minimum Penarikan Dana (IDR)",
            AppLanguage.ENGLISH to "1. Minimum Withdrawal Limit (IDR)",
            AppLanguage.SPANISH to "1. Límite Mínimo de Retiro (IDR)",
            AppLanguage.CHINESE to "1. 最低提款限额（IDR）",
            AppLanguage.ARABIC to "1. الحد الأدنى للسحب (IDR)",
            AppLanguage.JAPANESE to "1. 最低引き出し限度額 (IDR)"
        ),
        "admin_min_ewallet" to mapOf(
            AppLanguage.INDONESIAN to "Min. E-Wallet",
            AppLanguage.ENGLISH to "Min. E-Wallet",
            AppLanguage.SPANISH to "Mín. E-Wallet",
            AppLanguage.CHINESE to "最低电子钱包",
            AppLanguage.ARABIC to "الحد الأدنى للمحفظة الإلكترونية",
            AppLanguage.JAPANESE to "最低Eウォレット"
        ),
        "admin_min_bank" to mapOf(
            AppLanguage.INDONESIAN to "Min. Bank",
            AppLanguage.ENGLISH to "Min. Bank",
            AppLanguage.SPANISH to "Mín. Banco",
            AppLanguage.CHINESE to "最低银行",
            AppLanguage.ARABIC to "الحد الأدنى للبنك",
            AppLanguage.JAPANESE to "最低銀行"
        ),
        "admin_min_crypto" to mapOf(
            AppLanguage.INDONESIAN to "Min. Kripto",
            AppLanguage.ENGLISH to "Min. Crypto",
            AppLanguage.SPANISH to "Mín. Cripto",
            AppLanguage.CHINESE to "最低加密货币",
            AppLanguage.ARABIC to "الحد الأدنى للعملة المشفرة",
            AppLanguage.JAPANESE to "最低暗号通貨"
        ),
        "admin_section_commission" to mapOf(
            AppLanguage.INDONESIAN to "2. Tarif Komisi Affiliasi (%)",
            AppLanguage.ENGLISH to "2. Affiliate Commission Rate (%)",
            AppLanguage.SPANISH to "2. Tasa de Comisión de Afiliados (%)",
            AppLanguage.CHINESE to "2. 联盟佣金率（%）",
            AppLanguage.ARABIC to "2. معدل عمولة التسويق بالعمولة (%)",
            AppLanguage.JAPANESE to "2. アフィリエイト手数料率 (%)"
        ),
        "admin_tier_free" to mapOf(
            AppLanguage.INDONESIAN to "Tier Free (%)",
            AppLanguage.ENGLISH to "Free Tier (%)",
            AppLanguage.SPANISH to "Nivel Gratis (%)",
            AppLanguage.CHINESE to "免费层级（%）",
            AppLanguage.ARABIC to "المستوى المجاني (%)",
            AppLanguage.JAPANESE to "無料ティア (%)"
        ),
        "admin_tier_vip" to mapOf(
            AppLanguage.INDONESIAN to "Tier VIP (%)",
            AppLanguage.ENGLISH to "VIP Tier (%)",
            AppLanguage.SPANISH to "Nivel VIP (%)",
            AppLanguage.CHINESE to "VIP 层级（%）",
            AppLanguage.ARABIC to "المستوى VIP (%)",
            AppLanguage.JAPANESE to "VIPティア (%)"
        ),
        "admin_section_security" to mapOf(
            AppLanguage.INDONESIAN to "3. Keamanan & Batas Transfer QR (RTP)",
            AppLanguage.ENGLISH to "3. Security & QR Transfer Limit (RTP)",
            AppLanguage.SPANISH to "3. Seguridad y Límite de Transferencia QR (RTP)",
            AppLanguage.CHINESE to "3. 安全和 QR 转账限额（RTP）",
            AppLanguage.ARABIC to "3. الأمان وحد تحويل QR (RTP)",
            AppLanguage.JAPANESE to "3. セキュリティとQR送金制限 (RTP)"
        ),
        "admin_fee_rtp" to mapOf(
            AppLanguage.INDONESIAN to "Biaya Admin (RTP)",
            AppLanguage.ENGLISH to "Admin Fee (RTP)",
            AppLanguage.SPANISH to "Tarifa de Administración (RTP)",
            AppLanguage.CHINESE to "管理费（RTP）",
            AppLanguage.ARABIC to "رسوم الإدارة (RTP)",
            AppLanguage.JAPANESE to "管理手数料 (RTP)"
        ),
        "admin_daily_limit" to mapOf(
            AppLanguage.INDONESIAN to "Limit Harian (RTP)",
            AppLanguage.ENGLISH to "Daily Limit (RTP)",
            AppLanguage.SPANISH to "Límite Diario (RTP)",
            AppLanguage.CHINESE to "每日限额（RTP）",
            AppLanguage.ARABIC to "الحد اليومي (RTP)",
            AppLanguage.JAPANESE to "日次制限 (RTP)"
        ),
        "admin_mutex_delay" to mapOf(
            AppLanguage.INDONESIAN to "Jeda Mutex (Detik)",
            AppLanguage.ENGLISH to "Mutex Delay (Seconds)",
            AppLanguage.SPANISH to "Retardo de Mutex (Segundos)",
            AppLanguage.CHINESE to "互斥延迟（秒）",
            AppLanguage.ARABIC to "تأخير Mutex (ثواني)",
            AppLanguage.JAPANESE to "ミューテックス遅延 (秒)"
        ),
        "admin_section_tracking" to mapOf(
            AppLanguage.INDONESIAN to "4. Fitur Pelacakan & Status Server",
            AppLanguage.ENGLISH to "4. Tracking Features & Server Status",
            AppLanguage.SPANISH to "4. Funciones de Seguimiento y Estado del Servidor",
            AppLanguage.CHINESE to "4. 跟踪功能和服务器状态",
            AppLanguage.ARABIC to "4. ميزات التتبع وحالة الخادم",
            AppLanguage.JAPANESE to "4. 追跡機能とサーバーステータス"
        ),
        "admin_tracking_pinpoint" to mapOf(
            AppLanguage.INDONESIAN to "Pelacakan Pinpoint Pengguna",
            AppLanguage.ENGLISH to "User Pinpoint Tracking",
            AppLanguage.SPANISH to "Seguimiento Preciso de Usuarios",
            AppLanguage.CHINESE to "用户精确跟踪",
            AppLanguage.ARABIC to "تتبع المستخدم الدقيق",
            AppLanguage.JAPANESE to "ユーザーピンポイント追跡"
        ),
        "admin_tracking_desc" to mapOf(
            AppLanguage.INDONESIAN to "Mengumpulkan koordinat persebaran untuk analitik provider",
            AppLanguage.ENGLISH to "Collect distribution coordinates for provider analytics",
            AppLanguage.SPANISH to "Recopilar coordenadas de distribución para análisis de proveedores",
            AppLanguage.CHINESE to "收集分布坐标用于提供商分析",
            AppLanguage.ARABIC to "جمع إحداثيات التوزيع لتحليلات المزود",
            AppLanguage.JAPANESE to "プロバイダー分析用の分布座標を収集"
        ),
        "admin_maintenance_mode" to mapOf(
            AppLanguage.INDONESIAN to "Mode Pemeliharaan (Maintenance)",
            AppLanguage.ENGLISH to "Maintenance Mode",
            AppLanguage.SPANISH to "Modo de Mantenimiento",
            AppLanguage.CHINESE to "维护模式",
            AppLanguage.ARABIC to "وضع الصيانة",
            AppLanguage.JAPANESE to "メンテナンスモード"
        ),
        "admin_maintenance_desc" to mapOf(
            AppLanguage.INDONESIAN to "Nonaktifkan sementara penarikan dana untuk audit",
            AppLanguage.ENGLISH to "Temporarily disable withdrawals for audit",
            AppLanguage.SPANISH to "Deshabilitar temporalmente retiros para auditoría",
            AppLanguage.CHINESE to "暂时禁用提款以进行审计",
            AppLanguage.ARABIC to "تعطيل السحوبات مؤقتًا للتدقيق",
            AppLanguage.JAPANESE to "監査のため一時的に引き出しを無効化"
        ),
        "admin_save_settings" to mapOf(
            AppLanguage.INDONESIAN to "Simpan Perubahan Pengaturan",
            AppLanguage.ENGLISH to "Save Settings Changes",
            AppLanguage.SPANISH to "Guardar Cambios de Configuración",
            AppLanguage.CHINESE to "保存设置更改",
            AppLanguage.ARABIC to "حفظ تغييرات الإعدادات",
            AppLanguage.JAPANESE to "設定変更を保存"
        ),
        "admin_user_balance" to mapOf(
            AppLanguage.INDONESIAN to "Saldo Komisi Tersedia:",
            AppLanguage.ENGLISH to "Available Commission Balance:",
            AppLanguage.SPANISH to "Saldo de Comisión Disponible:",
            AppLanguage.CHINESE to "可用佣金余额：",
            AppLanguage.ARABIC to "رصيد العمولة المتاح:",
            AppLanguage.JAPANESE to "利用可能な手数料残高:"
        ),
        "admin_user_points" to mapOf(
            AppLanguage.INDONESIAN to "Poin Loyalitas (RTP):",
            AppLanguage.ENGLISH to "Loyalty Points (RTP):",
            AppLanguage.SPANISH to "Puntos de Lealtad (RTP):",
            AppLanguage.CHINESE to "忠诚积分（RTP）：",
            AppLanguage.ARABIC to "نقاط الولاء (RTP):",
            AppLanguage.JAPANESE to "ロイヤルティポイント (RTP):"
        ),
        "admin_user_2fa_status" to mapOf(
            AppLanguage.INDONESIAN to "Status 2FA & Enkripsi:",
            AppLanguage.ENGLISH to "2FA & Encryption Status:",
            AppLanguage.SPANISH to "Estado de 2FA y Cifrado:",
            AppLanguage.CHINESE to "2FA 和加密状态：",
            AppLanguage.ARABIC to "حالة 2FA والتشفير:",
            AppLanguage.JAPANESE to "2FAと暗号化の状態:"
        ),
        "admin_user_last_location" to mapOf(
            AppLanguage.INDONESIAN to "Pinpoint Lokasi Terakhir:",
            AppLanguage.ENGLISH to "Last Location Pinpoint:",
            AppLanguage.SPANISH to "Última Ubicación Preciso:",
            AppLanguage.CHINESE to "最后位置精确：",
            AppLanguage.ARABIC to "آخر موقع دقيق:",
            AppLanguage.JAPANESE to "最終位置ピンポイント:"
        ),
        "admin_heatmap_title" to mapOf(
            AppLanguage.INDONESIAN to "Analisis Densitas Wilayah (Provider)",
            AppLanguage.ENGLISH to "Region Density Analysis (Provider)",
            AppLanguage.SPANISH to "Análisis de Densidad Regional (Proveedor)",
            AppLanguage.CHINESE to "区域密度分析（提供商）",
            AppLanguage.ARABIC to "تحليل كثافة المنطقة (المزود)",
            AppLanguage.JAPANESE to "地域密度分析（プロバイダー）"
        ),
        "admin_heatmap_desc" to mapOf(
            AppLanguage.INDONESIAN to "Pelacakan otomatis kepadatan pengguna platform secara realtime",
            AppLanguage.ENGLISH to "Automatic real-time tracking of platform user density",
            AppLanguage.SPANISH to "Seguimiento automático en tiempo real de la densidad de usuarios",
            AppLanguage.CHINESE to "自动实时跟踪平台用户密度",
            AppLanguage.ARABIC to "تتبع تلقائي في الوقت الحقيقي لكثافة مستخدمي المنصة",
            AppLanguage.JAPANESE to "プラットフォームユーザー密度の自動リアルタイム追跡"
        ),
        "admin_area_crowded" to mapOf(
            AppLanguage.INDONESIAN to "WILAYAH RAMAI",
            AppLanguage.ENGLISH to "CROWDED AREA",
            AppLanguage.SPANISH to "ÁREA CONCURRIDA",
            AppLanguage.CHINESE to "拥挤区域",
            AppLanguage.ARABIC to "منطقة مزدحمة",
            AppLanguage.JAPANESE to "混雑エリア"
        ),
        "admin_area_quiet" to mapOf(
            AppLanguage.INDONESIAN to "WILAYAH SEPI",
            AppLanguage.ENGLISH to "QUIET AREA",
            AppLanguage.SPANISH to "ÁREA TRANQUILA",
            AppLanguage.CHINESE to "安静区域",
            AppLanguage.ARABIC to "منطقة هادئة",
            AppLanguage.JAPANESE to "閑散エリア"
        ),
        "admin_heatmap_map_title" to mapOf(
            AppLanguage.INDONESIAN to "Peta Persebaran & Heatmap",
            AppLanguage.ENGLISH to "Distribution Map & Heatmap",
            AppLanguage.SPANISH to "Mapa de Distribución y Mapa de Calor",
            AppLanguage.CHINESE to "分布图和热力图",
            AppLanguage.ARABIC to "خريطة التوزيع والخريطة الحرارية",
            AppLanguage.JAPANESE to "分布図とヒートマップ"
        ),
        "admin_radar_indonesia" to mapOf(
            AppLanguage.INDONESIAN to "📍 Radar Pinpoint Indonesia",
            AppLanguage.ENGLISH to "📍 Indonesia Pinpoint Radar",
            AppLanguage.SPANISH to "📍 Radar Preciso de Indonesia",
            AppLanguage.CHINESE to "📍 印度尼西亚精确雷达",
            AppLanguage.ARABIC to "📍 رادار إندونيسيا الدقيق",
            AppLanguage.JAPANESE to "📍 インドネシアインポイントレーダー"
        ),
        "admin_quiet_zone_opportunity" to mapOf(
            AppLanguage.INDONESIAN to "Zona Sepi: Peluang Promo Tambahan",
            AppLanguage.ENGLISH to "Quiet Zone: Additional Promo Opportunity",
            AppLanguage.SPANISH to "Zona Tranquila: Oportunidad de Promo Adicional",
            AppLanguage.CHINESE to "安静区：额外促销机会",
            AppLanguage.ARABIC to "المنطقة الهادئة: فرصة ترويجية إضافية",
            AppLanguage.JAPANESE to "閑散地帯: 追加プロモ機会"
        ),
        "admin_simulate_pin" to mapOf(
            AppLanguage.INDONESIAN to "Uji Simulasi Pin Provider:",
            AppLanguage.ENGLISH to "Test Provider Pin Simulation:",
            AppLanguage.SPANISH to "Probar Simulación de Pin de Proveedor:",
            AppLanguage.CHINESE to "测试提供商 Pin 模拟：",
            AppLanguage.ARABIC to "اختبار محاكاة Pin المزود:",
            AppLanguage.JAPANESE to "プロバイダーPinシミュレーションをテスト:"
        ),
        "admin_pinpoint_list_title" to mapOf(
            AppLanguage.INDONESIAN to "Daftar Detail Pinpoint Pengguna",
            AppLanguage.ENGLISH to "User Pinpoint Detail List",
            AppLanguage.SPANISH to "Lista Detallada de Ubicación Precisa de Usuarios",
            AppLanguage.CHINESE to "用户精确详情列表",
            AppLanguage.ARABIC to "قائمة تفاصيل المستخدم الدقيقة",
            AppLanguage.JAPANESE to "ユーザーピンポイント詳細リスト"
        ),
        "admin_pinpoint_list_desc" to mapOf(
            AppLanguage.INDONESIAN to "Menampilkan data koordinat GPS yang terekam oleh server",
            AppLanguage.ENGLISH to "Displaying GPS coordinate data recorded by the server",
            AppLanguage.SPANISH to "Mostrando datos de coordenadas GPS registrados por el servidor",
            AppLanguage.CHINESE to "显示服务器记录的 GPS 坐标数据",
            AppLanguage.ARABIC to "عرض بيانات إحداثيات GPS المسجلة بواسطة الخادم",
            AppLanguage.JAPANESE to "サーバーによって記録されたGPS座標データを表示"
        ),
        "admin_payout_pending" to mapOf(
            AppLanguage.INDONESIAN to "PENDING REVIEW",
            AppLanguage.ENGLISH to "PENDING REVIEW",
            AppLanguage.SPANISH to "REVISIÓN PENDIENTE",
            AppLanguage.CHINESE to "待审核",
            AppLanguage.ARABIC to "قيد المراجعة",
            AppLanguage.JAPANESE to "レビュー待ち"
        ),
        "admin_payout_net" to mapOf(
            AppLanguage.INDONESIAN to "Nominal Cair Bersih:",
            AppLanguage.ENGLISH to "Net Disbursed Amount:",
            AppLanguage.SPANISH to "Monto Neto Desembolsado:",
            AppLanguage.CHINESE to "净支付金额：",
            AppLanguage.ARABIC to "المبلغ الصافي المصروف:",
            AppLanguage.JAPANESE to "純支払額:"
        ),
        "admin_payout_process" to mapOf(
            AppLanguage.INDONESIAN to "Proses / Validasi",
            AppLanguage.ENGLISH to "Process / Validate",
            AppLanguage.SPANISH to "Procesar / Validar",
            AppLanguage.CHINESE to "处理/验证",
            AppLanguage.ARABIC to "معالجة / التحقق",
            AppLanguage.JAPANESE to "処理/検証"
        ),
        "admin_endpoint_title" to mapOf(
            AppLanguage.INDONESIAN to "Koneksi Portal Server Anda",
            AppLanguage.ENGLISH to "Your Portal Server Connection",
            AppLanguage.SPANISH to "Conexión de su Servidor Portal",
            AppLanguage.CHINESE to "您的门户服务器连接",
            AppLanguage.ARABIC to "اتصال خادم البوابة الخاص بك",
            AppLanguage.JAPANESE to "ポータルサーバー接続"
        ),
        "admin_endpoint_desc" to mapOf(
            AppLanguage.INDONESIAN to "Hubungkan aplikasi Royaltree ke sistem portal pusat Anda",
            AppLanguage.ENGLISH to "Connect Royaltree app to your central portal system",
            AppLanguage.SPANISH to "Conectar la aplicación Royaltree a su sistema de portal central",
            AppLanguage.CHINESE to "将 Royaltree 应用连接到您的中央门户系统",
            AppLanguage.ARABIC to "ربط تطبيق Royaltree بنظام البوابة المركزية الخاص بك",
            AppLanguage.JAPANESE to "Royaltreeアプリを中央ポータルシステムに接続"
        ),
        "admin_endpoint_base_url" to mapOf(
            AppLanguage.INDONESIAN to "Base URL Portal Server API",
            AppLanguage.ENGLISH to "Portal Server API Base URL",
            AppLanguage.SPANISH to "URL Base de la API del Servidor Portal",
            AppLanguage.CHINESE to "门户服务器 API 基础 URL",
            AppLanguage.ARABIC to "عنوان URL الأساسي لواجهة برمجة خادم البوابة",
            AppLanguage.JAPANESE to "ポータルサーバーAPIベースURL"
        ),
        "admin_endpoint_api_key" to mapOf(
            AppLanguage.INDONESIAN to "API Secret Key (Bearer Token)",
            AppLanguage.ENGLISH to "API Secret Key (Bearer Token)",
            AppLanguage.SPANISH to "Clave Secreta de API (Token Bearer)",
            AppLanguage.CHINESE to "API 密钥（Bearer 令牌）",
            AppLanguage.ARABIC to "مفتاح API السري (رمز Bearer)",
            AppLanguage.JAPANESE to "APIシークレットキー (Bearerトークン)"
        ),
        "admin_endpoint_test" to mapOf(
            AppLanguage.INDONESIAN to "Uji Koneksi",
            AppLanguage.ENGLISH to "Test Connection",
            AppLanguage.SPANISH to "Probar Conexión",
            AppLanguage.CHINESE to "测试连接",
            AppLanguage.ARABIC to "اختبار الاتصال",
            AppLanguage.JAPANESE to "接続をテスト"
        ),
        "admin_endpoint_save_url" to mapOf(
            AppLanguage.INDONESIAN to "Simpan URL",
            AppLanguage.ENGLISH to "Save URL",
            AppLanguage.SPANISH to "Guardar URL",
            AppLanguage.CHINESE to "保存 URL",
            AppLanguage.ARABIC to "حفظ URL",
            AppLanguage.JAPANESE to "URLを保存"
        ),
        "admin_endpoint_syncing" to mapOf(
            AppLanguage.INDONESIAN to "Sedang Sinkronisasi...",
            AppLanguage.ENGLISH to "Syncing...",
            AppLanguage.SPANISH to "Sincronizando...",
            AppLanguage.CHINESE to "正在同步...",
            AppLanguage.ARABIC to "جاري المزامنة...",
            AppLanguage.JAPANESE to "同期中..."
        ),
        "admin_endpoint_full_sync" to mapOf(
            AppLanguage.INDONESIAN to "Sinkronisasi Penuh Sekarang (Data User & Poin)",
            AppLanguage.ENGLISH to "Full Sync Now (User & Points Data)",
            AppLanguage.SPANISH to "Sincronización Completa Ahora (Datos de Usuario y Puntos)",
            AppLanguage.CHINESE to "立即完全同步（用户和积分数据）",
            AppLanguage.ARABIC to "مزامنة كاملة الآن (بيانات المستخدم والنقاط)",
            AppLanguage.JAPANESE to "今すぐ完全同期（ユーザーとポイントデータ）"
        ),
        "admin_endpoint_catalog_title" to mapOf(
            AppLanguage.INDONESIAN to "Katalog REST API Endpoints",
            AppLanguage.ENGLISH to "REST API Endpoints Catalog",
            AppLanguage.SPANISH to "Catálogo de Endpoints REST API",
            AppLanguage.CHINESE to "REST API 端点目录",
            AppLanguage.ARABIC to "كتالوج نقاط نهاية REST API",
            AppLanguage.JAPANESE to "REST APIエンドポイントカタログ"
        ),
        "admin_endpoint_catalog_desc" to mapOf(
            AppLanguage.INDONESIAN to "Spesifikasi lengkap untuk ditaruh di portal/server backend Anda",
            AppLanguage.ENGLISH to "Complete specification for your portal/backend server",
            AppLanguage.SPANISH to "Especificación completa para su portal/servidor backend",
            AppLanguage.CHINESE to "门户/后端服务器的完整规范",
            AppLanguage.ARABIC to "المواصفات الكاملة لبوابة/خادم الواجهة الخلفية",
            AppLanguage.JAPANESE to "ポータル/バックエンドサーバー用完全仕様"
        ),
        "admin_endpoint_curl_test" to mapOf(
            AppLanguage.INDONESIAN to "Perintah cURL Uji Coba:",
            AppLanguage.ENGLISH to "cURL Test Command:",
            AppLanguage.SPANISH to "Comando de Prueba cURL:",
            AppLanguage.CHINESE to "cURL 测试命令：",
            AppLanguage.ARABIC to "أمر اختبار cURL:",
            AppLanguage.JAPANESE to "cURLテストコマンド:"
        ),
        "admin_endpoint_json_example" to mapOf(
            AppLanguage.INDONESIAN to "Contoh Respons JSON (200 OK):",
            AppLanguage.ENGLISH to "Example JSON Response (200 OK):",
            AppLanguage.SPANISH to "Ejemplo de Respuesta JSON (200 OK):",
            AppLanguage.CHINESE to "示例 JSON 响应（200 OK）：",
            AppLanguage.ARABIC to "مثال على استجابة JSON (200 OK):",
            AppLanguage.JAPANESE to "JSONレスポンス例 (200 OK):"
        ),
        "admin_endpoint_server_script" to mapOf(
            AppLanguage.INDONESIAN to "Script Server Siap Pakai",
            AppLanguage.ENGLISH to "Ready-to-Use Server Script",
            AppLanguage.SPANISH to "Script de Servidor Listo para Usar",
            AppLanguage.CHINESE to "即用服务器脚本",
            AppLanguage.ARABIC to "نص الخادم جاهز للاستخدام",
            AppLanguage.JAPANESE to "すぐに使えるサーバースクリプト"
        ),
        "admin_endpoint_server_script_desc" to mapOf(
            AppLanguage.INDONESIAN to "Tersedia di folder /backend/ project Anda",
            AppLanguage.ENGLISH to "Available in your project /backend/ folder",
            AppLanguage.SPANISH to "Disponible en la carpeta /backend/ de su proyecto",
            AppLanguage.CHINESE to "可在项目的 /backend/ 文件夹中找到",
            AppLanguage.ARABIC to "متاح في مجلد /backend/ الخاص بمشروعك",
            AppLanguage.JAPANESE to "プロジェクトの/backend/フォルダにあります"
        ),
        "admin_endpoint_copy_script" to mapOf(
            AppLanguage.INDONESIAN to "Salin Script Lengkap ke Clipboard",
            AppLanguage.ENGLISH to "Copy Full Script to Clipboard",
            AppLanguage.SPANISH to "Copiar Script Completo al Portapapeles",
            AppLanguage.CHINESE to "将完整脚本复制到剪贴板",
            AppLanguage.ARABIC to "نسخ النص الكامل إلى الحافظة",
            AppLanguage.JAPANESE to "完全なスクリプトをクリップボードにコピー"
        ),


        // ========== ADMIN SCREEN (SISA) ==========
        "admin_active_users" to mapOf(
            AppLanguage.INDONESIAN to "Pengguna Aktif",
            AppLanguage.ENGLISH to "Active Users",
            AppLanguage.SPANISH to "Usuarios Activos",
            AppLanguage.CHINESE to "活跃用户",
            AppLanguage.ARABIC to "المستخدمون النشطون",
            AppLanguage.JAPANESE to "アクティブユーザー"
        ),
        "admin_users_expansion" to mapOf(
            AppLanguage.INDONESIAN to "Pengguna (Peluang Ekspansi)",
            AppLanguage.ENGLISH to "Users (Expansion Opportunity)",
            AppLanguage.SPANISH to "Usuarios (Oportunidad de Expansión)",
            AppLanguage.CHINESE to "用户（扩张机会）",
            AppLanguage.ARABIC to "المستخدمون (فرصة التوسع)",
            AppLanguage.JAPANESE to "ユーザー（拡張機会）"
        ),
        "admin_total_tracked" to mapOf(
            AppLanguage.INDONESIAN to "Total",
            AppLanguage.ENGLISH to "Total",
            AppLanguage.SPANISH to "Total",
            AppLanguage.CHINESE to "总计",
            AppLanguage.ARABIC to "الإجمالي",
            AppLanguage.JAPANESE to "合計"
        ),
        "admin_tracked_points" to mapOf(
            AppLanguage.INDONESIAN to "Titik Terlacak",
            AppLanguage.ENGLISH to "Tracked Points",
            AppLanguage.SPANISH to "Puntos Rastreados",
            AppLanguage.CHINESE to "追踪点",
            AppLanguage.ARABIC to "النقاط المتتبعة",
            AppLanguage.JAPANESE to "追跡ポイント"
        ),
        "admin_legend_free" to mapOf(
            AppLanguage.INDONESIAN to "User Free",
            AppLanguage.ENGLISH to "Free User",
            AppLanguage.SPANISH to "Usuario Gratis",
            AppLanguage.CHINESE to "免费用户",
            AppLanguage.ARABIC to "مستخدم مجاني",
            AppLanguage.JAPANESE to "無料ユーザー"
        ),
        "admin_legend_vip" to mapOf(
            AppLanguage.INDONESIAN to "VIP User",
            AppLanguage.ENGLISH to "VIP User",
            AppLanguage.SPANISH to "Usuario VIP",
            AppLanguage.CHINESE to "VIP 用户",
            AppLanguage.ARABIC to "مستخدم VIP",
            AppLanguage.JAPANESE to "VIPユーザー"
        ),

    )

    fun getString(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: translations[key]?.get(AppLanguage.INDONESIAN) ?: key
    }

    /**
     * Ambil terjemahan untuk key + bahasa.
     * @param key Kunci terjemahan
     * @param language Bahasa target
     * @param fallback Fallback kalau key tidak ditemukan
     */
    fun translate(key: String, language: AppLanguage, fallback: String = key): String {
        return translations[key]?.get(language) ?: fallback
    }
}
