package com.example.data.local.catalog

import com.example.domain.model.AdhkarCategory
import com.example.domain.model.DhikrItem
import com.example.domain.model.TasbihPreset

data class DailyAyahItem(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameAr: String,
    val surahNameEn: String,
    val textAr: String,
    val meaningEn: String
)

object AdhkarCatalog {

    val tasbihPresets: List<TasbihPreset> = listOf(
        TasbihPreset("subhanallah", "سُبْحَانَ اللَّهِ", "SubhanAllah (Glory be to Allah)", 33),
        TasbihPreset("alhamdulillah", "الْحَمْدُ لِلَّهِ", "Alhamdulillah (Praise be to Allah)", 33),
        TasbihPreset("allahuakbar", "اللَّهُ أَكْبَرُ", "Allahu Akbar (Allah is the Greatest)", 33),
        TasbihPreset("istighfar", "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ", "Astaghfirullah al-Azim (I seek Allah's forgiveness)", 100),
        TasbihPreset("tahlil", "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "La ilaha illAllah wahdahu la sharika lah", 100),
        TasbihPreset("hawqala", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "La hawla wa la quwwata illa billah", 33),
        TasbihPreset("salawat", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", "Allahumma salli wa sallim 'ala Nabiyyina Muhammad", 100),
        TasbihPreset("subhanallah_bihamdihi", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ", "SubhanAllahi wa bihamdihi, SubhanAllahil-Azim", 100)
    )

    val dailyAyahs: List<DailyAyahItem> = listOf(
        DailyAyahItem(2, 286, "البقرة", "Al-Baqarah", "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا", "Allah does not burden a soul beyond that it can bear."),
        DailyAyahItem(13, 28, "الرعد", "Ar-Ra'd", "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ", "Unquestionably, by the remembrance of Allah hearts are assured."),
        DailyAyahItem(94, 6, "الشرح", "Ash-Sharh", "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Indeed, with hardship comes ease."),
        DailyAyahItem(2, 152, "البقرة", "Al-Baqarah", "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ", "So remember Me; I will remember you. And be grateful to Me and do not deny Me."),
        DailyAyahItem(65, 3, "الطلاق", "At-Talaq", "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ", "And whoever relies upon Allah - then He is sufficient for him."),
        DailyAyahItem(39, 53, "الزمر", "Az-Zumar", "لَا تَقْنَطُوا مِن رَّحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا", "Do not despair of the mercy of Allah. Indeed, Allah forgives all sins."),
        DailyAyahItem(24, 35, "النور", "An-Nur", "اللَّهُ نُورُ السَّمَاوَاتِ وَالْأَرْضِ", "Allah is the Light of the heavens and the earth.")
    )

    val allAdhkar: List<DhikrItem> = listOf(
        // === 1. أذكار الصباح (MORNING) ===
        DhikrItem(
            id = "m_1",
            category = AdhkarCategory.MORNING,
            textAr = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            targetCount = 1,
            sourceAr = "سورة البقرة: ٢٥٥ (صحيح الترغيب)",
            sourceEn = "Surah Al-Baqarah 2:255",
            meaningAr = "من قرأها حين يصبح أُجير من الجن حتى يمسي.",
            meaningEn = "Whoever recites Ayat al-Kursi in the morning is protected until evening."
        ),
        DhikrItem(
            id = "m_2",
            category = AdhkarCategory.MORNING,
            textAr = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2723",
            meaningAr = "إقرار بملك الله وحده وسؤاله خير اليوم وبركته والاستعاذة من شره.",
            meaningEn = "Acknowledging Allah's sovereignty at dawn and asking for the goodness of the day."
        ),
        DhikrItem(
            id = "m_3",
            category = AdhkarCategory.MORNING,
            textAr = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            targetCount = 1,
            sourceAr = "صحيح البخاري (سيد الاستغفار)",
            sourceEn = "Sahih al-Bukhari 6306 (Sayyid al-Istighfar)",
            meaningAr = "سيد الاستغفار؛ من قاله موقناً به من النهار فمات قبل أن يمسي فهو من أهل الجنة.",
            meaningEn = "Master supplication for forgiveness; acknowledging Allah's favors and seeking mercy."
        ),
        DhikrItem(
            id = "m_4",
            category = AdhkarCategory.MORNING,
            textAr = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            targetCount = 3,
            sourceAr = "سنن أبي داود والترمذي",
            sourceEn = "Sunan Abi Dawud & At-Tirmidhi",
            meaningAr = "حفظ وتحصين من كل سوء وأذى طوال اليوم بإذن الله.",
            meaningEn = "Protection by Allah's Name so nothing on earth or in heaven can cause harm."
        ),
        DhikrItem(
            id = "m_5",
            category = AdhkarCategory.MORNING,
            textAr = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ ﷺ نَبِيًّا.",
            targetCount = 3,
            sourceAr = "سنن أبي داود وأحمد",
            sourceEn = "Sunan Abi Dawud & Ahmad",
            meaningAr = "من قالها حين يصبح ثلاثاً كان حقاً على الله أن يرضيه يوم القيامة.",
            meaningEn = "Expressing contentment with Allah as Lord, Islam as religion, and Muhammad as Prophet."
        ),
        DhikrItem(
            id = "m_6",
            category = AdhkarCategory.MORNING,
            textAr = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
            targetCount = 1,
            sourceAr = "المستدرك للحاكم وحسنه الألباني",
            sourceEn = "Al-Hakim (Graded Hasan)",
            meaningAr = "استغاثة برحمة الله لإصلاح كل شؤون الدنيا والآخرة.",
            meaningEn = "O Ever-Living, O Sustainer, in Your mercy I seek relief; rectify all my affairs."
        ),
        DhikrItem(
            id = "m_7",
            category = AdhkarCategory.MORNING,
            textAr = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.",
            targetCount = 100,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2692",
            meaningAr = "من قالها مائة مرة حين يصبح وحين يمسي لم يأت أحد يوم القيامة بأفضل مما جاء به إلا من قال مثل ذلك أو زاد.",
            meaningEn = "Glory be to Allah and praise Him — among the most rewarding daily remembrances."
        ),

        // === 2. أذكار المساء (EVENING) ===
        DhikrItem(
            id = "e_1",
            category = AdhkarCategory.EVENING,
            textAr = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا.",
            targetCount = 1,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2723",
            meaningAr = "افتتاح المساء بالتوحيد والحمد وطلب خير الليلة والوقاية من شرها.",
            meaningEn = "We have reached the evening and unto Allah belongs all sovereignty."
        ),
        DhikrItem(
            id = "e_2",
            category = AdhkarCategory.EVENING,
            textAr = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ.",
            targetCount = 1,
            sourceAr = "سنن الترمذي وابن ماجه",
            sourceEn = "Sunan At-Tirmidhi 3391",
            meaningAr = "تفويض الحياة والممات والمصير إلى الله وحده.",
            meaningEn = "O Allah, by You we enter the evening and morning, by You we live and die, and to You is the return."
        ),
        DhikrItem(
            id = "e_3",
            category = AdhkarCategory.EVENING,
            textAr = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            targetCount = 3,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2709",
            meaningAr = "استعاذة بكلمات الله الكاملة من كل ضرر في الليل.",
            meaningEn = "I seek refuge in the perfect words of Allah from the evil of what He has created."
        ),
        DhikrItem(
            id = "e_4",
            category = AdhkarCategory.EVENING,
            textAr = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَٰهَ إِلَّا أَنْتَ.",
            targetCount = 3,
            sourceAr = "سنن أبي داود",
            sourceEn = "Sunan Abi Dawud 5090",
            meaningAr = "سؤال الله العافية في الجسد والسمع والبصر.",
            meaningEn = "O Allah, grant me health in my body, my hearing, and my sight."
        ),
        DhikrItem(
            id = "e_5",
            category = AdhkarCategory.EVENING,
            textAr = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي.",
            targetCount = 1,
            sourceAr = "سنن أبي داود وابن ماجه",
            sourceEn = "Sunan Abi Dawud & Ibn Majah",
            meaningAr = "دعاء جامع للعفو والعافية والستر والأمان في الدنيا والآخرة.",
            meaningEn = "Comprehensive supplication for pardon, well-being, concealment of faults, and peace."
        ),

        // === 3. أذكار بعد الصلاة (AFTER_PRAYER) ===
        DhikrItem(
            id = "ap_1",
            category = AdhkarCategory.AFTER_PRAYER,
            textAr = "أَسْتَغْفِرُ اللَّهَ (ثَلَاثًا)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 591",
            meaningAr = "أول ما يقوله المصلي بعد السلام لجبر نقص الصلاة والثناء على الله.",
            meaningEn = "I seek Allah's forgiveness (3x). O Allah, You are Peace and from You comes peace."
        ),
        DhikrItem(
            id = "ap_2",
            category = AdhkarCategory.AFTER_PRAYER,
            textAr = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ.",
            targetCount = 1,
            sourceAr = "متفق عليه (البخاري ومسلم)",
            sourceEn = "Bukhari & Muslim",
            meaningAr = "توحيد الله والتسليم الكامل لعطائه ومنعه وحكمته.",
            meaningEn = "None can withhold what You give, nor give what You withhold."
        ),
        DhikrItem(
            id = "ap_3",
            category = AdhkarCategory.AFTER_PRAYER,
            textAr = "سُبْحَانَ اللَّهِ (٣٣)، وَالْحَمْدُ لِلَّهِ (٣٣)، وَاللَّهُ أَكْبَرُ (٣٣)، ثُمَّ تَمَامُ الْمِائَةِ: لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            targetCount = 33,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 597",
            meaningAr = "من سبح وحمد وكبر دبر كل صلاة ثلاثاً وثلاثين وختم بالتهليل غُفرت خطاياه وإن كانت مثل زبد البحر.",
            meaningEn = "Tasbih, Tahmid, and Takbir 33 times each after prayer, completed to 100 with Tahlil."
        ),
        DhikrItem(
            id = "ap_4",
            category = AdhkarCategory.AFTER_PRAYER,
            textAr = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ، وَشُكْرِكَ، وَحُسْنِ عِبَادَتِكَ.",
            targetCount = 1,
            sourceAr = "سنن أبي داود والنسائي",
            sourceEn = "Sunan Abi Dawud 1522",
            meaningAr = "وصية النبي ﷺ لمعاذ بن جبل ألا يدعها دبر كل صلاة.",
            meaningEn = "O Allah, help me to remember You, thank You, and worship You in excellence."
        ),

        // === 4. أذكار النوم (SLEEP) ===
        DhikrItem(
            id = "sl_1",
            category = AdhkarCategory.SLEEP,
            textAr = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا.",
            targetCount = 1,
            sourceAr = "صحيح البخاري",
            sourceEn = "Sahih al-Bukhari 6324",
            meaningAr = "ذكر مختصر يقال عند وضع الجنب للنوم استحضاراً لمعية الله.",
            meaningEn = "In Your name, O Allah, I die (sleep) and I live."
        ),
        DhikrItem(
            id = "sl_2",
            category = AdhkarCategory.SLEEP,
            textAr = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ.",
            targetCount = 3,
            sourceAr = "سنن أبي داود والترمذي",
            sourceEn = "Sunan Abi Dawud 5045",
            meaningAr = "كان النبي ﷺ إذا أراد أن يرقد وضع يده اليمنى تحت خده وقالها ثلاثاً.",
            meaningEn = "O Allah, protect me from Your punishment on the Day You resurrect Your servants."
        ),
        DhikrItem(
            id = "sl_3",
            category = AdhkarCategory.SLEEP,
            textAr = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لَا مَلْجَأَ وَلَا مَنْجَا مِنْكَ إِلَّا إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ.",
            targetCount = 1,
            sourceAr = "متفق عليه (البخاري ومسلم)",
            sourceEn = "Bukhari & Muslim",
            meaningAr = "من جعلها آخر ما يقول قبل النوم فمات من ليلته مات على الفطرة.",
            meaningEn = "Complete surrender and trust in Allah before sleep."
        ),
        DhikrItem(
            id = "sl_4",
            category = AdhkarCategory.SLEEP,
            textAr = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَكَفَانَا وَآوَانَا، فَكَمْ مِمَّنْ لَا كَافِيَ لَهُ وَلَا مُؤْوِيَ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2715",
            meaningAr = "شكر الله عند المبيت على نعم الطعام والشراب والكفاية والمسكن.",
            meaningEn = "Praise be to Allah who fed us, gave us drink, sufficed us, and sheltered us."
        ),

        // === 5. أذكار الاستيقاظ (WAKING) ===
        DhikrItem(
            id = "wk_1",
            category = AdhkarCategory.WAKING,
            textAr = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.",
            targetCount = 1,
            sourceAr = "صحيح البخاري",
            sourceEn = "Sahih al-Bukhari 6312",
            meaningAr = "حمد الله على تجديد الحياة مع كل صباح جديد.",
            meaningEn = "All praise is for Allah who gave us life after having taken it from us, and unto Him is the resurrection."
        ),
        DhikrItem(
            id = "wk_2",
            category = AdhkarCategory.WAKING,
            textAr = "الْحَمْدُ لِلَّهِ الَّذِي عَافَانِي فِي جَسَدِي، وَرَدَّ عَلَيَّ رُوحِي، وَأَذِنَ لِي بِذِكْرِهِ.",
            targetCount = 1,
            sourceAr = "سنن الترمذي",
            sourceEn = "Sunan At-Tirmidhi 3401",
            meaningAr = "شكر الله على العافية ورد الروح والتوفيق للذكر.",
            meaningEn = "Praise be to Allah who restored my health, returned my soul, and permitted me to remember Him."
        ),

        // === 6. أذكار الدخول والخروج (HOME_MOSQUE) ===
        DhikrItem(
            id = "hm_1",
            category = AdhkarCategory.HOME_MOSQUE,
            textAr = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ.",
            targetCount = 1,
            sourceAr = "سنن أبي داود والترمذي (الخروج من المنزل)",
            sourceEn = "Sunan Abi Dawud (Leaving Home)",
            meaningAr = "يقال لقائله: هُديت وكُفيت ووُقيت وتنحّى عنه الشيطان.",
            meaningEn = "In the name of Allah, I place my trust in Allah; there is no power or might except through Allah."
        ),
        DhikrItem(
            id = "hm_2",
            category = AdhkarCategory.HOME_MOSQUE,
            textAr = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا.",
            targetCount = 1,
            sourceAr = "سنن أبي داود (دخول المنزل)",
            sourceEn = "Sunan Abi Dawud (Entering Home)",
            meaningAr = "ثم يُسلّم على أهله لتحل البركة والسكينة في البيت.",
            meaningEn = "In the name of Allah we enter, in the name of Allah we leave, and upon our Lord we rely."
        ),
        DhikrItem(
            id = "hm_3",
            category = AdhkarCategory.HOME_MOSQUE,
            textAr = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم (دخول المسجد)",
            sourceEn = "Sahih Muslim 713 (Entering Mosque)",
            meaningAr = "يقال عند دخول المسجد بالقدم اليمنى بعد الصلاة على النبي ﷺ.",
            meaningEn = "O Allah, open for me the doors of Your mercy."
        ),
        DhikrItem(
            id = "hm_4",
            category = AdhkarCategory.HOME_MOSQUE,
            textAr = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم (الخروج من المسجد)",
            sourceEn = "Sahih Muslim 713 (Leaving Mosque)",
            meaningAr = "يقال عند الخروج من المسجد بالقدم اليسرى طلباً لرزق الله وفضله.",
            meaningEn = "O Allah, I ask You from Your bounty."
        ),

        // === 7. أذكار السفر (TRAVEL) ===
        DhikrItem(
            id = "tr_1",
            category = AdhkarCategory.TRAVEL,
            textAr = "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ، اللَّهُمَّ أَنْتَ الصَّاحِبُ فِي السَّفَرِ، وَالْخَلِيفَةُ فِي الْأَهْلِ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم (دعاء السفر)",
            sourceEn = "Sahih Muslim 1342 (Travel Supplication)",
            meaningAr = "دعاء الركوب والسفر؛ استعانة بالله على تيسير الطريق وحفظ الأهل والمال.",
            meaningEn = "Glory to Him who subjected this to us; O Allah, make our journey easy and be our Companion and Guardian."
        ),
        DhikrItem(
            id = "tr_2",
            category = AdhkarCategory.TRAVEL,
            textAr = "آيِبُونَ، تَائِبُونَ، عَابِدُونَ، لِرَبِّنَا حَامِدُونَ.",
            targetCount = 1,
            sourceAr = "صحيح مسلم (الرجوع من السفر)",
            sourceEn = "Sahih Muslim (Returning from Travel)",
            meaningAr = "يُزاد على دعاء السفر عند العودة إلى الديار.",
            meaningEn = "We return, repenting, worshipping, and praising our Lord."
        ),

        // === 8. الأدعية النبوية (PROPHETIC_DUA) ===
        DhikrItem(
            id = "pd_1",
            category = AdhkarCategory.PROPHETIC_DUA,
            textAr = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى.",
            targetCount = 1,
            sourceAr = "صحيح مسلم",
            sourceEn = "Sahih Muslim 2721",
            meaningAr = "دعاء نبوي جامع لأصول الخير: الهداية، والتقوى، والعفة، وغنى النفس.",
            meaningEn = "O Allah, I ask You for guidance, piety, chastity, and self-sufficiency."
        ),
        DhikrItem(
            id = "pd_2",
            category = AdhkarCategory.PROPHETIC_DUA,
            textAr = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ.",
            targetCount = 3,
            sourceAr = "سنن الترمذي",
            sourceEn = "Sunan At-Tirmidhi 2140",
            meaningAr = "كان من أكثر دعاء النبي ﷺ طلباً للثبات على الحق والإيمان.",
            meaningEn = "O Turner of hearts, keep my heart firm upon Your religion."
        ),
        DhikrItem(
            id = "pd_3",
            category = AdhkarCategory.PROPHETIC_DUA,
            textAr = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْجُبْنِ وَالْبُخْلِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ.",
            targetCount = 1,
            sourceAr = "صحيح البخاري",
            sourceEn = "Sahih al-Bukhari 6369",
            meaningAr = "استعاذة شاملة من الهموم النفسية والعجز وتراكم الديون.",
            meaningEn = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, cowardice and miserliness, and debt."
        ),
        DhikrItem(
            id = "pd_4",
            category = AdhkarCategory.PROPHETIC_DUA,
            textAr = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا.",
            targetCount = 1,
            sourceAr = "سنن ابن ماجه",
            sourceEn = "Sunan Ibn Majah 925",
            meaningAr = "دعاء جامع للعلم النافع والرزق الحلال والعمل المقبول.",
            meaningEn = "O Allah, I ask You for beneficial knowledge, pure provision, and accepted deeds."
        ),

        // === 9. أدعية قرآنية (QURANIC_DUA) ===
        DhikrItem(
            id = "qd_1",
            category = AdhkarCategory.QURANIC_DUA,
            textAr = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ.",
            targetCount = 1,
            sourceAr = "سورة البقرة: ٢٠١",
            sourceEn = "Surah Al-Baqarah 2:201",
            meaningAr = "أجمع دعاء لخير الدنيا والآخرة وكان أكثر دعاء النبي ﷺ.",
            meaningEn = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and protect us from the Fire."
        ),
        DhikrItem(
            id = "qd_2",
            category = AdhkarCategory.QURANIC_DUA,
            textAr = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ.",
            targetCount = 1,
            sourceAr = "سورة آل عمران: ٨",
            sourceEn = "Surah Aal-E-Imran 3:8",
            meaningAr = "دعاء الراسخين في العلم بطلب الثبات والرحمة.",
            meaningEn = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy."
        ),
        DhikrItem(
            id = "qd_3",
            category = AdhkarCategory.QURANIC_DUA,
            textAr = "رَبِّ اشْرَحْ لِي صَدْرِي * وَيَسِّرْ لِي أَمْرِي * وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي * يَفْقَهُوا قَوْلِي.",
            targetCount = 1,
            sourceAr = "سورة طه: ٢٥-٢٨",
            sourceEn = "Surah Ta-Ha 20:25-28",
            meaningAr = "دعاء نبي الله موسى عليه السلام لانشراح الصدر وتيسير الأمور وفصاحة البيان.",
            meaningEn = "My Lord, expand for me my chest, ease for me my task, and untie the knot from my tongue."
        ),
        DhikrItem(
            id = "qd_4",
            category = AdhkarCategory.QURANIC_DUA,
            textAr = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ.",
            targetCount = 3,
            sourceAr = "سورة الأنبياء: ٨٧",
            sourceEn = "Surah Al-Anbiya 21:87",
            meaningAr = "دعاء ذي النون؛ ما دعا به مسلم في شيء قط إلا استجاب الله له.",
            meaningEn = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers."
        ),
        DhikrItem(
            id = "qd_5",
            category = AdhkarCategory.QURANIC_DUA,
            textAr = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا.",
            targetCount = 1,
            sourceAr = "سورة الفرقان: ٧٤",
            sourceEn = "Surah Al-Furqan 25:74",
            meaningAr = "دعاء عباد الرحمن بصلاح الأهل والذرية والإمامة في الخير.",
            meaningEn = "Our Lord, grant us from among our spouses and offspring comfort to our eyes and make us an example for the righteous."
        )
    )

    fun byCategory(category: AdhkarCategory): List<DhikrItem> =
        allAdhkar.filter { it.category == category }

    fun dailyDhikr(dayOfYear: Int): DhikrItem =
        allAdhkar[dayOfYear.coerceAtLeast(0) % allAdhkar.size]

    fun dailyAyah(dayOfYear: Int): DailyAyahItem =
        dailyAyahs[dayOfYear.coerceAtLeast(0) % dailyAyahs.size]
}
