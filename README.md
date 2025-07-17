# Learning Embabel

A collection of examples and learning materials for the Embabel Agent Framework. This repository demonstrates various agent patterns, integrations, and use cases for building intelligent agents with Embabel.

## Overview

This repository contains multiple example applications showcasing different aspects of the Embabel framework:

- **First Agent** (`fistagent`) - Basic agent implementation
- **Sourcer** - Web research and content sourcing agents
- **Tripper** - Travel planning agent with MCP integration
- **From Video** - Video content analysis agents
- **Web** - Web-based agent interactions
- **External MCP** - **NEW!** Comprehensive external MCP server integration examples

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker and Docker Compose (for MCP services)

### Running Examples

1. **Clone the repository:**
   ```bash
   git clone https://github.com/abadongutierrez/learning-embabel.git
   cd learning-embabel
   ```

2. **Start MCP services (for external MCP examples):**
   ```bash
   # Copy and configure MCP environment
   cp .mcp.env.example .mcp.env
   # Edit .mcp.env with your API keys
   
   # Start MCP gateway and external services
   docker-compose -f compose.mcp.yml up -d
   ```

3. **Run a specific example:**
   ```bash
   # External MCP example (NEW!)
   ./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.externalmcp.ExternalMcpApp
   
   # Tripper (travel planning)
   ./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.tripper.TripperApp
   
   # Sourcer (research agents)
   ./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.sourcer.MainApp
   ```

## Examples Included

### 🆕 External MCP Server Integration

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/externalmcp/`

**Description**: Comprehensive example showing how to integrate with external MCP (Model Context Protocol) servers for enhanced agent capabilities.

**Features**:
- Web search integration (Brave Search)
- Wikipedia knowledge access
- GitHub repository management
- Web automation with Puppeteer
- Google Maps integration
- Airbnb listing management

**Key Components**:
- `ExternalMcpApp` - Main application with MCP gateway configuration
- `ExternalMcpResearchAgent` - Multi-source research agent
- `WebAutomationAgent` - Browser automation examples
- `GitHubIntegrationAgent` - Repository management automation

**Getting Started**: See [External MCP README](src/main/kotlin/com/jabaddon/learning/embabel/externalmcp/README.md)

### Travel Planning Agent (Tripper)

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/tripper/`

**Description**: Travel planning agent that helps create detailed itineraries using web search and mapping tools.

**Features**:
- Journey planning from user input
- Points of interest research
- Mapping and location services
- Docker and Docker Desktop MCP integration

### Research Agents (Sourcer)

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/sourcer/`

**Description**: Collection of research and content sourcing agents.

**Features**:
- Web page summarization
- Test planning automation
- Search result analysis
- Content extraction and processing

### Video Content Analysis

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/fromvideo/`

**Description**: Agents for analyzing and processing video content.

### Web Integration

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/web/`

**Description**: Web-based agent interactions and integrations.

### Basic Agent (First Agent)

**Location**: `src/main/kotlin/com/jabaddon/learning/embabel/fistagent/`

**Description**: Basic agent implementation showing fundamental Embabel concepts.

## MCP (Model Context Protocol) Integration

This repository demonstrates extensive use of MCP servers for connecting agents to external services:

### Local MCP Servers
- Docker integration
- Docker Desktop integration
- Local development tools

### External MCP Servers (via Gateway)
- **Brave Search**: Current web search capabilities
- **Wikipedia**: Encyclopedic knowledge access
- **GitHub**: Repository management and collaboration
- **Puppeteer**: Browser automation and web interaction
- **Google Maps**: Geographic information and mapping
- **OpenBnB**: Vacation rental and travel services

### MCP Gateway Setup

The MCP Gateway (configured in `compose.mcp.yml`) provides a unified interface to multiple external MCP servers:

```yaml
services:
  mcp-gateway:
    image: docker/agents_gateway:v2
    ports:
      - 9011:9011
    command:
      - --servers=brave,wikipedia-mcp,puppeteer,github,openbnb-airbnb,google-maps
      - --tools=brave_web_search,wikipedia-mcp:*,github:*,puppeteer:*,google-maps:*
