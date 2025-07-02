package com.jabaddon.learning.embabel.tools

import org.jsoup.Jsoup

class JSoupTool(val url: String) {

    fun run(): String {
        // Connect to the URL and get the document
        val document = Jsoup.connect(url).get()

        // Extract just the text content, removing all HTML tags
        return document.text()
    }
}

