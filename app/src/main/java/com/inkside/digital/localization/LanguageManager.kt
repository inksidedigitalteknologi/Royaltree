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
            AppLanguage.INDONESIAN to "Bayar %d Poin",
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
            AppLanguage.INDONESIAN to "Royaltree adalah platform afiliasi rewarding yang memberi kamu poin dari aktivitas sehari-hari.",
            AppLanguage.ENGLISH to "Royaltree is a rewarding affiliate platform that gives you points from daily activities.",
            AppLanguage.SPANISH to "Royaltree es una plataforma de afiliados que te da puntos por actividades diarias.",
            AppLanguage.CHINESE to "Royaltree 是一个奖励联盟平台，通过日常活动为您赚取积分。",
            AppLanguage.ARABIC to "رويالتري هي منصة تسويق بالعمولة تمنحك نقاطًا من الأنشطة اليومية.",
            AppLanguage.JAPANESE to "Royaltreeは日常活動でポイントが貯まるアフィリエイトプラットフォームです。"
        ),
        "onboarding_points_title" to mapOf(
            AppLanguage.INDONESIAN to "Banyak Cara Dapat Poin",
            AppLanguage.ENGLISH to "Many Ways to Earn Points",
            AppLanguage.SPANISH to "Muchas Formas de Ganar Puntos",
            AppLanguage.CHINESE to "多种赚取积分的方式",
            AppLanguage.ARABIC to "طرق عديدة لكسب النقاط",
            AppLanguage.JAPANESE to "ポイントを稼ぐ多くの方法"
        ),
        "onboarding_points_desc" to mapOf(
            AppLanguage.INDONESIAN to "Langkah kaki, misi harian, game, dan offerwall — semua bisa jadi poin.",
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
            AppLanguage.INDONESIAN to "Tukar poin jadi saldo, lalu tarik ke e-wallet atau bank favoritmu.",
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
            AppLanguage.INDONESIAN to "Poin",
            AppLanguage.ENGLISH to "Points",
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
