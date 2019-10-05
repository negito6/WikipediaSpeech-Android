package com.example.wikipediaspeech

import android.content.Context
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class WebpageLoader(private val context: Context, private val listener: LoadListener) {
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
                        .userAgent(context.getString(R.string.user_agent_ie11))
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