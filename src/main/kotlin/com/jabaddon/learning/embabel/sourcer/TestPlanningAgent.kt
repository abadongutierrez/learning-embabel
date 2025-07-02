package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.Condition
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.domain.io.UserInput
import org.kohsuke.github.GHEventPayload

class TPAStartPlanningOutput()

@Agent(
    name = "TestPlanningAgent",
    description = "Agent for planning tasks",
)
class TestPlanningAgent {

    @Action(
        outputBinding = "startPlanningOutput",
    )
    fun startPlanning(userInput: UserInput): TPAStartPlanningOutput {
        println("SimplePlanAgent has started.")
        return TPAStartPlanningOutput()
    }

    @Condition(name = "started")
    fun started(context: OperationContext): Boolean {
        println("Checking if planning has started...")
        return context.get("startPlanningOutput") == "yes"
    }

    @AchievesGoal("Show plan to the user")
    @Action(
        pre = ["started"],
    )
    fun showPlan(input: TPAStartPlanningOutput): String {
        println("Showing plan to the user...")
        return "SimplePlanAgent has finished."
    }
}
