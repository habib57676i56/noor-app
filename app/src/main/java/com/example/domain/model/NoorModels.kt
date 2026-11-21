package com.example.domain.model

enum class AppLanguage(val code: String) {
    ARABIC("ar"),
    ENGLISH("en");

    val isRtl: Boolean get() = this == ARABIC

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ARABIC
    }
}

enum class ThemeMode(val key: String) {
    DARK("dark"),
    LIGHT("light"),
    SYSTEM("system");

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.key == key } ?: DARK
    }
}

enum class PrayerName(val key: String, val isFard: Boolean) {
    FAJR("fajr", true),
    SUNRISE("sunrise", false),
    DHUHR("dhuhr", true),
    ASR("asr", true),
    MAGHRIB("maghrib", true),
    ISHA("isha", true);

    companion object {
        val fardPrayers: List<PrayerName> = entries.filter { it.isFard }
        fun fromKey(key: String): PrayerName =
            entries.firstOrNull { it.key == key } ?: FAJR
    }
}

enum class CalculationMethod(
    val key: String,
    val nameAr: String,
    val nameEn: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMinutes: Int = 0
) {
    UMM_AL_QURA("umm_al_qura", "أم القرى (مكة المكرمة)", "Umm al-Qura (Makkah)", 18.5, 0.0, 90),
    MWL("mwl", "رابطة العالم الإسلامي", "Muslim World League", 18.0, 17.0, 0),
    EGYPT("egypt", "الهيئة المصرية العامة للمساحة", "Egyptian General Authority", 19.5, 17.5, 0),
    KARACHI("karachi", "جامعة العلوم الإسلامية بكراتشي", "Univ. of Islamic Sciences, Karachi", 18.0, 18.0, 0),
    ISNA("isna", "الجمعية الإسلامية لأمريكا الشمالية", "ISNA (North America)", 15.0, 15.0, 0),
    DUBAI("dubai", "دائرة الشؤون الإسلامية بدبي", "Dubai / UAE", 18.2, 18.2, 0),
    QATAR("qatar", "قطر", "Qatar", 18.0, 0.0, 90),
    KUWAIT("kuwait", "الكويت", "Kuwait", 18.0, 17.5, 0),
    TURKEY("turkey", "رئاسة الشؤون الدينية التركية", "Diyanet (Turkey)", 18.0, 17.0, 0);

    companion object {
        fun fromKey(key: String?): CalculationMethod =
            entries.firstOrNull { it.key == key } ?: UMM_AL_QURA
    }
}

enum class AsrMadhhab(val key: String, val shadowFactor: Double, val nameAr: String, val nameEn: String) {
    SHAFI("shafi", 1.0, "الجمهور (الشافعي والمالكي والحنبلي)", "Standard (Shafi'i, Maliki, Hanbali)"),
    HANAFI("hanafi", 2.0, "المذهب الحنفي", "Hanafi");

    companion object {
        fun fromKey(key: String?): AsrMadhhab =
            entries.firstOrNull { it.key == key } ?: SHAFI
    }
}

data class CityLocation(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val countryEn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String
)

data class PrayerMinuteOffsets(
    val fajr: Int = 0,
    val sunrise: Int = 0,
    val dhuhr: Int = 0,
    val asr: Int = 0,
    val maghrib: Int = 0,
    val isha: Int = 0
)

data class DailyPrayerSchedule(
    val dateKey: String,
    val epochDay: Long,
    val fajrMillis: Long,
    val sunriseMillis: Long,
    val dhuhrMillis: Long,
    val asrMillis: Long,
    val maghribMillis: Long,
    val ishaMillis: Long
) {
    fun timeFor(prayer: PrayerName): Long = when (prayer) {
        PrayerName.FAJR -> fajrMillis
        PrayerName.SUNRISE -> sunriseMillis
        PrayerName.DHUHR -> dhuhrMillis
        PrayerName.ASR -> asrMillis
        PrayerName.MAGHRIB -> maghribMillis
        PrayerName.ISHA -> ishaMillis
    }

    fun allOrdered(): List<Pair<PrayerName, Long>> = listOf(
        PrayerName.FAJR to fajrMillis,
        PrayerName.SUNRISE to sunriseMillis,
        PrayerName.DHUHR to dhuhrMillis,
        PrayerName.ASR to asrMillis,
        PrayerName.MAGHRIB to maghribMillis,
        PrayerName.ISHA to ishaMillis
    )
}

