# External MCP Server Example

This example demonstrates how to use external MCP (Model Context Protocol) servers with the Embabel agent framework. External MCP servers provide additional capabilities to your agents by connecting to external services and APIs.

## Overview

The External MCP example shows how to:

- Configure agents to use external MCP servers through the MCP Gateway
- Access web search capabilities via Brave Search
- Integrate with GitHub for repository management
- Use Wikipedia for encyclopedic research
- Perform web automation with Puppeteer
- Access mapping services through Google Maps
- Manage Airbnb listings through OpenBnB integration

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Embabel       │    │   MCP Gateway   │    │ External MCP    │
│   Agent         │────│   (Port 9011)   │────│   Servers       │
│   Application   │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                              ┌─────────────────────────┼─────────────────────────┐
                              │                         │                         │
                    ┌─────────▼─────────┐    ┌─────────▼─────────┐    ┌─────────▼─────────┐
                    │   Brave Search    │    │   GitHub API      │    │   Wikipedia       │
                    │   MCP Server      │    │   MCP Server      │    │   MCP Server      │
                    └───────────────────┘    └───────────────────┘    └───────────────────┘
```

## Prerequisites

1. **MCP Gateway**: Start the MCP gateway using Docker Compose
2. **API Keys**: Configure required API keys for external services
3. **Embabel Framework**: Ensure the Embabel agent dependencies are available

## Setup Instructions

### 1. Start the MCP Gateway

The MCP Gateway connects your agents to external MCP servers. Start it using the provided Docker Compose configuration:

```bash
# Create the MCP environment file
cp .mcp.env.example .mcp.env

# Edit .mcp.env with your API keys
vim .mcp.env

# Start the MCP gateway and services
docker-compose -f compose.mcp.yml up -d
```

### 2. Configure API Keys

Create a `.mcp.env` file with the required API keys:

```bash
# Brave Search API Key (for web search)
BRAVE_API_KEY=your_brave_api_key_here

# GitHub Token (for GitHub integration)
GITHUB_TOKEN=your_github_token_here

# Google Maps API Key (for mapping services)
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# OpenBnB API Key (for Airbnb integration)
OPENBNB_API_KEY=your_openbnb_api_key_here

# Other service credentials as needed
```

### 3. Run the External MCP Example

```bash
# Run the external MCP example application
./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.externalmcp.ExternalMcpApp
```

## Available External MCP Services

### Web Search (Brave)
- **Tool**: `brave_web_search`
- **Purpose**: Current web search results
- **Usage**: Real-time information gathering, news, and current events

### Wikipedia
- **Tools**: `wikipedia-mcp:*`
- **Purpose**: Encyclopedic knowledge and background information
- **Usage**: Historical context, definitions, and educational content

### GitHub Integration
- **Tools**: 
  - `github:add_issue_comment`
  - `github:create_issue`
  - `github:list_issues`
  - `github:get_issue`
  - `github:list_pull_requests`
  - `github:get_pull_request`
- **Purpose**: Repository management and collaboration
- **Usage**: Issue tracking, code review, project management

### Web Automation (Puppeteer)
- **Tools**: `puppeteer:*`
- **Purpose**: Browser automation and web interaction
- **Usage**: Screenshots, content extraction, form filling

### Google Maps
- **Tools**: `google-maps:*`
- **Purpose**: Geographic information and mapping
- **Usage**: Location data, directions, place information

### OpenBnB (Airbnb Integration)
- **Tools**: `openbnb-airbnb:*`
- **Purpose**: Vacation rental listings and travel
- **Usage**: Property search, booking information

## Example Agents

### 1. Research Agent (`ExternalMcpResearchAgent`)

Demonstrates comprehensive research using multiple external sources:

```kotlin
@Agent(description = "Research assistant using external MCP servers")
class ExternalMcpResearchAgent {
    
    @Action
    fun conductWebResearch(userInput: UserInput): ResearchReport {
        return using(/* configuration */)
            .withToolGroups(setOf(CoreToolGroups.WEB, CoreToolGroups.MAPS))
            .create("Research using web search and mapping tools...")
    }
}
```

**Use cases**:
- Current event research with web search
- Historical context from Wikipedia
- Geographic analysis with maps
- Technical research with GitHub projects

### 2. Web Automation Agent (`WebAutomationAgent`)

Shows web automation capabilities using Puppeteer:

```kotlin
@Agent(description = "Web automation using Puppeteer MCP server")
class WebAutomationAgent {
    
