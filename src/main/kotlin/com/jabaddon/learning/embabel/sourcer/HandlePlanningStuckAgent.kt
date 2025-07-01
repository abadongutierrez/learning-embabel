package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.common.StuckHandler
import com.embabel.agent.api.common.StuckHandlerResult
import com.embabel.agent.api.common.StuckHandlingResultCode
import com.embabel.agent.core.AgentProcess
import java.time.LocalDate

data class StartSignal(
    val content: String? = null
)

data class PlanStart(val date: LocalDate)

//@Agent(description = "Agent for planning tasks in the Embabel framework")
class HandlePlanningStuckAgent : StuckHandler {

    @Action
    fun startPlanning(startSignal: StartSignal): PlanStart {
        println("Starting planning process...")
        // Simulate some planning logic
        return PlanStart(date = LocalDate.now())
    }

//    @Condition(name = "startPlanningWasExecuted")
//    fun startPlanningWasExecuted(context: OperationContext): Boolean {
//        // Check if the planning process has started
//        println("Checking if planning process was executed...")
//        val condition = context.get("startPlanningExecuted")
//        return condition as? Boolean ?: false
//    }

    @Action
    @AchievesGoal("Show a plan to the user")
    fun showPlan(planStart: PlanStart): String {
        println("Showing the plan to the user...")
        return "Plan started on ${planStart.date}. Please review the plan."
    }

    override fun handleStuck(agentProcess: AgentProcess): StuckHandlerResult {
        println("Handling stuck process for agent: ${agentProcess.agent.name}")
        agentProcess.addObject(StartSignal("start planning again"))
        return StuckHandlerResult(
            handler = null,
            message = "The planning process is stuck. Retrying...",
            code = StuckHandlingResultCode.REPLAN,
            agentProcess = agentProcess
        )
    }
}

