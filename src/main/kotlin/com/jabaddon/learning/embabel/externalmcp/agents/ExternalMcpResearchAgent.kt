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
 * Research Agent that demonstrates using external MCP servers for comprehensive research.
 * 
 * This agent uses multiple external MCP services:
 * - Brave web search for current information
 * - Wikipedia for encyclopedic knowledge  
 * - GitHub for code and project research
 * - Google Maps for location-based research
 */
@Agent(
    description = "Research assistant that uses external MCP servers for comprehensive information gathering"
)
class ExternalMcpResearchAgent {

    private val logger = org.slf4j.LoggerFactory.getLogger(ExternalMcpResearchAgent::class.java)

    private val researcherPersona = Persona(
        name = "Research Assistant",
        persona = "You are an expert research assistant with access to multiple external information sources",
        voice = "thorough and analytical",
        objective = "Provide comprehensive research using web search, encyclopedic knowledge, code repositories, and location data"
    )

    @Action
    fun conductWebResearch(userInput: UserInput): ResearchReport {
        logger.info("Conducting web research for: ${userInput.content}")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(researcherPersona)
        ).withToolGroups(
            // Use external MCP services through the gateway
            setOf(
                CoreToolGroups.WEB,    // Includes brave_web_search from MCP gateway
                CoreToolGroups.MAPS,   // Includes google-maps tools from MCP gateway
            )
        ).create(
            prompt = """
            Research the following topic using available web search tools:
            
            Topic: ${userInput.content}
            
            Please:
            1. Use web search to find current, relevant information
            2. If the topic involves locations, use mapping tools to provide geographic context
            3. Compile findings into a comprehensive research report
            4. Include sources and links where available
            
            Focus on accuracy and provide multiple perspectives where applicable.
            """.trimIndent()
        )
    }

    @Action
    fun researchWithWikipedia(topic: String): WikipediaResearch {
        logger.info("Researching with Wikipedia: $topic")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(researcherPersona)
        ).withToolGroups(
            // Wikipedia MCP server tools available through gateway
            setOf(CoreToolGroups.WEB)
        ).create(
            prompt = """
            Research the following topic using Wikipedia MCP tools:
            
            Topic: $topic
            
            Please:
            1. Search Wikipedia for comprehensive encyclopedic information
            2. Provide detailed background, history, and key facts
            3. Include related topics and cross-references
            4. Summarize the most important points
            
            Ensure the information is well-structured and educational.
            """.trimIndent()
        )
    }

    @Action
    fun researchGitHubProjects(query: String): GitHubResearch {
        logger.info("Researching GitHub projects: $query")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(researcherPersona)
        ).create(
            prompt = """
            Research GitHub projects and repositories related to: $query
            
            Use available GitHub MCP tools to:
            1. Search for relevant repositories
            2. Look at issues and pull requests for active projects
            3. Identify popular and well-maintained projects
            4. Provide insights about the technology landscape
            
            Compile findings into a report about the current state of development in this area.
            """.trimIndent()
        )
    }

    @Action
    fun comprehensiveResearch(userInput: UserInput): ComprehensiveResearchReport {
        logger.info("Conducting comprehensive research for: ${userInput.content}")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(researcherPersona)
        ).withToolGroups(
            // Use all available external MCP services
            setOf(
                CoreToolGroups.WEB,     // Web search, Wikipedia
                CoreToolGroups.MAPS,    // Google Maps
                CoreToolGroups.DEV,     // GitHub integration
            )
        ).create(
            prompt = """
            Conduct comprehensive research on the following topic using all available external MCP services:
            
            Topic: ${userInput.content}
            
            Research approach:
            1. Web Search: Use brave web search for current information and news
            2. Wikipedia: Search for encyclopedic background and historical context
            3. GitHub: Look for related open source projects and code
            4. Maps: If relevant, provide geographic context and location information
            
            Compile all findings into a comprehensive report that includes:
            - Current state and recent developments
            - Historical background and context
            - Technical implementations and code examples (if applicable)
            - Geographic relevance (if applicable)
            - Multiple perspectives and sources
            
            Ensure the report is well-structured, accurate, and cites sources.
            """.trimIndent()
        )
    }
}

/**
 * Research report from web-based research
 */
data class ResearchReport(
    val topic: String,
    val summary: String,
    val keyFindings: List<String>,
    val sources: List<String>,
    val relatedTopics: List<String>
)

/**
 * Research report specifically from Wikipedia
 */
data class WikipediaResearch(
    val topic: String,
    val encyclopedicSummary: String,
    val keyFacts: List<String>,
    val historicalContext: String,
    val relatedArticles: List<String>
)

/**
 * Research report from GitHub projects
 */
data class GitHubResearch(
    val query: String,
    val popularProjects: List<String>,
    val activeDevelopment: List<String>,
    val technicalInsights: List<String>,
    val recommendations: List<String>
)

/**
 * Comprehensive research report combining multiple sources
 */
data class ComprehensiveResearchReport(
    val topic: String,
    val executiveSummary: String,
    val webResearchFindings: String,
    val encyclopedicBackground: String,
    val technicalImplementations: String,
    val geographicContext: String?,
    val sources: List<String>,
    val recommendations: List<String>
)