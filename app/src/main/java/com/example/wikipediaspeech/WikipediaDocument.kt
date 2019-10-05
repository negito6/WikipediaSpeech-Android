package com.example.wikipediaspeech

import android.content.Context
import org.jsoup.nodes.Document

class WikipediaDocument(val context: Context, val document: Document) {
    fun body(): String {
        val text = document.getElementById(context.getString(R.string.wikipedia_dom_id_body)).text()

        return text.replace(context.getString(R.string.wikipedia_body_text_edit), "\n")
    }

    fun title(): String {
        return document.title().split(context.getString(R.string.wikipedia_title_pivot))[0]
    }

    fun speechText(): String {
        return title() + "\n" + body()
    }
}