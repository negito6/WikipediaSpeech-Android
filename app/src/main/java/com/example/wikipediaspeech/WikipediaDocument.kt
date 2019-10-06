package com.example.wikipediaspeech

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class WikipediaDocument(val document: Document) {
    companion object {
        private const val BODY_DELIMITER = "。"
        private const val TITLE_DELIMITER = " - "
        private const val BODY_DOM_ID = "mw-content-text"
        private val BODY_DOM_UNREADABLE_CLASSES = listOf<String>("mw-editsection", "reference", "toc", "reflist")
        private val BODY_DOM_UNREADABLE_TAGS = listOf<String>("table")
        private val BODY_DOM_UNREADABLE_IDS = listOf<String>("infoboxCountry")
    }

    fun bodyDom(): Element {
        return document.getElementById(BODY_DOM_ID)
    }

    fun readableBody(): String {
        val dom = bodyDom()
        BODY_DOM_UNREADABLE_TAGS.forEach { name ->
            dom.getElementsByTag(name).forEach { elem ->
                elem.remove()
            }
        }
        BODY_DOM_UNREADABLE_CLASSES.forEach { name ->
            dom.getElementsByClass(name).forEach { elem ->
                elem.remove()
            }
        }

        BODY_DOM_UNREADABLE_IDS.forEach { name ->
            dom.getElementById(name).remove()
        }
        return dom.text()
    }

    fun contents(): List<String> {
        return readableBody().split(BODY_DELIMITER)
    }

    fun title(): String {
        return document.title().split(TITLE_DELIMITER)[0]
    }

    fun speechTexts(): List<String> {
        return listOf(title()) + contents()
    }
}