package com.example.localization

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
            AppLanguage.INDONESIAN to "Misi Poin",
            AppLanguage.ENGLISH to "Point Missions",
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
            AppLanguage.INDONESIAN to "Poin Aktivitas",
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
        "sponsor_ad_title" to mapOf(
            AppLanguage.INDONESIAN to "Tonton Iklan Sponsor (+50 RTP)",
            AppLanguage.ENGLISH to "Watch Sponsor Ad (+50 RTP)",
            AppLanguage.SPANISH to "Ver Anuncio Patrocinado (+50 RTP)",
            AppLanguage.CHINESE to "观看赞助广告 (+50 RTP)",
            AppLanguage.ARABIC to "مشاهدة إعلان (+50 RTP)",
            AppLanguage.JAPANESE to "スポンサー広告を視聴 (+50 RTP)"
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
        )
    )

    fun getString(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: translations[key]?.get(AppLanguage.INDONESIAN) ?: key
    }
}
