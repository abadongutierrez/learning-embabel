package com.jabaddon.learning.embabel.externalmcp.agents

import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.using
import com.embabel.agent.api.common.create
import com.embabel.agent.core.CoreToolGroups
import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.prompt.persona.Persona
import com.embabel.common.ai.model.LlmOptions
import com.embabel.common.ai.model.ModelSelectionCriteria.Companion.byName

/**
 * Web automation agent that demonstrates using Puppeteer MCP server for web interactions.
 * 
 * This agent shows how to use external MCP servers for web automation tasks like:
 * - Taking screenshots of websites
 * - Extracting content from web pages
 * - Automating web interactions
 */
@Agent(
    description = "Web automation assistant using Puppeteer MCP server for web interactions"
)
class WebAutomationAgent {

    private val logger = org.slf4j.LoggerFactory.getLogger(WebAutomationAgent::class.java)

    private val automationPersona = Persona(
        name = "Web Automation Specialist",
        persona = "You are an expert in web automation and browser interactions",
        voice = "technical and precise",
        objective = "Perform web automation tasks efficiently and reliably using browser automation tools"
    )

    @Action
    fun captureWebsiteScreenshot(url: String): WebsiteCapture {
        logger.info("Capturing screenshot of website: $url")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(automationPersona)
        ).withToolGroups(
            // Puppeteer MCP server tools for web automation
            setOf(CoreToolGroups.WEB)
        ).create(
            prompt = """
            Take a screenshot of the following website using Puppeteer automation tools:
            
            URL: $url
            
            Please:
            1. Navigate to the website
            2. Wait for the page to fully load
            3. Take a full-page screenshot
            4. Analyze the visual content and layout
            5. Provide a description of what's visible on the page
            
            Ensure the screenshot captures the complete page content.
            """.trimIndent()
        )
    }

    @Action
    fun extractWebsiteContent(url: String): WebContentExtraction {
        logger.info("Extracting content from website: $url")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(automationPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.WEB)
        ).create(
            prompt = """
            Extract and analyze content from the following website using web automation tools:
            
            URL: $url
            
            Please:
            1. Navigate to the website
            2. Extract the main text content
            3. Identify key headings and structure
            4. Extract any important metadata
            5. Summarize the page's main purpose and content
            
            Focus on extracting meaningful content while ignoring navigation and ads.
            """.trimIndent()
        )
    }

    @Action
    fun performWebInteraction(instruction: UserInput): WebInteractionResult {
        logger.info("Performing web interaction: ${instruction.content}")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(automationPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.WEB)
        ).create(
            prompt = """
            Perform the following web interaction using browser automation tools:
            
            Instruction: ${instruction.content}
            
            Available actions include:
            - Navigate to websites
            - Click on elements
            - Fill out forms
            - Take screenshots
            - Extract content
            - Wait for elements to load
            
            Please:
            1. Break down the instruction into specific automation steps
            2. Execute the steps using available Puppeteer tools
            3. Capture relevant screenshots or content
            4. Report on the success or failure of each step
            5. Provide a summary of what was accomplished
            
            Ensure all interactions are performed safely and ethically.
            """.trimIndent()
        )
    }
}

/**
 * Result of website screenshot capture
 */
data class WebsiteCapture(
    val url: String,
    val screenshotPath: String?,
    val pageTitle: String,
    val visualDescription: String,
    val pageLoadTime: String,
    val errors: List<String>
)

/**
 * Result of website content extraction
 */
data class WebContentExtraction(
    val url: String,
    val mainContent: String,
    val headings: List<String>,
    val metadata: Map<String, String>,
    val summary: String,
    val extractionTimestamp: String
)

/**
 * Result of web interaction automation
 */
data class WebInteractionResult(
    val instruction: String,
    val steps: List<String>,
    val screenshots: List<String>,
    val extractedData: Map<String, String>,
    val success: Boolean,
    val errors: List<String>,
    val summary: String
)