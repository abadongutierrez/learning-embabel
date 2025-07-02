package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObject
import com.embabel.agent.domain.io.UserInput

data class SearchKey(
    val query: String
)

@Agent(
    name = "WebPageSummaryAgent",
    description = "Agent to summarize web pages",
)
class WebPageSummaryAgent {

    @Action
    fun start(input: UserInput): SearchKey {
        return usingDefaultLlm.createObject("""
            Extract the search query from the user input: ${input.content}
        """.trimIndent())
    }

    @Action(
    )
    fun searchWebPages(searchKey: SearchKey): String {
        return usingDefaultLlm. createObject("""
            Search the web for the query: ${searchKey.query}
            Return the first result as a summary.
        """.trimIndent())
    }
}