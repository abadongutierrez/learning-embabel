package com.jabaddon.learning.embabel.fistagent.multiagent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.domain.io.UserInput

data class CountryName(val name: String)

@Agent(
    name = "CountryCapitalAgent",
    description = "An agent that provides the name of the capital city of a given country.",
)
class CountryCapitalAgent(
    private val capitalService: AgentToolService
) {

    @Action
    fun extractCountryName(userInput: UserInput): CountryName? =
        usingDefaultLlm.createObjectIfPossible("""
            Extract the country name from the user input: ${userInput.content}
        """.trimIndent())

    @Action
    fun getCapitalCity(countryName: CountryName): CountryCapitalResponse {
        return capitalService.getCapitalCity(countryName.name)
    }

    @AchievesGoal("Get the capital city of a given country")
    // outputBinding is essential to finish the agent process
    @Action
    fun answer(response: CountryCapitalResponse): String {
        return response.report
    }
}