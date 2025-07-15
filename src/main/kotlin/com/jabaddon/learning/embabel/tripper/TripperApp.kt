package com.jabaddon.learning.embabel.tripper

import com.embabel.agent.config.annotation.EnableAgentShell
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LocalModels
import com.embabel.agent.config.annotation.LoggingThemes
import com.embabel.agent.config.annotation.McpServers
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
    localModels = [LocalModels.OLLAMA],
    mcpServers = [McpServers.DOCKER, McpServers.DOCKER_DESKTOP],
)
class TripperApp {
}

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<TripperApp>(*args)
}