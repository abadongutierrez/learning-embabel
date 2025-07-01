package com.jabaddon.learning.embabel.fistagent.firstagent.agent

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.usingDefaultLlm
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.domain.io.UserInput

data class Step1output(
    val content: String,
)

data class Step2output(
    val content: String,
)

data class Step3output(
    val content: String,
)

@Agent(
    name = "MetricValueExtractorAgent",
    description = "An agent that extracts metric values from a given input.",
    planner = com.embabel.agent.api.annotation.Planner.GOAP // default
)
class MetricValueExtractorAgent {

    @Action
    fun step1(userInput: UserInput): Step1output? =
        usingDefaultLlm.createObjectIfPossible(
            """
					Extract only the numerical values and their associated metrics from the text.
					Format each as 'value: metric' on a new line.
					Example format:
					92: customer satisfaction
					45%: revenue growth

                    ${userInput.content}
            """
        )

//    @Action
//    fun step1(userInput: UserInput): Step1output? {
//        val output = usingDefaultLlm.generateText(
//        """
//					Extract only the numerical values and their associated metrics from the text.
//					Format each as 'value: metric' on a new line.
//					Example format:
//					92: customer satisfaction
//					45%: revenue growth
//
//                    ${userInput.content}
//            """
//        )
//        return Step1output(content = output)
//    }

    @Action
    fun step2(step1output: Step1output): Step2output? =
        usingDefaultLlm.createObjectIfPossible(
            """
                    Convert all numerical values to percentages where possible.
					If not a percentage or points, convert to decimal (e.g., 92 points -> 92%).
					Keep one number per line.
					Example format:
					92%: customer satisfaction
					45%: revenue growth

                    ${step1output.content}
            """
        )

    @Action
    fun step3(step2output: Step2output): Step3output? =
        usingDefaultLlm.createObjectIfPossible(
            """
                    Sort all lines in descending order by numerical value.
					Keep the format 'value: metric' on each line.
					Example:
					92%: customer satisfaction
					87%: employee satisfaction

                    ${step2output.content}
            """
        )

    @AchievesGoal("Format sorted data as markdown table")
    @Action
    fun step4(step3output: Step3output): String {
        val generateText = usingDefaultLlm.generateText(
            """
                    Format the sorted data as a markdown table with columns:
					| Metric | Value |
					|:--|--:|
					| Customer Satisfaction | 92% | 

                    ${step3output.content}
            """
        )
        return generateText
    }
}