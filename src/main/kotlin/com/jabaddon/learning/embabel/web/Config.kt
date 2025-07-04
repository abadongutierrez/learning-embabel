package com.jabaddon.learning.embabel.web

import com.jabaddon.learning.embabel.tools.SerperTool
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {
    @Bean
    fun serperTool(@Value("\${serper.api.key}") serperApiKey: String) = SerperTool(serperApiKey)

    @Bean
    fun jsoupTool() = com.jabaddon.learning.embabel.tools.JSoupTool()
}