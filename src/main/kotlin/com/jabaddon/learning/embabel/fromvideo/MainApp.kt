package com.jabaddon.learning.embabel.fromvideo

import com.embabel.agent.config.annotation.EnableAgentShell
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LoggingThemes
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
)
class MainApp {
}

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<MainApp>(*args)
}