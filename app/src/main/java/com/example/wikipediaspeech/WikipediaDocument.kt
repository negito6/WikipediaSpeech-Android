package com.example.wikipediaspeech

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class WikipediaDocument(val document: Document) {
    fun bodyDom(): Element {
        return document.getElementById("mw-content-text")
    }

    fun readableBody(): String {
        val dom = bodyDom()
        arrayOf("table", "dl").forEach { name ->
            dom.getElementsByTag(name).forEach { elem ->
                elem.remove()
            }
        }
        arrayOf("mw-editsection", "reference", "toc", "reflist").forEach { name ->
            dom.getElementsByClass(name).forEach { elem ->
                elem.remove()
            }
        }
        return dom.text()
    }

    fun contents(): List<String> {
        return readableBody().split("。")
    }

    fun title(): String {
        return document.title().split(" - ")[0]
    }

    fun speechTexts(): List<String> {
        return listOf(title()) + contents()
    }
}