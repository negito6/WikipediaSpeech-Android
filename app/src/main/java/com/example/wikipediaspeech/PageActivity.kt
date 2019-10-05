package com.example.wikipediaspeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import java.util.*

import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.android.synthetic.main.activity_page.toolbar

class PageActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID = "12345678"
        const val EXTRA_PAGE_NAME = "key_page_name"
    }

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = intent.getStringExtra(EXTRA_PAGE_NAME)

        setContentView(R.layout.activity_page)

        setToolbar()
        setClickLisnter()
        initSpeech()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setTitle(title);
            it.setDisplayShowHomeEnabled(true);
            it.setDisplayHomeAsUpEnabled(true);
        }
    }

    private fun setClickLisnter() {
        play.setOnClickListener { view ->
            startSpeech(title)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
}
