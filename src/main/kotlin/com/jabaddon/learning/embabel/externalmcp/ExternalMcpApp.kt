package com.jabaddon.learning.embabel.externalmcp

import com.embabel.agent.config.annotation.EnableAgentShell
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LocalModels
import com.embabel.agent.config.annotation.LoggingThemes
import com.embabel.agent.config.annotation.McpServers
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

/**
 * Example application demonstrating how to use external MCP servers.
 * 
 * This application shows how to configure and use external MCP (Model Context Protocol) servers
 * for enhanced agent capabilities including web search, GitHub integration, maps, and more.
 * 
 * External MCP servers are configured via the compose.mcp.yml file and accessed through
 * the MCP gateway running on port 9011.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
    localModels = [LocalModels.OLLAMA],
    // Enable various MCP servers for enhanced agent capabilities
    mcpServers = [
        McpServers.DOCKER,           // Docker integration
        McpServers.DOCKER_DESKTOP,   // Docker Desktop integration
        McpServers.GATEWAY           // External MCP gateway (connects to external services)
    ],
)
class ExternalMcpApp {
}

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<ExternalMcpApp>(*args)
}