package com.example.wikipediaspeech

import android.content.Context
import org.jsoup.nodes.Document

class WikipediaDocument(val context: Context, val document: Document) {
    fun body(): String {
        return document.getElementById(context.getString(R.string.wikipedia_dom_id_body)).text()
    }

    fun title(): String {
        return document.title().split(context.getString(R.string.wikipedia_title_pivot))[0]
    }

    fun speechText(): String {
        return title() + "\n" + body()
    }
}