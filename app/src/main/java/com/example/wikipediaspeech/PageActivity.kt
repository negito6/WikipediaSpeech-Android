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
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast

class PageActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID = "12345678"
        const val EXTRA_PAGE_NAME = "key_page_name"
    }

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var loader: WebpageLoader

    private lateinit var name: String
    private lateinit var wikipediaDocument: WikipediaDocument
    private var speechProgress: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_page)

        val name = intent.getStringExtra(EXTRA_PAGE_NAME)
        val url = "https://ja.wikipedia.org/wiki/" + name

        setToolbar(name)
        setClickLisnter()
        loadPage(url)
    }

    private fun loadPage(url: String) {
        val context = this
        loader = WebpageLoader(context, object : WebpageLoader.LoadListener {

            override fun onStartLoad() {
            }

            override fun onFinishLoad(document: Document?, err: Exception?) {
                if (document == null) {
                    showError(err?.message)
                    return
                }
                wikipediaDocument = WikipediaDocument(context, document)
                page_title.text = wikipediaDocument.title()
                page_body.text = wikipediaDocument.body()
                initSpeech()
            }
        })
        loader.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.shutdown()
        }
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

    private fun setClickLisnter() {
        play.setOnClickListener {
            startSpeechDocument()
        }
        stop.setOnClickListener {
            pauseSpeech()
        }
    }

    private fun initSpeech() {
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (TextToSpeech.SUCCESS == status) {
                val locale = Locale.JAPANESE
                if (textToSpeech.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.language = locale
                }
                enableStart()
            }
        })

        // https://akira-watson.com/android/tts.html
        val listenerResult =
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    log("progress on Done $utteranceId")
                    speechNext()
                }

                override fun onError(utteranceId: String) {
                    log("progress on Error $utteranceId. (parent class)")
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    speechProgress -= 1
                    log("progress on Error $utteranceId, ${errorCode}")
                    enableStart()
                }

                override fun onStop(utteranceId: String, interrupted: Boolean) {
                    log("progress on Stop $utteranceId, interrupted: ${interrupted}")
                    if (interrupted) {
                        speechProgress -= 1
                    }
                    enableStart()
                }

                override fun onStart(utteranceId: String) {
                    log("progress on Start $utteranceId")
                    disableStart()
                }
            })
        if (listenerResult != TextToSpeech.SUCCESS) {
            log("failed to add utterance progress listener");
        }
    }

    private fun enableStart() {
        runOnUiThread {
            play.show()
            stop.hide()
        }
    }

    private fun disableStart() {
        runOnUiThread {
            play.hide()
            stop.show()
        }
    }

    private fun pauseSpeech() {
        log("pauseSpeech()")
        stopSpeech()
    }

    private fun stopSpeech() {
        if (textToSpeech.isSpeaking) {
            log("is speaking.")
            textToSpeech.stop()
        } else {
            enableStart()
        }
    }

    private fun startSpeechDocument() {
        speechNext()
    }

    private fun finishedSpeech() {
        enableStart()
        speechProgress = -1
        startSpeech(getString(R.string.finished))
    }

    private fun speechNext() {
        speechProgress += 1
        if (speechProgress == wikipediaDocument.speechTexts().size) {
            finishedSpeech()
            return
        }
        log("iteretion: ${speechProgress}")
        startSpeech(wikipediaDocument.speechTexts()[speechProgress])
    }

    private fun startSpeech(text: String) {
        stopSpeech()
        log("startSpeech(). length: ${text.length}")

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

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: getString(R.string.default_error_message), Toast.LENGTH_LONG)
            .show()
    }

    private fun log(message: String) {
        Logger.getLogger("PageActivity").log(LogRecord(Level.INFO, message))
    }
}
