package com.example.wikipediaspeech

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.speech.tts.TextToSpeech

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID = "12345678"
    }

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setClickLisnter()
        initSpeech()
    }

    private fun setClickLisnter() {
        play.setOnClickListener { view ->
            talkText(getString(R.string.no_selection))
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

    private fun talkText(text: String) {
        if (text.isNotEmpty()) {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }

            // start speech
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, SPEECH_ID)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
