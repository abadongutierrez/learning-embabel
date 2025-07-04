package com.jabaddon.learning.embabel.web.agents

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObject
import com.embabel.agent.domain.io.UserInput

data class SayHelloName(
    val name: String
)

@Agent(
    name = "SayHelloAgent",
    description = "An agent that says hello",
)
class SayHelloAgent {

    @Action
    fun extractNameFromInput(input: UserInput): SayHelloName {
        return usingDefaultLlm.createObject("""
            Extract the name from the input: $input
            If no name is found, return "World".
        """.trimIndent())
    }

    @AchievesGoal("Greet the user by name")
    @Action(
        outputBinding = "greeting",
    )
    fun sayHello(name: SayHelloName): String {
        return usingDefaultLlm.createObject("""
            Greet the user by name: ${name.name}
            
            Add some random fact related to the user's name.
            
            If the name is "World", just say "Hello, World!".
        """.trimIndent())
    }
}