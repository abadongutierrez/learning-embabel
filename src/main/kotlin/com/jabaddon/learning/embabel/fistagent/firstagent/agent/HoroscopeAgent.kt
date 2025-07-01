package com.jabaddon.learning.embabel.fistagent.firstagent.agent

import com.embabel.agent.api.annotation.*
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.domain.library.HasContent
import com.embabel.agent.shell.TerminalServices
import com.embabel.ux.form.Text
import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.jabaddon.learning.embabel.fistagent.firstagent.tools.HoroscopeService

/**
 * Data class representing a user with name and star sign.
 * This is the main data structure we'll collect from the form binding.
 */
@JsonClassDescription("User information with astrological details")
data class User(
    @Text(label = "Name")
    val name: String,

    @Text(label = "Star sign")
    @JsonPropertyDescription("Star sign (e.g., Aries, Taurus, Gemini, etc.)")
    val sign: String,
)

/**
 * Data class containing a user's horoscope reading.
 */
data class HoroscopeReading(
    val sign: String,
    val text: String,
) : HasContent {
    override val content: String
        get() = text
}

/**
 * An agent that collects user's name and star sign, then shows their horoscope.
 */
@Agent(
    description = "Collect a user's information and show their horoscope",
    scan = true,
    beanName = "HoroscopeReaderAgent",
)
class HoroscopeAgent(
    private val horoscopeService: HoroscopeService,
    private val terminalService: TerminalServices
) {
    companion object {
        private val validSigns = setOf(
            "aries", "taurus", "gemini", "cancer", "leo", "virgo",
            "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"
        )
    }

    /**
     * Collects user information through a form interface.
     * This action uses Embabel's form binding capabilities to collect
     * the user's name and star sign.
     *
     * @return A User object containing the collected information
     */
    @Action
    fun collectUserInfo(): User =
        fromForm("Please provide your information")

    /**
     * Validates the user's star sign. If invalid, prompts for correction.
     * @param user The user to validate
     * @return A valid User object
     */
    @Action
    fun validateStarSign(user: User, context: OperationContext): User {
        val normalizedSign = user.sign.trim().lowercase()
        return if (validSigns.contains(normalizedSign)) {
            user.copy(sign = normalizedSign.replaceFirstChar { it.uppercase() })
        } else {
            // Try to map the input using LLM
            val mappedSign = askLLMToMapSign(context, user.sign, validSigns)
            if (mappedSign != null && validSigns.contains(mappedSign.lowercase())) {
                // Ask the user to confirm if the mapping is correct (not using forms)
                val confirmed = terminalService.confirm("Is the star sign '$mappedSign' correct?")

                if (confirmed) {
                    user.copy(sign = mappedSign.replaceFirstChar { it.uppercase() })
                } else {
                    // If mapping isn't confirmed, ask for a correct value
                    fromForm("Please enter a valid star sign (e.g., Aries, Taurus, Gemini, etc.)", User::class.java)
                }
            } else {
                fromForm("Invalid star sign '${user.sign}'. Please enter a valid star sign (e.g., Aries, Taurus, Gemini, etc.)", User::class.java)
            }
        }
    }

    fun askLLMToMapSign(context: OperationContext, inputSign: String, knownSigns: Set<String>): String? {
        val text = context.promptRunner().generateText(
            """
            Map the input star sign: "$inputSign" to the closest matching valid star sign from this list:
            ${knownSigns.joinToString(", ")}
        
            If the input doesn't match any valid star sign, return the closest match.
            Return ONLY the mapped star sign name without any explanation. If no match is found, return null.
            """.trimIndent()
        )
        println("Mapped sign from LLM: $text")
        return if (text == null || text.isBlank() || text.lowercase() == "null") null else text.trim()
    }

    /**
     * Retrieves the daily horoscope for a user based on their star sign.
     *
     * @param user The user with their star sign information
     * @return A HoroscopeReading object containing the daily horoscope text
     */
    @AchievesGoal(
        description = "Show the horoscope reading for a user based on their star sign",
        examples = [
            "What's my horoscope?",
            "I want to know my horoscope for today",
            "Show me my star sign reading"
        ]
    )
    @Action
    fun getHoroscope(user: User,  context: OperationContext): HoroscopeReading {
        val validUser = validateStarSign(user, context)
        val horoscopeText = horoscopeService.dailyHoroscope(validUser.sign)
        return HoroscopeReading(
            sign = validUser.sign,
            text = """
                |Hello, ${validUser.name}!
                |
                |Here is your horoscope for ${validUser.sign} today:
                |
                |$horoscopeText
                |
                |Thank you for using the Horoscope Agent!
            """.trimMargin()
        )
    }
}



