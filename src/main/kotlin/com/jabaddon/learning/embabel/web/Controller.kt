package com.jabaddon.learning.embabel.web

import com.embabel.agent.api.annotation.support.AgentMetadataReader
import com.embabel.agent.api.common.autonomy.Autonomy
import com.embabel.agent.api.common.autonomy.GoalChoiceApprover
import com.embabel.agent.core.Agent
import com.embabel.agent.core.AgentPlatform
import com.embabel.agent.core.AgentProcessStatusCode
import com.embabel.agent.core.IoBinding
import com.embabel.agent.core.ProcessControl
import com.embabel.agent.core.ProcessOptions
import com.embabel.agent.core.Verbosity
import com.embabel.agent.domain.io.UserInput
import com.fasterxml.jackson.databind.ObjectMapper
import com.jabaddon.learning.embabel.web.agents.SayHelloAgent
import com.jabaddon.learning.embabel.web.agents.SummarizedWebPages
import com.jabaddon.learning.embabel.web.agents.WebPageSummaryAgent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val agentPlatform: AgentPlatform,
    private val sayHelloAgent: SayHelloAgent,
    private val webPageSummaryAgent: WebPageSummaryAgent,
    private val agentMetadataReader: AgentMetadataReader,
    private val autonomy: Autonomy,
    private val objectMapper: ObjectMapper,
) {
    @GetMapping("/agents")
    fun agents(): List<Map<String, String>> {
        return agentPlatform.agents().map {
            mapOf(
                "name" to it.name,
                "description" to it.description,
            )
        }
    }

    @GetMapping("/run/agents/sayHello")
    fun sayHello(@RequestParam name: String): String {
        val agentProcess = agentPlatform.createAgentProcess(
            agent = agentMetadataReader.createAgentMetadata(sayHelloAgent) as Agent,
            processOptions = ProcessOptions(
                verbosity = Verbosity(
                    showPlanning = true,
                    showPrompts = true,
                    showLlmResponses = true,
                )
            ),
            bindings = mapOf(
                IoBinding.DEFAULT_BINDING to UserInput(
                    name
                )
            )
        )
        agentProcess.run()
        return when (agentProcess.status) {
            AgentProcessStatusCode.COMPLETED -> {
                agentProcess.lastResult() as String
            }
            AgentProcessStatusCode.FAILED -> {
                throw RuntimeException("Agent failed: ${agentProcess.failureInfo}")
            }
            AgentProcessStatusCode.STUCK -> {
                throw RuntimeException("Agent got stuck")
            }
            else -> {
                throw RuntimeException("Unexpected status: ${agentProcess.status}")
            }
        }
    }

    @GetMapping("/run/agents/summary")
    fun searchSummary(@RequestParam topic: String): SummarizedWebPages {
        val agentProcess = agentPlatform.createAgentProcess(
            agent = agentMetadataReader.createAgentMetadata(webPageSummaryAgent) as Agent,
            processOptions = ProcessOptions(
                verbosity = Verbosity(
                    showPlanning = true,
                    showPrompts = true,
                    showLlmResponses = true,
                )
            ),
            bindings = mapOf(
                IoBinding.DEFAULT_BINDING to UserInput(
                   topic
                )
            )
        )
        agentProcess.run()
        return when (agentProcess.status) {
            AgentProcessStatusCode.COMPLETED -> {
                agentProcess.lastResult() as SummarizedWebPages
            }
            AgentProcessStatusCode.FAILED -> {
                throw RuntimeException("Agent failed: ${agentProcess.failureInfo}")
            }
            AgentProcessStatusCode.STUCK -> {
                throw RuntimeException("Agent got stuck")
            }
            else -> {
                throw RuntimeException("Unexpected status: ${agentProcess.status}")
            }
        }
    }

    @GetMapping("/run/agents/by-intent")
    fun runByIntent(@RequestParam intent: String): Any {
        // Let the system choose the best agent and goal
        val result = autonomy.chooseAndAccomplishGoal(
            intent = intent,
            processOptions = ProcessOptions(
                verbosity = Verbosity(
                    showPlanning = true,
                    showPrompts = true,
                    showLlmResponses = true,
                )
            ),
            goalChoiceApprover = GoalChoiceApprover.APPROVE_ALL,
            agentScope = agentPlatform
        )

        return when (result.output) {
            is String -> return mapOf("greeeting" to result.output as String)
            is SummarizedWebPages -> return result.output as SummarizedWebPages
            else -> throw RuntimeException("Unexpected output type: ${result.output::class.java}")
        }
    }

}