    @Action
    fun captureWebsiteScreenshot(url: String): WebsiteCapture {
        return using(/* configuration */)
            .withToolGroups(setOf(CoreToolGroups.WEB))
            .create("Take screenshot and analyze website...")
    }
}
```

**Use cases**:
- Website screenshots and analysis
- Content extraction from web pages
- Automated form filling
- Web testing and monitoring

### 3. GitHub Integration Agent (`GitHubIntegrationAgent`)

Demonstrates GitHub repository management:

```kotlin
@Agent(description = "GitHub integration using GitHub MCP server")
class GitHubIntegrationAgent {
    
    @Action
    fun analyzeRepositoryIssues(repositoryUrl: String): RepositoryAnalysis {
        return using(/* configuration */)
            .withToolGroups(setOf(CoreToolGroups.DEV))
            .create("Analyze GitHub repository issues...")
    }
}
```

**Use cases**:
- Issue triage and management
- Pull request reviews
- Repository analysis
- Project workflow automation

## Configuration Details

### Application Configuration

The main application class configures MCP servers:

```kotlin
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
    localModels = [LocalModels.OLLAMA],
    mcpServers = [
        McpServers.DOCKER,           // Docker integration
        McpServers.DOCKER_DESKTOP,   // Docker Desktop integration
        McpServers.GATEWAY           // External MCP gateway
    ],
)
class ExternalMcpApp
```

### Tool Group Mapping

External MCP tools are accessed through core tool groups:

- `CoreToolGroups.WEB`: Web search, Wikipedia, Puppeteer
- `CoreToolGroups.MAPS`: Google Maps integration
- `CoreToolGroups.DEV`: GitHub integration
- Custom tool groups: Direct MCP server access

## Usage Examples

### Basic Web Research

```bash
# Start the application
./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.externalmcp.ExternalMcpApp

# In another terminal, make a request
curl "http://localhost:8080/run/agents/by-intent?intent=Research the latest developments in AI technology"
```

### GitHub Repository Analysis

```bash
curl "http://localhost:8080/run/agents/by-intent?intent=Analyze the issues in the tensorflow/tensorflow repository"
```

### Web Automation

```bash
curl "http://localhost:8080/run/agents/by-intent?intent=Take a screenshot of https://example.com and describe the content"
```

## Troubleshooting

### Common Issues

1. **MCP Gateway Connection Failed**
   - Ensure Docker Compose services are running
   - Check that port 9011 is available
   - Verify network connectivity

2. **API Key Issues**
   - Verify all required API keys are in `.mcp.env`
   - Check API key permissions and quotas
   - Ensure keys are properly formatted

3. **Tool Not Found**
   - Confirm the MCP server is running and registered
   - Check the tool names in the gateway configuration
   - Verify the tool group mappings

### Debugging

Enable debug logging to troubleshoot MCP connections:

```yaml
# application.yml
logging:
  level:
    com.embabel.agent.mcp: DEBUG
    com.jabaddon.learning.embabel.externalmcp: DEBUG
```

### Health Checks

Verify MCP gateway status:

```bash
# Check gateway health
curl http://localhost:9011/health

# List available tools
curl http://localhost:9011/tools
```

## Best Practices

1. **Error Handling**: Always handle MCP server failures gracefully
2. **Rate Limiting**: Respect API rate limits for external services
3. **Caching**: Cache results when appropriate to reduce API calls
4. **Security**: Never commit API keys to version control
5. **Monitoring**: Monitor MCP server health and performance

## Advanced Configuration

### Custom MCP Server

To add a custom MCP server to the gateway:

1. Add the server to `compose.mcp.yml`:
```yaml
command:
  - --servers=your-custom-server
  - --tools=your-custom-server:*
```

2. Configure the server connection details
3. Update agent code to use the new tools

### Tool Filtering

Restrict available tools by modifying the gateway configuration:

```yaml
command:
  - --tools=brave_web_search        # Only web search
  - --tools=github:list_issues      # Only GitHub issue listing
  - --tools=wikipedia-mcp:search    # Only Wikipedia search
```

## Security Considerations

- Store API keys securely using environment variables or secret management
- Validate all external inputs before processing
- Implement proper authentication for agent endpoints
- Monitor and log all external API calls
- Use HTTPS for all external communications

## Performance Optimization

- Implement connection pooling for MCP gateway connections
- Cache frequently accessed data
- Use async operations for long-running external calls
- Monitor and optimize API call patterns
- Implement circuit breakers for external service failures

## Contributing

When adding new external MCP integrations:

1. Add the MCP server configuration to `compose.mcp.yml`
2. Create agent examples demonstrating the integration
3. Update this documentation with usage examples
4. Add appropriate error handling and logging
5. Include unit tests for the integration

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.io/introduction)
- [Embabel Agent Framework Documentation](https://docs.embabel.com)
- [MCP Server Registry](https://github.com/modelcontextprotocol/servers)