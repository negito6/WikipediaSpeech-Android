package com.example.wikipediaspeech

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*

class WikipediaDocument(val document: Document) {
    companion object {
        private const val BODY_DELIMITER = "。"
        private const val TITLE_DELIMITER = " - "
        private const val BODY_DOM_ID = "mw-content-text"
        private val BODY_DOM_UNREADABLE_CLASSES = listOf<String>("mw-editsection", "reference", "toc", "reflist", "references-small")
        private val BODY_DOM_UNREADABLE_TAGS = listOf<String>("table")
        private val BODY_DOM_UNREADABLE_IDS = listOf<String>("infoboxCountry")
    }

    fun bodyDom(): Element {
        return document.getElementById(BODY_DOM_ID).children().first()
    }

    fun readableBody(): Element {
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
            val element = dom.getElementById(name)
            if (element != null) {
                element.remove()
            }
        }
        return dom
    }

    fun contents(): Array<String> {
        var elements = listOf<Element>()
        readableBody().children().forEach { element ->
            if (element.tagName().toLowerCase(Locale.ENGLISH) == "ul") {
                elements = elements + element.children()
            } else {
                elements = elements + element
            }
        }
        var list = listOf<String>()
        elements.forEach { element ->
            val text = element.text()
            if (text.isNotEmpty()) {
                list = list + text.split(BODY_DELIMITER).filter { it.isNotEmpty() }
            }
        }
        return list.toTypedArray()
    }

    fun title(): String {
        return document.title().split(TITLE_DELIMITER)[0]
    }

    fun speechTexts(): List<String> {
        return listOf(title()) + contents()
    }
}