package com.example.wikipediaspeech

import android.content.Context
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class WikipediaDocument(val context: Context, val document: Document) {
    fun body(): String {
        return bodyDom().text()
    }

    fun bodyDom(): Element {
        return document.getElementById(context.getString(R.string.wikipedia_dom_id_body))
    }

    fun readableBody(): String {
        val dom = bodyDom()
        arrayOf("table", "dl").forEach { name ->
            dom.getElementsByTag(name).forEach { elem ->
                elem.parent().insertChildren(elem.siblingIndex(), elem.childNodes())
                elem.remove()
            }
        }
        arrayOf("mw-editsection", "reference").forEach { name ->
            dom.getElementsByClass(name).forEach { elem ->
                elem.parent().insertChildren(elem.siblingIndex(), elem.childNodes())
                elem.remove()
            }
        }
        return dom.text()
    }

    fun contents(): List<String> {
        return readableBody().split("\n")
    }

    fun title(): String {
        return document.title().split(context.getString(R.string.wikipedia_title_pivot))[0]
    }

    fun speechTexts(): List<String> {
        return listOf(title()) + contents()
    }
}