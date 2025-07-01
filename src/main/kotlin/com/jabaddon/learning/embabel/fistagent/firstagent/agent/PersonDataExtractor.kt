package com.jabaddon.learning.embabel.fistagent.firstagent.agent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.Condition
import com.embabel.agent.api.annotation.fromForm
import com.embabel.agent.api.annotation.using
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.domain.io.UserInput
import com.embabel.common.ai.model.LlmOptions
import com.embabel.common.ai.model.ModelSelectionCriteria.Companion.Auto
import com.embabel.ux.form.Text

data class PersonName(
    val content: String,
)

data class PersonLastName(
    val content: String,
)

data class PersonData(
    @Text(label = "Name")
    val name: String,
    @Text(label = "Last Name")
    val lastName: String,
)

@Agent(description = "Extracts person data from user input and prints it")
class PersonDataExtractor {
    @Action
    fun extractPersonName(userInput: UserInput, context: OperationContext): PersonName? {
        log(context)
        context.set("extractPersonNameRan", true)
        return using(LlmOptions(criteria = Auto)).createObjectIfPossible<PersonName>(
            "Extract the first name of the person from this user input: ${userInput.content}")
    }

    private fun log(context: OperationContext) {
        println("{{{")
        println("\tcontext.objects = ${context.objects}")
        println("\tcontext.processContext = ${context.processContext}")
        println("\tcontext.processContext.blackboard = ${context.processContext.blackboard}")
        println("}}}")
    }

    @Action
    fun extractPersonLastName(userInput: UserInput, context: OperationContext): PersonLastName? {
        log(context)
        context.set("extractPersonLastNameRan", true)
        return using(LlmOptions(criteria = Auto)).createObjectIfPossible<PersonLastName>(
            "Extract the last name of the person from this user input: ${userInput.content}")
    }

    @Action
    fun extractPesonData(userInput: UserInput, context: OperationContext): PersonData? {
        log(context)
        context.set("extractPersonDataRan", true)
        return using(LlmOptions(criteria = Auto)).createObjectIfPossible<PersonData>(
            "Extract the first name and last name of the person from this user input: ${userInput.content}")
    }

    @Action(
        cost = 200.0,
        pre = ["PersonDataNotAvailable"]
    )
    fun askForMissingPersonData(context: OperationContext): PersonData {
        log(context)
        return fromForm("Enter your data")
    }

    @Action
    fun buildPersonData(
        personName: PersonName,
        personLastName: PersonLastName,
        context: OperationContext
    ): PersonData {
        log(context)
        return PersonData(name = personName.content, lastName = personLastName.content)
    }

    @Condition(name = "PersonDataNotAvailable")
    fun isPersonDataNotAvailable(personData: PersonData?, name: PersonName?, lastName: PersonLastName?, context: OperationContext): Boolean {
        return lastName == null || name == null || personData == null
    }

    @AchievesGoal("Person data extraction and printing")
    @Action
    fun printPersonData(
        personData: PersonData,
        context: OperationContext
    ): Boolean {
        log(context)
        println("Person= Name: ${personData.name}, Last Name: ${personData.lastName}")
        return true
    }
}
