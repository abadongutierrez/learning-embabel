package com.jabaddon.learning.embabel.tripper.agents

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.using
import com.embabel.agent.api.common.create
import com.embabel.agent.api.common.createObjectIfPossible
import com.embabel.agent.core.CoreToolGroups
import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.prompt.element.ToolCallControl
import com.embabel.agent.prompt.persona.Persona
import com.embabel.agent.prompt.persona.RoleGoalBackstory
import com.embabel.common.ai.model.LlmOptions
import com.embabel.common.ai.model.ModelSelectionCriteria.Companion.byName
import com.embabel.common.ai.prompt.PromptContributor
import com.jabaddon.learning.embabel.tools.SerperTool
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.LocalDate

val HermesPersona = Persona(
    name = "Hermes",
    persona = "You are an expert travel planner",
    voice = "friendly and concise",
    objective = "Make a detailed travel plan meeting requirements. By default always consider travelers use flights",
)

val Researcher = RoleGoalBackstory(
    role = "Researcher",
    goal = "Research points of interest for a travel plan",
    backstory = "You are an expert researcher who can find interesting stories about art, culture, and famous people associated with places.",
)

@ConfigurationProperties("embabel.tripper")
data class TravelPlannerProperties(
    val wordCount: Int = 700,
    val imageWidth: Int = 800,
    val travelPlannerPersona: Persona = HermesPersona,
    val researcher: RoleGoalBackstory = Researcher,
    val toolCallControl: ToolCallControl = ToolCallControl(),
    private val thinkerModel: String = "gpt-4.1",
    private val researcherModel: String = "gpt-4.1",
) {
    val thinkerLlm = LlmOptions(
        criteria = byName(thinkerModel),
    )

    val researcherLlm = LlmOptions(
        criteria = byName(researcherModel),
    )

}

@Agent(
    description = "Make a detailed travel plan for a trip",
)
class TripperAgent(
    private val config: TravelPlannerProperties,
    private val serperTool: SerperTool,
) {

    private val logger = org.slf4j.LoggerFactory.getLogger(TripperAgent::class.java)

    @Action
    fun planFromUserInput(userInput: UserInput): JourneyTravelBrief? {
        return using(
            generateExamples = true
        )
            .createObjectIfPossible(
                """
                Given the following user input, extract a travel brief for a journey.
                <user-input>${userInput.content}</user-input>
            """.trimIndent(),
            )
    }

    @Action
    fun findPointsOfInterest(
        travelBrief: JourneyTravelBrief,
    ): ItineraryIdeas {
        return using(
            llm = config.thinkerLlm,
            promptContributors = listOf(
                config.travelPlannerPersona,
            ),
        ).withToolGroups(
            setOf(CoreToolGroups.WEB, CoreToolGroups.MAPS, CoreToolGroups.MATH),
        )

            .create(
                prompt = """
                Consider the following travel brief for a journey from ${travelBrief.from} to ${travelBrief.to}.
                ${travelBrief.contribution()}
                Use the web search tool to find points of interest that are relevant to the travel brief and travelers.
                Use the mapping tool to consider appropriate order and put a rough date range for each point of interest.
            """.trimIndent(),
            )
    }

    @AchievesGoal("Show the user a detailed travel plan for their trip")
    @Action
    fun printJourneyTravelBrief(
        journeyTravelBrief: JourneyTravelBrief,
        ideas: ItineraryIdeas
    ): Output {
        return Output(
            journey = journeyTravelBrief,
            ideas = ideas,
        )
    }
}

data class Output(
    val journey: JourneyTravelBrief,
    val ideas: ItineraryIdeas,
)

data class JourneyTravelBrief(
    val from: String,
    val to: String,
    val transportPreference: String,
    override val brief: String,
    override val departureDate: LocalDate,
    override val returnDate: LocalDate,
) : TravelBrief {

    override fun contribution(): String =
        """
        Journey from $from to $to
        Dates: $departureDate to $returnDate
        Brief: $brief
        Transport preference: $transportPreference
    """.trimIndent()
}

interface TravelBrief: PromptContributor {
    val brief: String
    val departureDate: LocalDate
    val returnDate: LocalDate
}

data class Traveler(
    val name: String,
    val about: String,
)

data class Travelers(
    val travelers: List<Traveler>,
) : PromptContributor {

    override fun contribution(): String =
        if (travelers.isEmpty()) "No information could be found about travelers"
        else "${travelers.size} travelers:\n" + travelers.joinToString(separator = "\n") {
            "${it.name}: ${it.about}"
        }
}

data class PointOfInterest(
    val name: String,
    val description: String,
    val location: String,
    val fromDate: LocalDate,
    val toDate: LocalDate,
)

data class ItineraryIdeas(
    val pointsOfInterest: List<PointOfInterest>,
)
