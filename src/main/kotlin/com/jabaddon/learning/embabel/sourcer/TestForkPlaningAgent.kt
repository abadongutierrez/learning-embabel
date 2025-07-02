package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.domain.io.UserInput

class StartOutput

class Fork1Output

class Fork2Output

class Fork3Output

data class JoinOutput(val message: String)

@Agent(
    name = "TestForkPlaningAgent",
    description = "A agent that demonstrates forking in planning",
)
class TestForkFlowPlaningAgent {

    @Action
    fun start(input: UserInput, context: OperationContext): StartOutput {
        println("Starting TestForkFlowPlaningAgent with input: $input")
        return StartOutput()
    }

    @Action
    fun fork1(startOutput: StartOutput): Fork1Output {
        println("Processing fork1")
        return Fork1Output()
    }

    @Action
    fun fork2(startOutput: StartOutput): Fork2Output {
        println("Processing fork2")
        return Fork2Output()
    }

    @Action
    fun fork3(startOutput: StartOutput): Fork3Output {
        println("Processing fork3")
        return Fork3Output()
    }


    @Action
    fun join(
        fork1Output: Fork1Output,
        fork2Output: Fork2Output,
        fork3Output: Fork3Output
    ): JoinOutput {
        println("Joining outputs from forks")
        return JoinOutput("Forks completed successfully")
    }

    @AchievesGoal("Demonstrates forking in planning")
    @Action
    fun end(joinOutput: JoinOutput): String {
        return joinOutput.message
    }
}