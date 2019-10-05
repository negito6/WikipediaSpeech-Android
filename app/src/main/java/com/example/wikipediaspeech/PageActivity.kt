package com.example.wikipediaspeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import java.util.*

import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.android.synthetic.main.activity_page.toolbar
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.nodes.Document
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

class PageActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID = "12345678"
        const val EXTRA_PAGE_NAME = "key_page_name"
    }

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var loader: WebpageLoader

    private lateinit var name: String
    private lateinit var wikipediaDocument: WikipediaDocument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_page)

        val name = intent.getStringExtra(EXTRA_PAGE_NAME)
        val url = "https://ja.wikipedia.org/wiki/" + name

        setToolbar(name)
        setClickLisnter()
        initSpeech()
        loadPage(url)
    }

    private fun loadPage(url: String) {
        val context = this
        loader = WebpageLoader(context, object : WebpageLoader.LoadListener {

            override fun onStartLoad() {
                textToSpeech.stop()
            }

            override fun onFinishLoad(document: Document) {
                wikipediaDocument = WikipediaDocument(context, document)
                play.show()
                page_title.text = wikipediaDocument.title()
                page_body.text = wikipediaDocument.body()
            }
        })
        loader.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
        loader.cancel()
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setTitle(title)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun speechText(): String {
        return wikipediaDocument.speechText()
    }

    private fun setClickLisnter() {
        play.setOnClickListener {
            startSpeech(speechText())
        }
        stop.setOnClickListener {
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
        log("stopSpeech()")
        play.show()
        stop.hide()
        if (textToSpeech.isSpeaking) {
            log("is speaking.")
            textToSpeech.stop()
        }
    }

    private fun startSpeech(text: String) {
        stopSpeech()
        log("startSpeech(). length: ${text.length}")

        if (text.isNotEmpty()) {
            play.hide()
            stop.show()
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

    private fun log(message: String) {
        Logger.getLogger("PageActivity").log(LogRecord(Level.INFO, message))
    }
}
