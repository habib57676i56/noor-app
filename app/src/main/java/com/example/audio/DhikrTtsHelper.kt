package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class DhikrTtsHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _speakingDhikrId = MutableStateFlow<String?>(null)
    val speakingDhikrId: StateFlow<String?> = _speakingDhikrId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val res = tts?.setLanguage(Locale("ar"))
                isReady = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(0.88f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _speakingDhikrId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        if (_speakingDhikrId.value == utteranceId) {
                            _speakingDhikrId.value = null
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _speakingDhikrId.value = null
                    }
                })
            }
        }
    }

    fun speakOrStop(dhikrId: String, arabicText: String) {
        if (_speakingDhikrId.value == dhikrId) {
            stop()
            return
        }
        val engine = tts ?: return
        if (!isReady) {
            engine.setLanguage(Locale("ar"))
        }
        _speakingDhikrId.value = dhikrId
        engine.speak(arabicText, TextToSpeech.QUEUE_FLUSH, null, dhikrId)
    }

    fun stop() {
        tts?.stop()
        _speakingDhikrId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
