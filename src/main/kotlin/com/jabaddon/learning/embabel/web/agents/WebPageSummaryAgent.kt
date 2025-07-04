package com.jabaddon.learning.embabel.web.agents

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObject
import com.embabel.agent.domain.io.UserInput
import com.jabaddon.learning.embabel.tools.JSoupTool
import com.jabaddon.learning.embabel.tools.SerperTool

data class SearchKey(
    val query: String
)

data class WebSearchResult(
    val title: String,
    val link: String,
)

data class WebSearchResults(
    val results: List<WebSearchResult>
)


data class SummarizedWebPages(
    val pages: List<WebPageSummarizedContent>
)

data class WebPageSummarizedContent(
    val url: String,
    val summary: String
)

@Agent(
    name = "WebPageSummaryAgent",
    description = "Agent to search web pages and summarize its content",
)
class WebPageSummaryAgent(
    private val serperTool: SerperTool,
    private val jSoupTool: JSoupTool,
) {

    @Action
    fun extractSearchKey(input: UserInput): SearchKey = usingDefaultLlm.createObject("""
            Extract the search query from the user input: ${input.content}
        """.trimIndent())

    @Action
    fun searchWebPages(searchKey: SearchKey): WebSearchResults = usingDefaultLlm.withToolObject(serperTool).createObject("""
            Using web tools search the web for the query: ${searchKey.query}
            
            Just use the first 10 results from the search.
            
            Extract the title and link from each search results.
        """.trimIndent())

    @Action
    fun visitEachPageAndSummarizeContent(webSearchResults: WebSearchResults): SummarizedWebPages =
        usingDefaultLlm.withToolObject(jSoupTool).createObject("""
            For each web page, visit the page and summarize the content.
            
            Use tool JSoupTool to visit and scrape the content of each page.
            
            List the URLs of the pages to visit:
            ${webSearchResults.results.joinToString("\n") { "- ${it.link}" }}
            
            DO NOT visit the pages directly, use the JSoupTool to scrape the content.
            DO NOT use any other tool to scrape the content.
            JUST VISIT the pages listed above and summarize the content of each page.
        """.trimIndent())

    @Action
    @AchievesGoal("Show the summarized web pages for the user")
    fun summarizeWebPages(summarizedWebPages: SummarizedWebPages): SummarizedWebPages = summarizedWebPages
}
