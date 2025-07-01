package com.jabaddon.learning.embabel.fistagent.firstagent.agent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.fromForm
import com.embabel.ux.form.Text
import com.fasterxml.jackson.annotation.JsonClassDescription
import java.time.LocalDate

@JsonClassDescription("User information")
data class UserInfo(
    @Text(label = "Name")
    val name: String,

    @Text(label = "Birthdate (yyyy-mm-dd)")
    val birthdate: String,
)

@Agent(
    name = "TestFrom",
    description = "An agent that demonstrates the use of fromForm to get user input.")
class TestFormInputAgent {


    @Action
    fun ask(): UserInfo {
        return fromForm("Provide your info?")
    }

    @AchievesGoal("Knowledge of user name")
    @Action
    fun printName(user: UserInfo): Boolean {
        println("User's name is: $user and you are ${computeYearOld(user)} years old.")
        return true
    }

    fun computeYearOld(user: UserInfo): Int {
        val birthDate = LocalDate.parse(user.birthdate)
        val currentDate = LocalDate.now()
        return currentDate.year - birthDate.year - if (currentDate.dayOfYear < birthDate.dayOfYear) 1 else 0
    }
}