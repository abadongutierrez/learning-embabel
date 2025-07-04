package com.jabaddon.learning.embabel.tools

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import java.io.IOException

class JSoupTool {
    private val logger = LoggerFactory.getLogger(JSoupTool::class.java)

    @Tool(
        name = "JSoupTool",
        description = "Tool to scrape and extract text from web pages using JSoup"
    )
    fun run(url: String): String {
        logger.info("Running JSoupTool on URL: $url")

        return try {
            // Connect to the URL and get the document
            val document = Jsoup.connect(url).get()

            // Extract just the text content, removing all HTML tags
            document.text()
        } catch (e: IOException) {
            logger.error("Error connecting to URL: $url", e)
            "" // Return empty string on error
        } catch (e: Exception) {
            logger.error("Unexpected error while processing URL: $url", e)
            "" // Return empty string for any other exception
        }
    }
}

