#!/bin/bash

# External MCP Demo Script
# This script demonstrates the external MCP server integration example

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_header() {
    echo -e "${PURPLE}================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}================================${NC}"
    echo
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_demo() {
    echo -e "${CYAN}[DEMO]${NC} $1"
}

print_code() {
    echo -e "${YELLOW}$1${NC}"
}

# Main demo
clear
print_header "External MCP Server Integration Demo"

echo "This demo shows how to use external MCP servers with Embabel agents."
echo "The example integrates with multiple external services through a unified MCP gateway."
echo

print_step "1. Architecture Overview"
echo
cat << 'EOF'
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Embabel       │    │   MCP Gateway   │    │ External APIs   │
│   Agents        │────│   (Port 9011)   │────│ & Services      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                       │                       │
    Research Agent         Gateway Routes         Brave Search
    Web Automation         Tool Calls            Wikipedia
    GitHub Integration     Load Balancing        GitHub API
                          Error Handling        Google Maps
                          Authentication       Puppeteer
EOF
echo

print_step "2. Available External MCP Services"
echo
print_demo "Web Search (Brave): Real-time web search and current information"
print_code "  Tool: brave_web_search"
echo
print_demo "Wikipedia: Encyclopedic knowledge and background information"
print_code "  Tools: wikipedia-mcp:search, wikipedia-mcp:get_article"
echo
print_demo "GitHub Integration: Repository management and collaboration"
print_code "  Tools: github:list_issues, github:create_issue, github:list_pull_requests"
echo
print_demo "Web Automation (Puppeteer): Browser automation and web interaction"
print_code "  Tools: puppeteer:screenshot, puppeteer:get_page_content, puppeteer:click_element"
echo
print_demo "Google Maps: Geographic information and mapping"
print_code "  Tools: google-maps:search, google-maps:directions, google-maps:place_details"
echo

print_step "3. Example Agent Implementation"
echo
print_demo "Research Agent that uses multiple MCP services:"
echo
print_code '@Agent(description = "Research assistant using external MCP servers")
class ExternalMcpResearchAgent {
    
    @Action
    fun comprehensiveResearch(userInput: UserInput): ComprehensiveResearchReport {
        return using(
            llm = LlmOptions(criteria = byName("gpt-4o")),
            promptContributors = listOf(researcherPersona)
        ).withToolGroups(
            setOf(
                CoreToolGroups.WEB,     // Web search, Wikipedia
                CoreToolGroups.MAPS,    // Google Maps
                CoreToolGroups.DEV,     // GitHub integration
            )
        ).create(
            prompt = """
            Conduct comprehensive research using all available external MCP services:
            
            Topic: ${userInput.content}
            
            1. Web Search: Use brave web search for current information
            2. Wikipedia: Search for encyclopedic background
            3. GitHub: Look for related open source projects
            4. Maps: Provide geographic context if relevant
            """
        )
    }
}'

echo

print_step "4. Configuration Example"
echo
print_demo "Main application configuration:"
echo
print_code '@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
    localModels = [LocalModels.OLLAMA],
    mcpServers = [
        McpServers.DOCKER,           // Local Docker integration
        McpServers.DOCKER_DESKTOP,   // Docker Desktop integration  
        McpServers.GATEWAY           // External MCP gateway
    ],
)
class ExternalMcpApp'

echo

print_step "5. Usage Examples"
echo
print_demo "Once the application is running, you can make API calls:"
echo

echo "Research Request:"
print_code 'curl "http://localhost:8080/run/agents/by-intent?intent=Research the latest developments in quantum computing"'
echo

echo "GitHub Repository Analysis:"
print_code 'curl "http://localhost:8080/run/agents/by-intent?intent=Analyze the issues in the microsoft/vscode repository"'
echo

echo "Web Automation:"
print_code 'curl "http://localhost:8080/run/agents/by-intent?intent=Take a screenshot of https://github.com and describe the layout"'
echo

echo "Travel Research with Maps:"
print_code 'curl "http://localhost:8080/run/agents/by-intent?intent=Research tourist attractions in Tokyo and provide map locations"'
echo

print_step "6. Setup Process"
echo
print_demo "To run this example:"
echo
echo "1. Configure API keys:"
print_code "   cp .mcp.env.example .mcp.env"
print_code "   # Edit .mcp.env with your API keys"
echo

echo "2. Start MCP services:"
print_code "   docker-compose -f compose.mcp.yml up -d"
echo

echo "3. Run the application:"
print_code "   ./mvnw spring-boot:run -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.externalmcp.ExternalMcpApp"
echo

echo "4. Or use the helper script:"
print_code "   ./run-external-mcp-example.sh setup"
print_code "   ./run-external-mcp-example.sh start"
echo

print_step "7. Key Benefits"
echo
print_demo "✓ Unified Interface: Access multiple external services through one gateway"
print_demo "✓ Service Abstraction: Agents don't need to know about individual APIs"
print_demo "✓ Error Handling: Built-in retry logic and graceful degradation"
print_demo "✓ Authentication: Centralized API key management"
print_demo "✓ Load Balancing: Distribute requests across service instances"
print_demo "✓ Monitoring: Centralized logging and metrics"
echo

print_step "8. File Structure"
echo
print_demo "The example includes:"
echo
print_code "src/main/kotlin/com/jabaddon/learning/embabel/externalmcp/
├── ExternalMcpApp.kt                    # Main application
├── ExternalMcpConfiguration.kt          # Configuration classes
├── agents/
│   ├── ExternalMcpResearchAgent.kt      # Multi-source research
│   ├── WebAutomationAgent.kt            # Browser automation
│   └── GitHubIntegrationAgent.kt        # Repository management
└── README.md                            # Detailed documentation

src/main/resources/
└── application-externalmcp.yml          # Application configuration

src/test/kotlin/com/jabaddon/learning/embabel/externalmcp/
└── ExternalMcpAppTest.kt               # Unit tests

Root files:
├── .mcp.env.example                     # API key template
├── compose.mcp.yml                      # MCP gateway configuration
└── run-external-mcp-example.sh         # Setup helper script"

echo

print_header "Demo Complete!"
echo "This example demonstrates a comprehensive integration with external MCP servers,"
echo "providing agents with access to web search, Wikipedia, GitHub, web automation,"
echo "and mapping services through a unified interface."
echo
echo "For more details, see:"
echo "- src/main/kotlin/com/jabaddon/learning/embabel/externalmcp/README.md"
echo "- Main project README.md"
echo