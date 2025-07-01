package com.jabaddon.learning.embabel.fistagent.multiagent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.domain.io.UserInput

data class CityName(val name: String)

@Agent(
    name = "CurrentTimeAgent",
    description = "An agent that provides the current time in a specified city.",
)
class CurrentTimeAgent(val agentToolService: AgentToolService) {

    @Action
    fun extractCityName(userInput: UserInput): CityName? =
        usingDefaultLlm.createObjectIfPossible("""
            Extract the name of the city from the user input.
            
            User input: "${userInput.content}"
        """.trimIndent())

    @Action
    fun getCurrentTime(cityName: CityName): CurrentTimeResponse = agentToolService.getCurrentTime(cityName.name)


    @AchievesGoal("Provide the current time in a specified city.")
    @Action
    fun answer(response: CurrentTimeResponse): String {
        return response.report
    }
}