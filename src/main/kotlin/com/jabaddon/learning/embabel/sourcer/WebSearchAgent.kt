package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.domain.io.UserInput
import com.jabaddon.learning.embabel.tools.SearchResponse
import com.jabaddon.learning.embabel.tools.SerperTool

data class SearchQuery(val query: String)

data class FormattedSearchResults(val content: String)

@Agent(
    name = "WebSearchAgent",
    description = "An agent that performs web searches using the Serper API and returns the results."
)
class WebSearchAgent(
    private val serperTool: SerperTool
) {

    @Action
    fun extractSearchQuery(userInput: UserInput): SearchQuery? =
        usingDefaultLlm.createObjectIfPossible("""
            Extract the search query from the user input: ${userInput.content}
        """.trimIndent())

    @Action
    fun performWebSearch(searchQuery: SearchQuery): SearchResponse {
        return serperTool.search(searchQuery.query)
    }

    @Action
    fun formatResults(searchResponse: SearchResponse): FormattedSearchResults {
        val lineSeparator = System.lineSeparator()

        if (searchResponse.status == "error") {
            return FormattedSearchResults("Error: ${searchResponse.error ?: "Unknown error occurred during search"}")
        }

        if (searchResponse.searchResults.isEmpty()) {
            return FormattedSearchResults("No results found for your search.")
        }

        val stringBuilder = StringBuilder()
        stringBuilder.append("Here are the search results:$lineSeparator$lineSeparator")

        searchResponse.searchResults.forEachIndexed { index, result ->
            stringBuilder.append("${index + 1}. ${result.title}$lineSeparator")
            stringBuilder.append("   URL: ${result.link}$lineSeparator")
            stringBuilder.append("   ${result.snippet}$lineSeparator")
            stringBuilder.append(String(CharArray(50) { '-' }))  // Separator line
            stringBuilder.append("$lineSeparator$lineSeparator")
        }

        return FormattedSearchResults(stringBuilder.toString())
    }

    @AchievesGoal("Perform a web search and return the results")
    @Action
    fun answer(formattedResults: FormattedSearchResults): String {
        return formattedResults.content
    }
}
