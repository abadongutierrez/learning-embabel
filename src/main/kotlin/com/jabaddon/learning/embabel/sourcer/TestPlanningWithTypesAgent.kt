package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent

data class StartPlanningOutput(
    val message: String
)

@Agent(
    name = "TestPlanningWithTypesAgent",
    description = "An agent that  planning with types",
)
class TestPlanningWithTypesAgent {
    @Action
    fun startPlanning(): StartPlanningOutput {
        println("SimplePlanAgent has started.")
        return StartPlanningOutput(
            message = "Planning has started successfully."
        )
    }

    @AchievesGoal("Show plan to the user")
    @Action
    fun showPlan(startPlanningOutput: StartPlanningOutput): String {
        return "SimplePlanAgent has performed an action. ${startPlanningOutput.message}"
    }
}
