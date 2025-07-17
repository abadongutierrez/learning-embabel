package com.jabaddon.learning.embabel.externalmcp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Integration tests for the External MCP Example application.
 * 
 * These tests verify that the application starts correctly and the configuration
 * is properly loaded. Since we don't have access to external MCP servers in tests,
 * we focus on configuration and basic application startup.
 */
@SpringBootTest(classes = [ExternalMcpApp::class])
@ActiveProfiles("test")
class ExternalMcpAppIntegrationTest {

    @Test
    fun `application should start successfully`() {
        // If the test runs without throwing an exception, 
        // the application context loads successfully
    }
}

/**
 * Unit tests for ExternalMcpConfiguration
 */
class ExternalMcpConfigurationTest {

    @Test
    fun `gateway configuration should have correct defaults`() {
        val config = ExternalMcpConfiguration()
        
        assert(config.gateway.host == "localhost")
        assert(config.gateway.port == 9011)
        assert(config.gateway.protocol == "http")
        assert(config.gateway.baseUrl == "http://localhost:9011")
    }

    @Test
    fun `services configuration should enable main services by default`() {
        val config = ExternalMcpConfiguration()
        
        assert(config.services.braveSearch.enabled)
        assert(config.services.wikipedia.enabled)
        assert(config.services.github.enabled)
        assert(config.services.puppeteer.enabled)
        assert(config.services.googleMaps.enabled)
        // OpenBnB should be disabled by default
        assert(!config.services.openbnb.enabled)
    }

    @Test
    fun `tools configuration should have reasonable timeouts`() {
        val config = ExternalMcpConfiguration()
        
        assert(config.tools.webSearch.timeout == 15000L)
        assert(config.tools.wikipediaSearch.timeout == 15000L)
        assert(config.tools.puppeteerScreenshot.timeout == 15000L)
    }

    @Test
    fun `external mcp tool constants should be defined`() {
        assert(ExternalMcpTools.WebSearch.BRAVE_SEARCH == "brave_web_search")
        assert(ExternalMcpTools.Wikipedia.SEARCH == "wikipedia-mcp:search")
        assert(ExternalMcpTools.GitHub.LIST_ISSUES == "github:list_issues")
        assert(ExternalMcpTools.Puppeteer.SCREENSHOT == "puppeteer:screenshot")
        assert(ExternalMcpTools.GoogleMaps.SEARCH == "google-maps:search")
    }
}