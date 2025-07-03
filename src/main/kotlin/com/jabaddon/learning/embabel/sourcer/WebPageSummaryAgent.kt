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

@Agent(
    name = "WebPageSummaryAgent",
    description = "Agent to summarize web pages",

)
class WebPageSummaryAgent {

    @Action
    fun extractSearchKey(input: UserInput): SearchKey {
        return usingDefaultLlm.createObject("""
            Extract the search query from the user input: ${input.content}
        """.trimIndent())
    }

    @Action(
        toolGroups = ["web", "math"],
    )
    fun searchWebPages(searchKey: SearchKey): List<WebSearchResult> {
        return usingDefaultLlm.createObject("""
            Using web tools search the web for the query: ${searchKey.query}
            
            Extract the title, link, and snippet from each search results.
        """.trimIndent())
    }

    @Action
    @AchievesGoal(description = "Summarized the web pages for the user")
    fun summarizeWebPages(webSearchResults: List<WebSearchResult>): List<WebSearchResult> {
        return webSearchResults
    }
}
