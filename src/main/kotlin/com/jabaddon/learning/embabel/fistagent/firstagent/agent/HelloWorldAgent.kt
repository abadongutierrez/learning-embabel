package com.jabaddon.learning.embabel.fistagent.firstagent.agent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.Planner

@Agent(
    name = "HelloWorldAgent",
    description = "An agent that prints 'Hello, World!' and a greeting message.",
    planner = Planner.GOAP // default
)
class HelloWorldAgent {

    @Action
    fun sayHello(): String {
        return "Hello, World!"
    }

    @AchievesGoal("Prints a greeting")
    @Action
    fun printGreeting(greeting: String) = greeting
}