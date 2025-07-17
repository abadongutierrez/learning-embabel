#!/bin/bash

# External MCP Example Runner Script
# This script helps set up and run the external MCP server example

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if required tools are installed
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is required but not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is required but not installed. Please install Docker Compose first."
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        print_error "Java is required but not installed. Please install Java 21+ first."
        exit 1
    fi
    
    print_success "All prerequisites are installed."
}

# Function to set up MCP environment
setup_mcp_env() {
    print_status "Setting up MCP environment..."
    
    if [ ! -f "$PROJECT_ROOT/.mcp.env" ]; then
        if [ -f "$PROJECT_ROOT/.mcp.env.example" ]; then
            cp "$PROJECT_ROOT/.mcp.env.example" "$PROJECT_ROOT/.mcp.env"
            print_warning "Created .mcp.env from example. Please edit it with your API keys:"
            print_warning "  - BRAVE_API_KEY"
            print_warning "  - GITHUB_TOKEN"
            print_warning "  - GOOGLE_MAPS_API_KEY"
            print_warning "  - OPENAI_API_KEY (if needed)"
            echo
            print_warning "Edit the file now? (y/n)"
            read -r response
            if [[ "$response" =~ ^[Yy]$ ]]; then
                ${EDITOR:-nano} "$PROJECT_ROOT/.mcp.env"
            fi
        else
            print_error ".mcp.env.example not found. Cannot create MCP environment file."
            exit 1
        fi
    else
        print_success "MCP environment file already exists."
    fi
}

# Function to start MCP services
start_mcp_services() {
    print_status "Starting MCP services..."
    
    cd "$PROJECT_ROOT"
    
    if [ ! -f "compose.mcp.yml" ]; then
        print_error "compose.mcp.yml not found. Cannot start MCP services."
        exit 1
    fi
    
    # Check if services are already running
    if docker-compose -f compose.mcp.yml ps | grep -q "Up"; then
        print_warning "MCP services appear to be already running."
        print_status "Restarting services..."
        docker-compose -f compose.mcp.yml restart
    else
        print_status "Starting MCP gateway and external services..."
        docker-compose -f compose.mcp.yml up -d
    fi
    
    # Wait for services to start
    print_status "Waiting for MCP gateway to start..."
    timeout=30
    while [ $timeout -gt 0 ]; do
        if curl -f -s http://localhost:9011/health > /dev/null 2>&1; then
            print_success "MCP gateway is running on port 9011."
            break
        fi
        sleep 1
        ((timeout--))
    done
    
    if [ $timeout -eq 0 ]; then
        print_warning "MCP gateway health check timed out. It may still be starting up."
    fi
}

# Function to run the external MCP example
run_external_mcp_example() {
    print_status "Running External MCP Example..."
    
    cd "$PROJECT_ROOT"
    
    # Make mvnw executable if it's not
    if [ ! -x "mvnw" ]; then
        chmod +x mvnw
    fi
    
    print_status "Starting the External MCP Example application..."
    print_status "The application will be available at: http://localhost:8080"
    print_status "Press Ctrl+C to stop the application."
    echo
    
    ./mvnw spring-boot:run \
        -Dspring-boot.run.main-class=com.jabaddon.learning.embabel.externalmcp.ExternalMcpApp \
        -Dspring.profiles.active=externalmcp
}

# Function to show example API calls
show_examples() {
    echo
    print_status "Example API calls to try:"
    echo
    echo "1. Web Research:"
    echo "   curl \"http://localhost:8080/run/agents/by-intent?intent=Research the latest developments in AI technology\""
    echo
    echo "2. GitHub Repository Analysis:"
    echo "   curl \"http://localhost:8080/run/agents/by-intent?intent=Analyze the issues in the tensorflow/tensorflow repository\""
    echo
    echo "3. Web Automation:"
    echo "   curl \"http://localhost:8080/run/agents/by-intent?intent=Take a screenshot of https://example.com and describe the content\""
    echo
    echo "4. List Available Agents:"
    echo "   curl \"http://localhost:8080/agents\""
    echo
}

# Function to stop MCP services
stop_mcp_services() {
    print_status "Stopping MCP services..."
    cd "$PROJECT_ROOT"
    docker-compose -f compose.mcp.yml down
    print_success "MCP services stopped."
}

# Function to show status
show_status() {
    print_status "Checking status..."
    
    # Check MCP services
    cd "$PROJECT_ROOT"
    if docker-compose -f compose.mcp.yml ps | grep -q "Up"; then
        print_success "MCP services are running."
        
        # Check gateway health
        if curl -f -s http://localhost:9011/health > /dev/null 2>&1; then
            print_success "MCP gateway is healthy."
        else
            print_warning "MCP gateway is not responding on port 9011."
        fi
    else
        print_warning "MCP services are not running."
    fi
    
    # Check if Java application is running
    if curl -f -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "External MCP Example application is running on port 8080."
    else
        print_warning "External MCP Example application is not running on port 8080."
    fi
}

# Main script logic
case "${1:-}" in
    "setup")
        check_prerequisites
        setup_mcp_env
        start_mcp_services
        print_success "Setup complete! Run '$0 start' to start the application."
        ;;
    "start")
        check_prerequisites
        setup_mcp_env
        start_mcp_services
        show_examples
        run_external_mcp_example
        ;;
    "stop")
        stop_mcp_services
        ;;
    "status")
        show_status
        ;;
    "examples")
        show_examples
        ;;
    *)
        echo "External MCP Example Runner"
        echo
        echo "Usage: $0 [command]"
        echo
        echo "Commands:"
        echo "  setup    - Set up MCP environment and start services"
        echo "  start    - Start MCP services and run the example application"
        echo "  stop     - Stop MCP services"
        echo "  status   - Check status of services"
        echo "  examples - Show example API calls"
        echo
        echo "Quick start:"
        echo "  $0 setup    # First time setup"
        echo "  $0 start    # Run the example"
        echo
        ;;
esac