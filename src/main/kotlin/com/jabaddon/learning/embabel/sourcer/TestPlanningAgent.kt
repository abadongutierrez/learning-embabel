package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.domain.io.UserInput

@Agent(
    name = "TestPlanningAgent",
    description = "Agent for planning tasks in the Embabel framework",
)
class TestPlanningAgent {

    @Action(
        post = ["started"],
    )
    fun startPlanning(input: UserInput, context: OperationContext) {
        println("SimplePlanAgent has started.")
        context.set("started", true)
    }

    @AchievesGoal("Show plan to the user")
    @Action(
        pre = ["started"],
    )
    fun showPlan() {
        println("SimplePlanAgent has performed an action.")
    }
}
