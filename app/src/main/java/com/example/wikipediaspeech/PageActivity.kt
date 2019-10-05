package com.example.wikipediaspeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.*

class PageActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID = "12345678"
        const val EXTRA_PAGE_NAME = "key_page_name"
    }

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        intent.getStringExtra(EXTRA_PAGE_NAME)

        setClickLisnter()
        initSpeech()
    }

    private fun setClickLisnter() {
        play.setOnClickListener { view ->
            startSpeech(getString(R.string.no_selection))
        }
        stop.setOnClickListener { view ->
            stopSpeech()
        }
    }

    private fun initSpeech() {
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (TextToSpeech.SUCCESS == status) {
                val locale = Locale.JAPANESE
                if (textToSpeech.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.language = locale
                }
            }
        })
    }

    private fun stopSpeech() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    private fun startSpeech(text: String) {
        stopSpeech()

        if (text.isNotEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, SPEECH_ID)
        }
    }
}
