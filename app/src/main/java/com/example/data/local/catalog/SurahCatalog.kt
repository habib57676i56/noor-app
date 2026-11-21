package com.example.data.local.catalog

import com.example.domain.model.QuranReciter
import com.example.domain.model.QuranVerse
import com.example.domain.model.SurahMeta

object SurahCatalog {

    val allSurahs: List<SurahMeta> = listOf(
        SurahMeta(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, true, 1, 1),
        SurahMeta(2, "البقرة", "Al-Baqarah", "The Cow", 286, false, 1, 1),
        SurahMeta(3, "آل عمران", "Aal-E-Imran", "Family of Imran", 200, false, 3, 5),
        SurahMeta(4, "النساء", "An-Nisa", "The Women", 176, false, 4, 8),
        SurahMeta(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, false, 6, 11),
        SurahMeta(6, "الأنعام", "Al-An'am", "The Cattle", 165, true, 7, 13),
        SurahMeta(7, "الأعراف", "Al-A'raf", "The Heights", 206, true, 8, 16),
        SurahMeta(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, false, 9, 18),
        SurahMeta(9, "التوبة", "At-Tawbah", "The Repentance", 129, false, 10, 19),
        SurahMeta(10, "يونس", "Yunus", "Jonah", 109, true, 11, 21),
        SurahMeta(11, "هود", "Hud", "Hud", 123, true, 11, 22),
        SurahMeta(12, "يوسف", "Yusuf", "Joseph", 111, true, 12, 24),
        SurahMeta(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, false, 13, 25),
        SurahMeta(14, "إبراهيم", "Ibrahim", "Abraham", 52, true, 13, 26),
        SurahMeta(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, true, 14, 27),
        SurahMeta(16, "النحل", "An-Nahl", "The Bee", 128, true, 14, 27),
        SurahMeta(17, "الإسراء", "Al-Isra", "The Night Journey", 111, true, 15, 29),
        SurahMeta(18, "الكهف", "Al-Kahf", "The Cave", 110, true, 15, 30),
        SurahMeta(19, "مريم", "Maryam", "Mary", 98, true, 16, 31),
        SurahMeta(20, "طه", "Ta-Ha", "Ta-Ha", 135, true, 16, 32),
        SurahMeta(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, true, 17, 33),
        SurahMeta(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, false, 17, 34),
        SurahMeta(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, true, 18, 35),
        SurahMeta(24, "النور", "An-Nur", "The Light", 64, false, 18, 36),
        SurahMeta(25, "الفرقان", "Al-Furqan", "The Criterion", 77, true, 18, 36),
        SurahMeta(26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, true, 19, 37),
        SurahMeta(27, "النمل", "An-Naml", "The Ant", 93, true, 19, 38),
        SurahMeta(28, "القصص", "Al-Qasas", "The Stories", 88, true, 20, 39),
        SurahMeta(29, "العنكبوت", "Al-Ankabut", "The Spider", 69, true, 20, 40),
        SurahMeta(30, "الروم", "Ar-Rum", "The Romans", 60, true, 21, 41),
        SurahMeta(31, "لقمان", "Luqman", "Luqman", 34, true, 21, 41),
        SurahMeta(32, "السجدة", "As-Sajdah", "The Prostration", 30, true, 21, 42),
        SurahMeta(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, false, 21, 42),
        SurahMeta(34, "سبأ", "Saba", "Sheba", 54, true, 22, 43),
        SurahMeta(35, "فاطر", "Fatir", "Originator", 45, true, 22, 44),
        SurahMeta(36, "يس", "Ya-Sin", "Ya Sin", 83, true, 22, 44),
        SurahMeta(37, "الصافات", "As-Saffat", "Those Who Set the Ranks", 182, true, 23, 45),
        SurahMeta(38, "ص", "Sad", "The Letter Sad", 88, true, 23, 46),
        SurahMeta(39, "الزمر", "Az-Zumar", "The Troops", 75, true, 23, 46),
        SurahMeta(40, "غافر", "Ghafir", "The Forgiver", 85, true, 24, 47),
        SurahMeta(41, "فصلت", "Fussilat", "Explained in Detail", 54, true, 24, 48),
        SurahMeta(42, "الشورى", "Ash-Shura", "The Consultation", 53, true, 25, 49),
        SurahMeta(43, "الزخرف", "Az-Zukhruf", "Ornaments of Gold", 89, true, 25, 49),
        SurahMeta(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, true, 25, 50),
        SurahMeta(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, true, 25, 50),
        SurahMeta(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, true, 26, 51),
        SurahMeta(47, "محمد", "Muhammad", "Muhammad", 38, false, 26, 51),
        SurahMeta(48, "الفتح", "Al-Fath", "The Victory", 29, false, 26, 52),
        SurahMeta(49, "الحجرات", "Al-Hujurat", "The Rooms", 18, false, 26, 52),
        SurahMeta(50, "ق", "Qaf", "The Letter Qaf", 45, true, 26, 52),
        SurahMeta(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, true, 26, 52),
        SurahMeta(52, "الطور", "At-Tur", "The Mount", 49, true, 27, 53),
        SurahMeta(53, "النجم", "An-Najm", "The Star", 62, true, 27, 53),
        SurahMeta(54, "القمر", "Al-Qamar", "The Moon", 55, true, 27, 53),
        SurahMeta(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, false, 27, 54),
        SurahMeta(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, true, 27, 54),
        SurahMeta(57, "الحديد", "Al-Hadid", "The Iron", 29, false, 27, 54),
        SurahMeta(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", 22, false, 28, 55),
        SurahMeta(59, "الحشر", "Al-Hashr", "The Exile", 24, false, 28, 55),
        SurahMeta(60, "الممتحنة", "Al-Mumtahanah", "She That Is to Be Examined", 13, false, 28, 55),
        SurahMeta(61, "الصف", "As-Saff", "The Ranks", 14, false, 28, 55),
        SurahMeta(62, "الجمعة", "Al-Jumu'ah", "The Congregation", 11, false, 28, 56),
        SurahMeta(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, false, 28, 56),
        SurahMeta(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, false, 28, 56),
        SurahMeta(65, "الطلاق", "At-Talaq", "The Divorce", 12, false, 28, 56),
        SurahMeta(66, "التحريم", "At-Tahrim", "The Prohibition", 12, false, 28, 56),
        SurahMeta(67, "الملك", "Al-Mulk", "The Sovereignty", 30, true, 29, 57),
        SurahMeta(68, "القلم", "Al-Qalam", "The Pen", 52, true, 29, 57),
        SurahMeta(69, "الحاقة", "Al-Haqqah", "The Reality", 52, true, 29, 57),
        SurahMeta(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, true, 29, 57),
        SurahMeta(71, "نوح", "Nuh", "Noah", 28, true, 29, 57),
        SurahMeta(72, "الجن", "Al-Jinn", "The Jinn", 28, true, 29, 58),
        SurahMeta(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, true, 29, 58),
        SurahMeta(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, true, 29, 58),
        SurahMeta(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, true, 29, 58),
        SurahMeta(76, "الإنسان", "Al-Insan", "The Man", 31, false, 29, 58),
        SurahMeta(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, true, 29, 58),
        SurahMeta(78, "النبأ", "An-Naba", "The Tidings", 40, true, 30, 59),
        SurahMeta(79, "النازعات", "An-Nazi'at", "Those Who Drag Forth", 46, true, 30, 59),
        SurahMeta(80, "عبس", "Abasa", "He Frowned", 42, true, 30, 59),
        SurahMeta(81, "التكوير", "At-Takwir", "The Overthrowing", 29, true, 30, 59),
        SurahMeta(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, true, 30, 59),
        SurahMeta(83, "المطففين", "Al-Mutaffifin", "The Defrauding", 36, true, 30, 59),
        SurahMeta(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", 25, true, 30, 59),
        SurahMeta(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, true, 30, 59),
        SurahMeta(86, "الطارق", "At-Tariq", "The Nightcomer", 17, true, 30, 59),
        SurahMeta(87, "الأعلى", "Al-A'la", "The Most High", 19, true, 30, 60),
        SurahMeta(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, true, 30, 60),
        SurahMeta(89, "الفجر", "Al-Fajr", "The Dawn", 30, true, 30, 60),
        SurahMeta(90, "البلد", "Al-Balad", "The City", 20, true, 30, 60),
        SurahMeta(91, "الشمس", "Ash-Shams", "The Sun", 15, true, 30, 60),
        SurahMeta(92, "الليل", "Al-Layl", "The Night", 21, true, 30, 60),
        SurahMeta(93, "الضحى", "Ad-Duhaa", "The Morning Hours", 11, true, 30, 60),
        SurahMeta(94, "الشرح", "Ash-Sharh", "The Relief", 8, true, 30, 60),
        SurahMeta(95, "التين", "At-Tin", "The Fig", 8, true, 30, 60),
        SurahMeta(96, "العلق", "Al-Alaq", "The Clot", 19, true, 30, 60),
        SurahMeta(97, "القدر", "Al-Qadr", "The Power", 5, true, 30, 60),
        SurahMeta(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, false, 30, 60),
        SurahMeta(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, false, 30, 60),
        SurahMeta(100, "العاديات", "Al-Adiyat", "The Courser", 11, true, 30, 60),
        SurahMeta(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, true, 30, 60),
        SurahMeta(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", 8, true, 30, 60),
        SurahMeta(103, "العصر", "Al-Asr", "The Declining Day", 3, true, 30, 60),
        SurahMeta(104, "الهمزة", "Al-Humazah", "The Traducer", 9, true, 30, 60),
        SurahMeta(105, "الفيل", "Al-Fil", "The Elephant", 5, true, 30, 60),
        SurahMeta(106, "قريش", "Quraysh", "Quraysh", 4, true, 30, 60),
        SurahMeta(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", 7, true, 30, 60),
        SurahMeta(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, true, 30, 60),
        SurahMeta(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, true, 30, 60),
        SurahMeta(110, "النصر", "An-Nasr", "The Divine Support", 3, false, 30, 60),
        SurahMeta(111, "المسد", "Al-Masad", "The Palm Fiber", 5, true, 30, 60),
        SurahMeta(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, true, 30, 60),
        SurahMeta(113, "الفلق", "Al-Falaq", "The Daybreak", 5, true, 30, 60),
        SurahMeta(114, "الناس", "An-Nas", "Mankind", 6, true, 30, 60)
    )

    fun findSurah(number: Int): SurahMeta =
        allSurahs.firstOrNull { it.number == number } ?: allSurahs.first()

    fun audioUrlForGlobalAyah(globalNumber: Int, reciter: QuranReciter): String =
        "https://cdn.islamic.network/quran/audio/128/${reciter.cdnSlug}/$globalNumber.mp3"

    /**
     * Verified bundled offline Uthmani text + concise Al-Muyassar Tafsir for essential Surahs
     * so they work instantaneously offline on first launch. All other 114 Surahs are fetched
     * from authoritative AlQuran Cloud (quran-uthmani + ar.muyassar) and cached permanently in Room.
     */
    fun getBundledSurahVerses(surahNumber: Int, reciter: QuranReciter): List<QuranVerse>? {
        val raw = bundledSurahs[surahNumber] ?: return null
        return raw.map { v ->
            QuranVerse(
                surahNumber = surahNumber,
                numberInSurah = v.numberInSurah,
                globalNumber = v.globalNumber,
                textUthmani = v.textUthmani,
                tafsirAr = v.tafsirAr,
                audioUrl = audioUrlForGlobalAyah(v.globalNumber, reciter)
            )
        }
    }

    private data class RawVerse(
        val numberInSurah: Int,
        val globalNumber: Int,
        val textUthmani: String,
        val tafsirAr: String
    )

    private val bundledSurahs: Map<Int, List<RawVerse>> = mapOf(
        1 to listOf(
            RawVerse(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "أبتدئ قراءة القرآن باسم الله مستعيناً به؛ اللهِ المعبود بحق، الرحمنِ ذي الرحمة العامة، الرحيمِ بالمؤمنين."),
            RawVerse(2, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الثناء الكامل لله وحده، مالك جميع الخلائق ومدبر شؤونهم ومربيهم بنعمه."),
            RawVerse(3, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "ذو الرحمة الواسعة التي شملت كل شيء، والرحمة الخاصة بالمؤمنين."),
            RawVerse(4, 4, "مَالِكِ يَوْمِ الدِّينِ", "المتفرد بالملك والحكم يوم الجزاء والحساب."),
            RawVerse(5, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "نخصك وحدك بالعبادة والطاعة، ونستعين بك وحدك في جميع أمورنا."),
            RawVerse(6, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "دلّنا وأرشدنا ووفقنا إلى الطريق الواضح القويم الذي لا اعوجاج فيه وهو الإسلام."),
            RawVerse(7, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "طريق النبيين والصديقين والشهداء والصالحين، غير طريق من علموا الحق وتركوه، ولا من تاهوا عنه بجهل.")
        ),
        94 to listOf(
            RawVerse(1, 6091, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "ألم نوسّع لك يا محمد صدرك بنور الإسلام والهداية والسكينة؟"),
            RawVerse(2, 6092, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "وحططنا عنك حملك الثقيل الذي كان يرهقك."),
            RawVerse(3, 6093, "الَّذِي أَنقَضَ ظَهْرَكَ", "الذي أثقل ظهرك قبل النبوة فأذهبناه عنك."),
            RawVerse(4, 6094, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "وجعلناك مرفوع الشأن في الدنيا والآخرة يُذكر اسمك مع اسم الله في الأذان والشهادة."),
            RawVerse(5, 6095, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "فلا يثنينّك أذى عن الدعوة فإن مع الضيق فرجاً قريباً."),
            RawVerse(6, 6096, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "تأكيد لبشارة الله أن العسر الواحد يعقبه يسران."),
            RawVerse(7, 6097, "فَإِذَا فَرَغْتَ فَانصَبْ", "فإذا فرغت من أشغالك فاجتهد في العبادة والدعاء."),
            RawVerse(8, 6098, "وَإِلَىٰ رَبِّكَ فَارْغَب", "واجعل رغبتك وتوجهك كله إلى ربك وحده.")
        ),
        97 to listOf(
            RawVerse(1, 6126, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "إنا أنزلنا القرآن الكريم في ليلة الشرف والفضل العظيم."),
            RawVerse(2, 6127, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "وما أعلمك يا محمد بعظمة هذه الليلة وفضلها الجليل؟"),
            RawVerse(3, 6128, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "العمل الصالح فيها أفضل من عبادة ألف شهر ليس فيها ليلة القدر."),
            RawVerse(4, 6129, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "ينزل الملائكة وجبريل عليه السلام فيها بكثرة بالخير والبركة بأمر ربهم."),
            RawVerse(5, 6130, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "هي أمن وسلام وخير خالص حتى طلوع الفجر.")
        ),
        103 to listOf(
            RawVerse(1, 6177, "وَالْعَصْرِ", "أقسم الله بالدهر والزمن لما فيه من العبر وتقلبات الأحوال."),
            RawVerse(2, 6178, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "إن جنس الإنسان لفي هلاك ونقصان وخسارة."),
            RawVerse(3, 6179, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "إلا المؤمنين العاملين للصالحات الذين يوصي بعضهم بعضاً بالحق والصبر على طاعة الله.")
        ),
        108 to listOf(
            RawVerse(1, 6205, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "إنا أعطيناك يا محمد الخير الكثير ومنه نهر الكوثر في الجنة."),
            RawVerse(2, 6206, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "فأخلص صلاتك كلها لربك واذبح نسكك له وحده شكراً لنعمته."),
            RawVerse(3, 6207, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "إن مبغضك هو المنقطع ذكره وخيره في الدنيا والآخرة.")
        ),
        109 to listOf(
            RawVerse(1, 6208, "قُلْ يَا أَيُّهَا الْكَافِرُونَ", "قل يا محمد للذين كفروا بالله ورسوله."),
            RawVerse(2, 6209, "لَا أَعْبُدُ مَا تَعْبُدُونَ", "لا أعبد الأصنام والآلهة الباطلة التي تعبدونها."),
            RawVerse(3, 6210, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "ولا أنتم عابدون الله وحده الذي أعبده."),
            RawVerse(4, 6211, "وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ", "ولن أكون عابداً يوماً ما عبدتم من دون الله."),
            RawVerse(5, 6212, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "ولا أنتم عابدون معبودي ما دمتم على شرككم."),
            RawVerse(6, 6213, "لَكُمْ دِينُكُمْ وَلِيَ دِينِ", "لكم دينكم الذي اخترتموه، ولي ديني الذي لا أبغي غيره وهو الإسلام.")
        ),
        110 to listOf(
            RawVerse(1, 6214, "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ", "إذا تم لك يا محمد نصر الله على أعدائك وفتح مكة المكرمة."),
            RawVerse(2, 6215, "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا", "ورأيت الناس يقبلون على الإسلام جماعات كثيرة."),
            RawVerse(3, 6216, "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا", "فنزّه ربك حامداً له شاكراً على نعمته، واطلب مغفرته فإنه كثير التوبة على عباده.")
        ),
        112 to listOf(
            RawVerse(1, 6222, "قُلْ هُوَ اللَّهُ أَحَدٌ", "قل يا محمد: هو الله المتفرد بالألوهية والربوبية والأسماء الحسنى، لا شريك له."),
            RawVerse(2, 6223, "اللَّهُ الصَّمَدُ", "الله المقصود وحده في قضاء الحوائج، الكامل في صفاته الغني عن كل ما سواه."),
            RawVerse(3, 6224, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "ليس له ولد ولا والد، منزه عن المجانسة والمماثلة."),
            RawVerse(4, 6225, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "وليس له مثيل ولا نظير ولا مكافئ من خلقه.")
        ),
        113 to listOf(
            RawVerse(1, 6226, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "قل يا محمد: ألتجئ وأعتصم برب الصبح الذي يفلق ظلام الليل بنوره."),
            RawVerse(2, 6227, "مِن شَرِّ مَا خَلَقَ", "من أذى جميع المخلوقات وشرها."),
            RawVerse(3, 6228, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "ومن شر الليل الشديد الظلمة إذا أقبل وانتشر ما فيه من المؤذيات."),
            RawVerse(4, 6229, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "ومن شر السواحر اللاتي ينفثن في عقد الخيوط لإلحاق الأذى."),
            RawVerse(5, 6230, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "ومن شر كل حاسد يتمنى زوال النعمة إذا أظهر حسده وعمل بمقتضاه.")
        ),
        114 to listOf(
            RawVerse(1, 6231, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "قل: ألتجئ وأتحصن برب الناس وخالقهم ومدبر شؤونهم."),
            RawVerse(2, 6232, "مَلِكِ النَّاسِ", "المتصرف في شؤونهم الغني عنهم."),
            RawVerse(3, 6233, "إِلَٰهِ النَّاسِ", "المعبود بحق لا معبود سواهُ."),
            RawVerse(4, 6234, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "من أذى الشيطان الذي يلقي الوسوسة في الصدور ويخنس متراجعاً عند ذكر الله."),
            RawVerse(5, 6235, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "الذي يبث الشك والشر في قلوب البشر."),
            RawVerse(6, 6236, "مِنَ الْجِنَّةِ وَالنَّاسِ", "سواء أكان من شياطين الجن أم من شياطين الإنس.")
        )
    )
}
