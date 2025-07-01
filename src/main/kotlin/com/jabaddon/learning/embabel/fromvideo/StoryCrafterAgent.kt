package com.jabaddon.learning.embabel.fromvideo

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObject
import com.embabel.agent.domain.io.UserInput

data class Story(val content: String)

@Agent(description = "Agent to craft a story to the user")
class StoryCrafterAgent {
    @Action
    @AchievesGoal("Craft a story based on user input")
    fun craftStory(userInput: UserInput): Story {
        return usingDefaultLlm.createObject("""
            Craft a compelling story based on the user's input.
            The story should be engaging, imaginative, and tailored to the user's preferences.
            User Input: ${userInput.content}
            Ensure the story has a clear beginning, middle, and end.
            Use descriptive language to bring the story to life.
        """.trimIndent())
    }
}