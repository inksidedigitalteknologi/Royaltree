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
            AppLanguage.INDONESIAN to "Ruang Mining Royaltree",
            AppLanguage.ENGLISH to "Royaltree Mining Room",
            AppLanguage.SPANISH to "Sala de Minería Royaltree",
            AppLanguage.CHINESE to "Royaltree 挖矿室",
            AppLanguage.ARABIC to "غرفة تعدين Royaltree",
            AppLanguage.JAPANESE to "Royaltree マイニングルーム"
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
            AppLanguage.INDONESIAN to "Tonton Iklan Sponsor (+2-5 RTP)",
            AppLanguage.ENGLISH to "Watch Sponsor Ad (+2-5 RTP)",
            AppLanguage.SPANISH to "Ver Anuncio Patrocinado (+2-5 RTP)",
            AppLanguage.CHINESE to "观看赞助广告 (+2-5 RTP)",
            AppLanguage.ARABIC to "مشاهدة إعلان (+2-5 RTP)",
            AppLanguage.JAPANESE to "スポンサー広告を視聴 (+2-5 RTP)"
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
            AppLanguage.INDONESIAN to "Buka Ruang Mining Royaltree untuk menaruh item dan menghasilkan RTP pasif 24/7!",
            AppLanguage.ENGLISH to "Open Game Room to place items and generate passive points 24/7!",
            AppLanguage.SPANISH to "¡Abre Game Room para colocar objetos y generar puntos pasivos 24/7!",
            AppLanguage.CHINESE to "打开游戏室放置物品，24/7 产生被动积分！",
            AppLanguage.ARABIC to "افتح غرفة الألعاب لوضع العناصر وتوليد نقاط سلبية 24/7!",
            AppLanguage.JAPANESE to "ゲームルームを開いてアイテムを配置し、24時間受動的にポイントを生成しましょう！"
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
            AppLanguage.INDONESIAN to "Tonton Iklan Sponsor",
            AppLanguage.ENGLISH to "Watch Sponsor Ad",
            AppLanguage.SPANISH to "Ver anuncio patrocinado",
            AppLanguage.CHINESE to "观看赞助广告",
            AppLanguage.ARABIC to "شاهد إعلان الراعي",
            AppLanguage.JAPANESE to "スポンサー広告を見る"
        ),
        "home_watch_ad_desc" to mapOf(
            AppLanguage.INDONESIAN to "Dapat +2-5 RTP • 30 detik",
            AppLanguage.ENGLISH to "Get +2-5 RTP • 30 sec",
            AppLanguage.SPANISH to "Obtén +2-5 RTP • 30 seg",
            AppLanguage.CHINESE to "获得 +2-5 RTP • 30 秒",
            AppLanguage.ARABIC to "احصل على +2-5 RTP • 30 ثانية",
            AppLanguage.JAPANESE to "+2-5 RTP 獲得 • 30秒"
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
            AppLanguage.INDONESIAN to "💰 Komisi",
            AppLanguage.ENGLISH to "💰 Commission",
            AppLanguage.SPANISH to "💰 Comisión",
            AppLanguage.CHINESE to "💰 佣金",
            AppLanguage.ARABIC to "💰 عمولة",
            AppLanguage.JAPANESE to "💰 手数料"
        ),
        "profile_edit" to mapOf(
            AppLanguage.INDONESIAN to "✏️ Edit Profil",
            AppLanguage.ENGLISH to "✏️ Edit Profile",
            AppLanguage.SPANISH to "✏️ Editar Perfil",
            AppLanguage.CHINESE to "✏️ 编辑个人资料",
            AppLanguage.ARABIC to "✏️ تعديل الملف",
            AppLanguage.JAPANESE to "✏️ プロフィール編集"
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
            AppLanguage.INDONESIAN to "⛏️ Status Mining",
            AppLanguage.ENGLISH to "⛏️ Mining Status",
            AppLanguage.SPANISH to "⛏️ Estado de Minería",
            AppLanguage.CHINESE to "⛏️ 挖矿状态",
            AppLanguage.ARABIC to "⛏️ حالة التعدين",
            AppLanguage.JAPANESE to "⛏️ マイニング状況"
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
            AppLanguage.INDONESIAN to "⚡ Aksi Cepat",
            AppLanguage.ENGLISH to "⚡ Quick Actions",
            AppLanguage.SPANISH to "⚡ Acciones Rápidas",
            AppLanguage.CHINESE to "⚡ 快捷操作",
            AppLanguage.ARABIC to "⚡ إجراءات سريعة",
            AppLanguage.JAPANESE to "⚡ クイックアクション"
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
            AppLanguage.INDONESIAN to "👤 Profil",
            AppLanguage.ENGLISH to "👤 Profile",
            AppLanguage.SPANISH to "👤 Perfil",
            AppLanguage.CHINESE to "👤 个人资料",
            AppLanguage.ARABIC to "👤 الملف الشخصي",
            AppLanguage.JAPANESE to "👤 プロフィール"
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
