package com.jabaddon.learning.embabel.web

import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LoggingThemes
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
)
class WebMainApp {
}

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<WebMainApp>(*args)
}