```

## API Usage

All examples expose REST endpoints for agent interaction:

```bash
# List available agents
GET http://localhost:8080/agents

# Execute agent by intent
GET http://localhost:8080/run/agents/by-intent?intent=Your request here

# Run specific agent action
GET http://localhost:8080/run/agents/{agentName}?param=value
```

### Example Requests

```bash
# External MCP research example
curl "http://localhost:8080/run/agents/by-intent?intent=Research the latest AI developments using web search and Wikipedia"

# GitHub repository analysis
curl "http://localhost:8080/run/agents/by-intent?intent=Analyze the issues in the tensorflow/tensorflow repository"

# Web automation example
curl "http://localhost:8080/run/agents/by-intent?intent=Take a screenshot of https://example.com"

# Travel planning
curl "http://localhost:8080/run/agents/by-intent?intent=Plan a trip from Mexico City to Japan for 4 friends next month"
```

## Configuration

### Environment Variables

Configure external services through environment variables:

```bash
# Web search
SERPER_API_KEY=your_serper_key
BRAVE_API_KEY=your_brave_key

# GitHub integration  
GITHUB_TOKEN=your_github_token

# Google services
GOOGLE_MAPS_API_KEY=your_maps_key

# Other services
OPENAI_API_KEY=your_openai_key
```

### MCP Configuration

External MCP servers are configured in `.mcp.env`:

```bash
cp .mcp.env.example .mcp.env
# Edit with your API keys
```

## Development

### Building

```bash
./mvnw clean compile
```

### Testing

```bash
./mvnw test
```

### Running with Profiles

```bash
# Development mode
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Production mode  
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Embabel       │    │   MCP Gateway   │    │ External APIs   │
│   Agents        │────│   (Port 9011)   │────│ & Services      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                       │                       │
          │                       │                       │
    ┌─────▼─────┐          ┌─────▼─────┐          ┌─────▼─────┐
    │   Local   │          │    MCP    │          │ Brave,    │
    │   Tools   │          │  Servers  │          │ GitHub,   │
    │           │          │           │          │ Wikipedia │
    └───────────┘          └───────────┘          └───────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add your example agent or improvement
4. Include documentation and tests
5. Submit a pull request

### Adding New Examples

When adding new agent examples:

1. Create a new package under `com.jabaddon.learning.embabel.{your-example}`
2. Include a main application class
3. Add agent implementations
4. Create a README explaining the example
5. Add appropriate configuration
6. Include tests

### Adding MCP Integrations

For new MCP server integrations:

1. Add server configuration to `compose.mcp.yml`
2. Update `.mcp.env.example` with required API keys
3. Create agent examples using the integration
4. Document the setup and usage
5. Add tool constants to configuration classes

## Troubleshooting

### Common Issues

1. **Embabel Dependencies Not Found**
   ```
   Could not resolve: com.embabel.agent:embabel-agent-starter
   ```
   - Ensure you have access to the Embabel artifact repository
   - Check your Maven settings and credentials

2. **MCP Gateway Connection Failed**
   ```
   Connection refused to localhost:9011
   ```
   - Start the MCP gateway: `docker-compose -f compose.mcp.yml up -d`
   - Check Docker is running and port 9011 is available

3. **External API Authentication Errors**
   ```
   401 Unauthorized or 403 Forbidden
   ```
   - Verify API keys in `.mcp.env` are correct
   - Check API key permissions and quotas

### Debug Mode

Enable debug logging:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev -Dlogging.level.com.embabel=DEBUG
```

## Resources

- [Embabel Framework Documentation](https://docs.embabel.com)
- [Model Context Protocol](https://modelcontextprotocol.io)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

## License

This project is for learning and demonstration purposes. Please refer to the Embabel framework license for usage terms.

## Support

For questions about Embabel framework usage or these examples:

1. Check the documentation links above
2. Review existing issues in this repository
3. Create a new issue with details about your question or problem

---

**Happy learning with Embabel! 🚀**