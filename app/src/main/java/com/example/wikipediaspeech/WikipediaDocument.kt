package com.example.wikipediaspeech

import android.content.Context
import org.jsoup.nodes.Document

class WikipediaDocument(val context: Context, val document: Document) {
    fun body(): String {
        return document.getElementById(context.getString(R.string.wikipedia_dom_id_body)).text()
    }

    fun bodyBlocks(): List<String> {
        return body().replace(context.getString(R.string.wikipedia_body_text_edit), "\n").split("\n")
    }

    fun title(): String {
        return document.title().split(context.getString(R.string.wikipedia_title_pivot))[0]
    }

    fun speechTexts(): List<String> {
        return listOf(title()) + bodyBlocks()
    }
}