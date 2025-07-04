package com.jabaddon.learning.embabel.sourcer

import com.embabel.agent.config.annotation.EnableAgentShell
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LoggingThemes
import com.jabaddon.learning.embabel.tools.SerperTool
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
)
class MainApp {

    @Bean
    fun serperTool(@Value("\${serper.api.key}") serperApiKey: String) = SerperTool(serperApiKey)

    @Bean
    fun jsoupTool() = com.jabaddon.learning.embabel.tools.JSoupTool()
}

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<MainApp>(*args)
}