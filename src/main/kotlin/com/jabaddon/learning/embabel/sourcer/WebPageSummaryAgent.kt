package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObject
import com.embabel.agent.domain.io.UserInput

data class SearchKey(
    val query: String
)

data class WebSearchResult(
    val title: String,
    val link: String,
    val snippet: String
)

data class WebSearchResults(
    val results: List<WebSearchResult>
)

@Agent(
    name = "WebPageSummaryAgent",
    description = "Agent to summarize web pages",
)
class WebPageSummaryAgent {

    @Action
    fun extractSearchKey(input: UserInput): SearchKey = usingDefaultLlm.createObject("""
            Extract the search query from the user input: ${input.content}
        """.trimIndent())

    @Action(
        toolGroups = ["web"],
    )
    fun searchWebPages(searchKey: SearchKey): WebSearchResults = usingDefaultLlm.createObject("""
            Using web tools search the web for the query: ${searchKey.query}
            
            Just use the first 10 results from the search.
            
            Extract the title, link, and snippet from each search results.
        """.trimIndent())

    @Action
    @AchievesGoal("Summarized the web pages for the user")
    fun summarizeWebPages(webSearchResults: WebSearchResults): WebSearchResults = webSearchResults
}
