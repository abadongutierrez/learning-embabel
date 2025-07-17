package com.jabaddon.learning.embabel.externalmcp

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration properties for external MCP server integration.
 * 
 * This configuration class helps manage settings for connecting to and using
 * external MCP servers through the MCP Gateway.
 */
@Configuration
@ConfigurationProperties(prefix = "embabel.externalmcp")
data class ExternalMcpConfiguration(
    /**
     * MCP Gateway connection settings
     */
    val gateway: GatewayConfig = GatewayConfig(),
    
    /**
     * External service configuration
     */
    val services: ServicesConfig = ServicesConfig(),
    
    /**
     * Tool usage settings
     */
    val tools: ToolsConfig = ToolsConfig()
) {
    
    data class GatewayConfig(
        val host: String = "localhost",
        val port: Int = 9011,
        val protocol: String = "http",
        val timeout: Long = 30000, // 30 seconds
        val retries: Int = 3
    ) {
        val baseUrl: String
            get() = "$protocol://$host:$port"
    }
    
    data class ServicesConfig(
        val braveSearch: ServiceConfig = ServiceConfig("brave", true),
        val wikipedia: ServiceConfig = ServiceConfig("wikipedia-mcp", true),
        val github: ServiceConfig = ServiceConfig("github", true),
        val puppeteer: ServiceConfig = ServiceConfig("puppeteer", true),
        val googleMaps: ServiceConfig = ServiceConfig("google-maps", true),
        val openbnb: ServiceConfig = ServiceConfig("openbnb-airbnb", false)
    )
    
    data class ServiceConfig(
        val name: String,
        val enabled: Boolean = true,
        val timeout: Long = 10000, // 10 seconds
        val rateLimit: Int = 60 // requests per minute
    )
    
    data class ToolsConfig(
        val webSearch: ToolConfig = ToolConfig("brave_web_search", true),
        val wikipediaSearch: ToolConfig = ToolConfig("wikipedia-mcp:search", true),
        val githubIssues: ToolConfig = ToolConfig("github:list_issues", true),
        val githubPRs: ToolConfig = ToolConfig("github:list_pull_requests", true),
        val puppeteerScreenshot: ToolConfig = ToolConfig("puppeteer:screenshot", true),
        val mapsSearch: ToolConfig = ToolConfig("google-maps:search", true)
    )
    
    data class ToolConfig(
        val name: String,
        val enabled: Boolean = true,
        val maxRetries: Int = 2,
        val timeout: Long = 15000 // 15 seconds
    )
}

/**
 * Constants for external MCP tool groups and tool names
 */
object ExternalMcpTools {
    
    // Tool group constants
    const val WEB_TOOLS = "web"
    const val DEV_TOOLS = "dev"
    const val MAPS_TOOLS = "maps"
    
    // Specific tool names available through MCP gateway
    object WebSearch {
        const val BRAVE_SEARCH = "brave_web_search"
    }
    
    object Wikipedia {
        const val SEARCH = "wikipedia-mcp:search"
        const val GET_ARTICLE = "wikipedia-mcp:get_article"
    }
    
    object GitHub {
        const val LIST_ISSUES = "github:list_issues"
        const val GET_ISSUE = "github:get_issue"
        const val CREATE_ISSUE = "github:create_issue"
        const val ADD_ISSUE_COMMENT = "github:add_issue_comment"
        const val LIST_PULL_REQUESTS = "github:list_pull_requests"
        const val GET_PULL_REQUEST = "github:get_pull_request"
    }
    
    object Puppeteer {
        const val SCREENSHOT = "puppeteer:screenshot"
        const val GET_PAGE_CONTENT = "puppeteer:get_page_content"
        const val CLICK_ELEMENT = "puppeteer:click_element"
        const val FILL_FORM = "puppeteer:fill_form"
    }
    
    object GoogleMaps {
        const val SEARCH = "google-maps:search"
        const val GET_DIRECTIONS = "google-maps:directions"
        const val GET_PLACE_DETAILS = "google-maps:place_details"
    }
    
    object OpenBnB {
        const val SEARCH_LISTINGS = "openbnb-airbnb:search_listings"
        const val GET_LISTING_DETAILS = "openbnb-airbnb:get_listing_details"
    }
}