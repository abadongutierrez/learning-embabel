package com.jabaddon.learning.embabel.tools

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * Response from a web search query
 */
data class SearchResponse(
    val status: String,
    val searchResults: List<SearchResult>,
    val error: String? = null
)

/**
 * Represents an individual search result
 */
data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String
)

/**
 * Service for performing web searches using the Serper API
 */
class SerperTool(private val serperApiKey: String) {

    private val restTemplate = RestTemplate()
    private val objectMapper = ObjectMapper()
    private val serperApiUrl = "https://google.serper.dev/search"
    private val logger = LoggerFactory.getLogger(SerperTool::class.java)

    init {
        if (serperApiKey.isBlank()) {
            logger.warn("SerperTool initialized with blank API key. Searches will fail.")
        } else {
            // Log only first few characters to avoid exposing full API key
            val maskedKey = if (serperApiKey.length > 4) {
                serperApiKey.substring(0, 4) + "..." + serperApiKey.takeLast(2)
            } else {
                "***"
            }
            logger.info("SerperTool initialized with API key: ${maskedKey}")
        }
    }

    /**
     * Performs a web search using the provided query
     */
    fun search(query: String, numResults: Int = 10): SearchResponse {
        return when {
            serperApiKey.isBlank() -> SearchResponse(
                status = "error",
                searchResults = emptyList(),
                error = "Serper API key is not configured."
            )
            else -> {
                try {
                    logger.debug("Performing search with query: '$query', requesting $numResults results")

                    val headers = HttpHeaders()
                    headers.contentType = MediaType.APPLICATION_JSON

                    headers.set("X-API-KEY", serperApiKey)

                    val request = HttpEntity(mapOf("q" to query, "num" to numResults), headers)
                    val response = restTemplate.exchange(
                        serperApiUrl,
                        HttpMethod.POST,
                        request,
                        String::class.java
                    )

                    logger.debug("Search request successful, processing results")
                    val results = parseSearchResults(response.body, numResults)

                    SearchResponse(
                        status = "success",
                        searchResults = results
                    )
                } catch (e: Exception) {
                    logger.error("Error performing search with Serper API: ${e.message}")
                    SearchResponse(
                        status = "error",
                        searchResults = emptyList(),
                        error = "Error performing search: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Parse the response from Serper API
     */
    private fun parseSearchResults(responseBody: String?, maxResults: Int): List<SearchResult> {
        if (responseBody == null) return emptyList()

        val jsonResponse = objectMapper.readTree(responseBody)
        val organicResults = jsonResponse.path("organic")

        val results = mutableListOf<SearchResult>()

        for (i in 0 until minOf(organicResults.size(), maxResults)) {
            val result = organicResults[i]
            results.add(
                SearchResult(
                    title = result.path("title").asText(""),
                    link = result.path("link").asText(""),
                    snippet = result.path("snippet").asText("")
                )
            )
        }

        return results
    }
}





