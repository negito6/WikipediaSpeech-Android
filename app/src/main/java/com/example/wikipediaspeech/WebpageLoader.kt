package com.example.wikipediaspeech

import android.content.Context
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class WebpageLoader(private val context: Context, private val listener: LoadListener) {
        private lateinit var loader: Loader

        fun loadUrl(url: String) {
            loader = Loader()
            loader.execute(url)
            listener.onStartLoad()
        }

        fun cancel() {
            loader.cancel(true)
        }

        private fun onFinishLoad(document: Document) {
            listener.onFinishLoad(document)
        }

        interface LoadListener : EventListener {
            fun onStartLoad()
            fun onFinishLoad(document: Document)
        }

        private inner class Loader : AsyncTask<String, Int, Document>() {
            override fun doInBackground(vararg urls: String): Document {
                return Jsoup.connect(urls[0])
                    .userAgent(context.getString(R.string.user_agent_ie11))
                    .followRedirects(true)
                    .get()
            }

            override fun onPostExecute(document: Document) {
                onFinishLoad(document)
            }
        }
}