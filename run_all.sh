 
#   Copyright 2025 Avishek Chanda

#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at

#       http://www.apache.org/licenses/LICENSE-2.0

#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
build_project() {
    echo "Building the project..."
    mvn clean compile
    if [ $? -ne 0 ]; then
        echo "Build failed"
        exit 1
    fi
}

# Function to run single process version
run_single_process() {
    echo "Running single process version..."
    mvn exec:java@single-process
}

# Function to run multi-process version
run_multi_process() {
    echo "Running multi-process version..."

    # Start responder in background
    echo "Starting responder process..."
    mvn exec:java@multi-process-responder &
    RESPONDER_PID=$!

    # Wait for responder to start
    sleep 2

    # Start initiator
    echo "Starting initiator process..."
    mvn exec:java@multi-process-initiator

    # Wait for responder to finish
    wait $RESPONDER_PID 2>/dev/null

    echo "Multi-process execution completed"
}

# Function to run tests
run_tests() {
    echo "Running tests..."
    mvn test
}

# Main menu
while true; do
    echo ""
    echo "Player Communication Application"
    echo "1. Build project"
    echo "2. Run single process version"
    echo "3. Run multi-process version"
    echo "4. Run tests"
    echo "5. Exit"
    read -p "Enter your choice: " choice

    case $choice in
    1) build_project ;;
    2) run_single_process ;;
    3) run_multi_process ;;
    4) run_tests ;;
    5)
        echo "Exiting..."
        exit 0
        ;;
    *) echo "Invalid option" ;;
    esac
done
