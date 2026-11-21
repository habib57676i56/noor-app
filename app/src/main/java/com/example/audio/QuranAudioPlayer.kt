package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val playingSurah: Int? = null,
    val playingAyah: Int? = null,
    val errorMessage: String? = null
)

class QuranAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    fun playAyah(
        surahNumber: Int,
        ayahNumber: Int,
        audioUrl: String,
        onCompleted: (() -> Unit)? = null
    ) {
        stop()
        _state.value = AudioPlaybackState(
            isPlaying = false,
            isBuffering = true,
            playingSurah = surahNumber,
            playingAyah = ayahNumber,
            errorMessage = null
        )

        try {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener {
                    start()
                    _state.value = _state.value.copy(isPlaying = true, isBuffering = false)
                }
                setOnCompletionListener {
                    _state.value = _state.value.copy(isPlaying = false, isBuffering = false)
                    onCompleted?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = AudioPlaybackState(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Audio playback unavailable offline or network error"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = mp
        } catch (_: Exception) {
            _state.value = AudioPlaybackState(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "Unable to stream recitation"
            )
        }
    }

    fun togglePauseResume() {
        val mp = mediaPlayer ?: return
        try {
            if (mp.isPlaying) {
                mp.pause()
                _state.value = _state.value.copy(isPlaying = false)
            } else {
                mp.start()
                _state.value = _state.value.copy(isPlaying = true)
            }
        } catch (_: Exception) {
            stop()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
            _state.value = AudioPlaybackState()
        }
    }
}
