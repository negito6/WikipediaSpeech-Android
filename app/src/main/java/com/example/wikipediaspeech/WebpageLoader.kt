package com.example.wikipediaspeech

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class WebpageLoader(private val listener: LoadListener) {
    companion object {
        private const val USER_AGENT_IE11 = "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko"

    }
        private lateinit var loader: Loader
        private var err: Exception? = null

        fun loadUrl(url: String) {
            loader = Loader()
            loader.execute(url)
            listener.onStartLoad()
        }

        fun cancel() {
            loader.cancel(true)
        }

        private fun onFinishLoad(document: Document?) {
            listener.onFinishLoad(document, err)
        }

        interface LoadListener : EventListener {
            fun onStartLoad()
            fun onFinishLoad(document: Document?, err: Exception?)
        }

        private inner class Loader : AsyncTask<String, Int, Document?>() {
            override fun doInBackground(vararg urls: String): Document? {
                try {
                    return Jsoup.connect(urls[0])
                        .userAgent(USER_AGENT_IE11)
                        .followRedirects(true)
                        .get()
                } catch (e: Exception) {
                    err = e
                }
                return null
            }

            override fun onPostExecute(document: Document?) {
                onFinishLoad(document)
            }
        }
}