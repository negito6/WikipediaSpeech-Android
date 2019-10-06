package com.example.wikipediaspeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import java.util.*

import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.android.synthetic.main.activity_page.toolbar
import kotlinx.android.synthetic.main.content_page.*
import org.jsoup.nodes.Document
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import android.speech.tts.UtteranceProgressListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.view.View

class PageActivity : AppCompatActivity() {
    companion object {
        private const val SPEECH_ID_PAGE = "page"
        private const val SPEECH_ID_SYSTEM = "system"
        const val EXTRA_PAGE_NAME = "key_page_name"
    }

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var loader: WebpageLoader

    private lateinit var name: String
    private lateinit var wikipediaDocument: WikipediaDocument
    private var speechProgress: Int = -1

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_page)

        val name = intent.getStringExtra(EXTRA_PAGE_NAME)
        val url = "https://ja.wikipedia.org/wiki/" + name

        setToolbar(name)
        setClickLisnter()
        loadPage(url)
    }

    private fun setListView(wikipediaDocument: WikipediaDocument) {
        listView = findViewById(R.id.line_list_view)
        val contents = wikipediaDocument.contents()
        val listItems = arrayOfNulls<String>(contents.size)
        for (i in 0 until contents.size) {
            listItems[i] = contents[i]
        }
        val adapter = ArrayAdapter(this, R.layout.line_item, listItems)
        listView.adapter = adapter
        listView.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: AdapterView<*>, view: View, position: Int, itemId: Long) {
                log("Clicked: " + position.toString())
                if (!textToSpeech.isSpeaking) {
                    activate(position)
                }
            }
        })
    }

    private fun deactivate() {
        // reset background color
    }

    private fun activate(position: Int) {
        deactivate()
        speechProgress = position
        // set background color if position > 0
        runOnUiThread {
            current_index.text = speechProgress.toString() + " / " + wikipediaDocument.speechTexts().size.toString()
        }
    }

    private fun loadPage(url: String) {
        loader = WebpageLoader(object : WebpageLoader.LoadListener {

            override fun onStartLoad() {
            }

            override fun onFinishLoad(document: Document?, err: Exception?) {
                if (document == null) {
                    showError(err?.message)
                    return
                }
                wikipediaDocument = WikipediaDocument(document)
                page_title.text = wikipediaDocument.title()
                current_index.text = "0 / " + wikipediaDocument.speechTexts().size.toString()
                setListView(wikipediaDocument)
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
                    log("progress on Done. id: $utteranceId")
                    if (utteranceId == SPEECH_ID_PAGE) {
                        speechNext()
                    } else {
                        enableStart()
                    }
                }

                override fun onError(utteranceId: String) {
                    log("progress on Error. id: $utteranceId. (parent class)")
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    speechProgress -= 1
                    log("progress on Error. id: $utteranceId, ${errorCode}")
                    enableStart()
                }

                override fun onStop(utteranceId: String, interrupted: Boolean) {
                    log("progress on Stop. id: $utteranceId, interrupted: ${interrupted}")
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
        speechProgress = -1
        startSpeech(getString(R.string.finished), SPEECH_ID_SYSTEM)
        log(wikipediaDocument.readableBody().text())
        enableStart()
    }

    private fun speechNext() {
        activate(speechProgress + 1)
        log("iteretion: ${speechProgress}")
        if (speechProgress == wikipediaDocument.speechTexts().size) {
            finishedSpeech()
            return
        }
        startSpeech(wikipediaDocument.speechTexts()[speechProgress], SPEECH_ID_PAGE)
    }

    private fun startSpeech(text: String, id: String) {
        stopSpeech()
        log("startSpeech(). length: ${text.length}")

        if (text.isNotEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
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
