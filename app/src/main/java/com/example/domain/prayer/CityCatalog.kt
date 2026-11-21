package com.example.domain.prayer

import com.example.domain.model.CityLocation

object CityCatalog {
    val defaultCity = CityLocation(
        id = "makkah",
        nameAr = "مكة المكرمة",
        nameEn = "Makkah",
        countryAr = "المملكة العربية السعودية",
        countryEn = "Saudi Arabia",
        latitude = 21.4225,
        longitude = 39.8262,
        timezoneId = "Asia/Riyadh"
    )

    val cities: List<CityLocation> = listOf(
        defaultCity,
        CityLocation("madinah", "المدينة المنورة", "Madinah", "المملكة العربية السعودية", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh"),
        CityLocation("riyadh", "الرياض", "Riyadh", "المملكة العربية السعودية", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh"),
        CityLocation("jeddah", "جدة", "Jeddah", "المملكة العربية السعودية", "Saudi Arabia", 21.4858, 39.1925, "Asia/Riyadh"),
        CityLocation("dammam", "الدمام", "Dammam", "المملكة العربية السعودية", "Saudi Arabia", 26.4207, 50.0888, "Asia/Riyadh"),
        CityLocation("cairo", "القاهرة", "Cairo", "مصر", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        CityLocation("alexandria", "الإسكندرية", "Alexandria", "مصر", "Egypt", 31.2001, 29.9187, "Africa/Cairo"),
        CityLocation("jerusalem", "القدس", "Jerusalem", "فلسطين", "Palestine", 31.7683, 35.2137, "Asia/Jerusalem"),
        CityLocation("amman", "عمّان", "Amman", "الأردن", "Jordan", 31.9454, 35.9284, "Asia/Amman"),
        CityLocation("dubai", "دبي", "Dubai", "الإمارات", "UAE", 25.2048, 55.2708, "Asia/Dubai"),
        CityLocation("abudhabi", "أبوظبي", "Abu Dhabi", "الإمارات", "UAE", 24.4539, 54.3773, "Asia/Dubai"),
        CityLocation("doha", "الدوحة", "Doha", "قطر", "Qatar", 25.2854, 51.5310, "Asia/Qatar"),
        CityLocation("kuwait", "مدينة الكويت", "Kuwait City", "الكويت", "Kuwait", 29.3759, 47.9774, "Asia/Kuwait"),
        CityLocation("manama", "المنامة", "Manama", "البحرين", "Bahrain", 26.2285, 50.5860, "Asia/Bahrain"),
        CityLocation("muscat", "مسقط", "Muscat", "عُمان", "Oman", 23.5880, 58.3829, "Asia/Muscat"),
        CityLocation("baghdad", "بغداد", "Baghdad", "العراق", "Iraq", 33.3152, 44.3661, "Asia/Baghdad"),
        CityLocation("damascus", "دمشق", "Damascus", "سوريا", "Syria", 33.5138, 36.2765, "Asia/Damascus"),
        CityLocation("beirut", "بيروت", "Beirut", "لبنان", "Lebanon", 33.8938, 35.5018, "Asia/Beirut"),
        CityLocation("sanaa", "صنعاء", "Sana'a", "اليمن", "Yemen", 15.3694, 44.1910, "Asia/Aden"),
        CityLocation("khartoum", "الخرطوم", "Khartoum", "السودان", "Sudan", 15.5007, 32.5599, "Africa/Khartoum"),
        CityLocation("tripoli", "طرابلس", "Tripoli", "ليبيا", "Libya", 32.8872, 13.1913, "Africa/Tripoli"),
        CityLocation("tunis", "تونس", "Tunis", "تونس", "Tunisia", 36.8065, 10.1815, "Africa/Tunis"),
        CityLocation("algiers", "الجزائر", "Algiers", "الجزائر", "Algeria", 36.7538, 3.0588, "Africa/Algiers"),
        CityLocation("rabat", "الرباط", "Rabat", "المغرب", "Morocco", 34.0209, -6.8416, "Africa/Casablanca"),
        CityLocation("casablanca", "الدار البيضاء", "Casablanca", "المغرب", "Morocco", 33.5731, -7.5898, "Africa/Casablanca"),
        CityLocation("istanbul", "إسطنبول", "Istanbul", "تركيا", "Turkey", 41.0082, 28.9784, "Europe/Istanbul"),
        CityLocation("ankara", "أنقرة", "Ankara", "تركيا", "Turkey", 39.9334, 32.8597, "Europe/Istanbul"),
        CityLocation("karachi", "كراتشي", "Karachi", "باكستان", "Pakistan", 24.8607, 67.0011, "Asia/Karachi"),
        CityLocation("islamabad", "إسلام آباد", "Islamabad", "باكستان", "Pakistan", 33.6844, 73.0479, "Asia/Karachi"),
        CityLocation("dhaka", "دكا", "Dhaka", "بنغلاديش", "Bangladesh", 23.8103, 90.4125, "Asia/Dhaka"),
        CityLocation("jakarta", "جاكرتا", "Jakarta", "إندونيسيا", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        CityLocation("kualalumpur", "كوالالمبور", "Kuala Lumpur", "ماليزيا", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        CityLocation("london", "لندن", "London", "المملكة المتحدة", "United Kingdom", 51.5074, -0.1278, "Europe/London"),
        CityLocation("paris", "باريس", "Paris", "فرنسا", "France", 48.8566, 2.3522, "Europe/Paris"),
        CityLocation("berlin", "برلين", "Berlin", "ألمانيا", "Germany", 52.5200, 13.4050, "Europe/Berlin"),
        CityLocation("newyork", "نيويورك", "New York", "الولايات المتحدة", "United States", 40.7128, -74.0060, "America/New_York"),
        CityLocation("dearborn", "ديربورن", "Dearborn", "الولايات المتحدة", "United States", 42.3223, -83.1763, "America/Detroit"),
        CityLocation("toronto", "تورونتو", "Toronto", "كندا", "Canada", 43.6532, -79.3832, "America/Toronto"),
        CityLocation("sydney", "سيدني", "Sydney", "أستراليا", "Australia", -33.8688, 151.2093, "Australia/Sydney")
    )

    fun findById(id: String): CityLocation =
        cities.firstOrNull { it.id == id } ?: defaultCity
}