data class NextPrayerInfo(
    val nextPrayer: PrayerName,
    val nextPrayerMillis: Long,
    val previousPrayer: PrayerName,
    val previousPrayerMillis: Long,
    val remainingMillis: Long,
    val progress: Float
)

enum class AdhkarCategory(val id: String, val nameAr: String, val nameEn: String) {
    MORNING("morning", "أذكار الصباح", "Morning Adhkar"),
    EVENING("evening", "أذكار المساء", "Evening Adhkar"),
    AFTER_PRAYER("after_prayer", "أذكار بعد الصلاة", "After Prayer"),
    SLEEP("sleep", "أذكار النوم", "Before Sleep"),
    WAKING("waking", "أذكار الاستيقاظ", "Upon Waking"),
    HOME_MOSQUE("home_mosque", "أذكار الدخول والخروج", "Entering & Leaving"),
    TRAVEL("travel", "أذكار السفر", "Travel Adhkar"),
    PROPHETIC_DUA("prophetic_dua", "الأدعية النبوية", "Prophetic Duas"),
    QURANIC_DUA("quranic_dua", "أدعية قرآنية", "Quranic Duas");

    companion object {
        fun fromId(id: String): AdhkarCategory =
            entries.firstOrNull { it.id == id } ?: MORNING
    }
}

data class DhikrItem(
    val id: String,
    val category: AdhkarCategory,
    val textAr: String,
    val targetCount: Int,
    val sourceAr: String,
    val sourceEn: String,
    val meaningAr: String,
    val meaningEn: String
)

data class TasbihPreset(
    val id: String,
    val textAr: String,
    val textEn: String,
    val defaultTarget: Int
)

enum class QuranReadingTheme(val key: String) {
    AUTO("auto"),
    SEPIA("sepia"),
    NIGHT("night"),
    LIGHT("light");

    companion object {
        fun fromKey(key: String?): QuranReadingTheme =
            entries.firstOrNull { it.key == key } ?: AUTO
    }
}

enum class QuranReciter(val id: String, val nameAr: String, val nameEn: String, val cdnSlug: String) {
    ALAFASY("alafasy", "مشاري راشد العفاسي", "Mishary Rashid Alafasy", "ar.alafasy"),
    HUSARY("husary", "محمود خليل الحصري", "Mahmoud Khalil Al-Husary", "ar.husary"),
    MINSHAWI("minshawi", "محمد صديق المنشاوي", "Muhammad Siddiq Al-Minshawi", "ar.minshawi");

    companion object {
        fun fromId(id: String?): QuranReciter =
            entries.firstOrNull { it.id == id } ?: ALAFASY
    }
}

data class SurahMeta(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val meaningEn: String,
    val verseCount: Int,
    val isMeccan: Boolean,
    val startJuz: Int,
    val startHizb: Int
)

data class QuranVerse(
    val surahNumber: Int,
    val numberInSurah: Int,
    val globalNumber: Int,
    val textUthmani: String,
    val tafsirAr: String,
    val audioUrl: String
)

data class HijriDateModel(
    val year: Int,
    val month: Int, // 1..12
    val day: Int,   // 1..30
    val monthNameAr: String,
    val monthNameEn: String
)

data class IslamicOccasion(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val hijriMonth: Int,
    val hijriDay: Int,
    val daysUntil: Int
)

data class AchievementItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val unlocked: Boolean,
    val progress: Float
)
