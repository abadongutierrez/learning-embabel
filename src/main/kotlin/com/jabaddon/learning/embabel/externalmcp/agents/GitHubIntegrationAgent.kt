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
 * GitHub Integration Agent that demonstrates using GitHub MCP server.
 * 
 * This agent shows how to use external GitHub MCP services for:
 * - Managing GitHub issues
 * - Reviewing pull requests
 * - Searching repositories
 * - Creating issues and comments
 */
@Agent(
    description = "GitHub integration assistant using GitHub MCP server for repository management"
)
class GitHubIntegrationAgent {

    private val logger = org.slf4j.LoggerFactory.getLogger(GitHubIntegrationAgent::class.java)

    private val githubPersona = Persona(
        name = "GitHub Assistant",
        persona = "You are an expert in GitHub workflows and repository management",
        voice = "helpful and technically precise",
        objective = "Assist with GitHub repository management, issue tracking, and collaboration"
    )

    @Action
    fun analyzeRepositoryIssues(repositoryUrl: String): RepositoryAnalysis {
        logger.info("Analyzing repository issues: $repositoryUrl")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(githubPersona)
        ).withToolGroups(
            // GitHub MCP server tools
            setOf(CoreToolGroups.DEV)
        ).create(
            prompt = """
            Analyze the GitHub repository issues using GitHub MCP tools:
            
            Repository: $repositoryUrl
            
            Please:
            1. List current open issues
            2. Identify the most critical issues
            3. Analyze issue patterns and common themes
            4. Check for stale or abandoned issues
            5. Provide recommendations for issue management
            
            Focus on providing actionable insights for repository maintainers.
            """.trimIndent()
        )
    }

    @Action
    fun reviewPullRequests(repositoryUrl: String): PullRequestReview {
        logger.info("Reviewing pull requests for: $repositoryUrl")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(githubPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.DEV)
        ).create(
            prompt = """
            Review pull requests for the GitHub repository using GitHub MCP tools:
            
            Repository: $repositoryUrl
            
            Please:
            1. List current open pull requests
            2. Analyze the changes in each PR
            3. Identify PRs that need attention
            4. Check for merge conflicts or CI failures
            5. Provide review comments and suggestions
            6. Recommend which PRs are ready to merge
            
            Provide constructive feedback for both maintainers and contributors.
            """.trimIndent()
        )
    }

    @Action
    fun createIssueReport(userInput: UserInput): IssueCreationResult {
        logger.info("Creating GitHub issue based on: ${userInput.content}")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o-mini")),
            promptContributors = listOf(githubPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.DEV)
        ).create(
            prompt = """
            Create a GitHub issue based on the following request using GitHub MCP tools:
            
            Request: ${userInput.content}
            
            Please:
            1. Determine the appropriate repository (if not specified)
            2. Create a well-formatted issue with:
               - Clear title
               - Detailed description
               - Steps to reproduce (if applicable)
               - Expected vs actual behavior
               - Appropriate labels
            3. Add the issue to the repository
            4. Provide the issue URL and number
            
            Ensure the issue follows best practices for GitHub issue creation.
            """.trimIndent()
        )
    }

    @Action
    fun manageRepositoryWorkflow(repositoryUrl: String, action: String): WorkflowManagement {
        logger.info("Managing repository workflow: $action for $repositoryUrl")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(githubPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.DEV)
        ).create(
            prompt = """
            Manage GitHub repository workflow using GitHub MCP tools:
            
            Repository: $repositoryUrl
            Action: $action
            
            Available workflow actions:
            - Triage issues (organize, label, assign)
            - Review and merge PRs
            - Update project boards
            - Manage releases
            - Update documentation
            - Close stale issues
            
            Please:
            1. Execute the requested workflow action
            2. Provide detailed steps taken
            3. Report on any changes made
            4. Suggest follow-up actions if needed
            
            Ensure all actions follow repository guidelines and best practices.
            """.trimIndent()
        )
    }

    @Action
    fun searchAndAnalyzeProjects(searchQuery: String): ProjectAnalysis {
        logger.info("Searching and analyzing GitHub projects: $searchQuery")
        
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(githubPersona)
        ).withToolGroups(
            setOf(CoreToolGroups.DEV)
        ).create(
            prompt = """
            Search and analyze GitHub projects using GitHub MCP tools:
            
            Search Query: $searchQuery
            
            Please:
            1. Search for repositories matching the query
            2. Analyze the most popular and active projects
            3. Compare features, activity levels, and maintenance status
            4. Identify trending projects and emerging patterns
            5. Provide recommendations based on different use cases
            6. Include metrics like stars, forks, recent commits, and issues
            
            Focus on providing insights that help with technology decisions and learning.
            """.trimIndent()
        )
    }
}

/**
 * Repository analysis result
 */
data class RepositoryAnalysis(
    val repositoryUrl: String,
    val totalIssues: Int,
    val openIssues: Int,
    val criticalIssues: List<String>,
    val issuePatterns: List<String>,
    val staleIssues: List<String>,
    val recommendations: List<String>
)

/**
 * Pull request review result
 */
data class PullRequestReview(
    val repositoryUrl: String,
    val openPRs: List<String>,
    val prAnalysis: Map<String, String>,
    val readyToMerge: List<String>,
    val needsAttention: List<String>,
    val reviewComments: List<String>
)

/**
 * Issue creation result
 */
data class IssueCreationResult(
    val issueUrl: String,
    val issueNumber: Int,
    val title: String,
    val description: String,
    val labels: List<String>,
    val success: Boolean,
    val errors: List<String>
)

/**
 * Workflow management result
 */
data class WorkflowManagement(
    val repositoryUrl: String,
    val action: String,
    val stepsTaken: List<String>,
    val changesMade: List<String>,
    val followUpActions: List<String>,
    val success: Boolean
)

/**
 * Project analysis result
 */
data class ProjectAnalysis(
    val searchQuery: String,
    val foundProjects: List<String>,
    val popularProjects: Map<String, ProjectMetrics>,
    val trendingProjects: List<String>,
    val recommendations: Map<String, String>,
    val insights: List<String>
)

/**
 * Project metrics
 */
data class ProjectMetrics(
    val stars: Int,
    val forks: Int,
    val recentCommits: Int,
    val openIssues: Int,
    val lastActivity: String,
    val maintenanceStatus: String
)