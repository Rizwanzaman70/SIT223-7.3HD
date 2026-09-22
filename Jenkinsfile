pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building application...'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running automated unit and integration tests...'
                bat 'mvn test'
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application into JAR file...'
                bat 'mvn package -DskipTests'
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Running SonarQube code quality analysis...'

                withSonarQubeEnv('SonarQube-Local') {
                    bat '''
                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
                    -Dsonar.projectKey=SIT223-7.3HD ^
                    -Dsonar.projectName=SIT223-7.3HD
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building versioned Docker image...'

                bat '''
                docker build -t sit223-devops-app:%BUILD_NUMBER% .
                docker tag sit223-devops-app:%BUILD_NUMBER% sit223-devops-app:latest
                '''
            }
        }

        stage('Security') {
            steps {
                echo 'Running Trivy vulnerability scan...'

                bat '''
                docker save sit223-devops-app:%BUILD_NUMBER% -o sit223-devops-app.tar

                docker run --rm ^
                -v "%CD%:/workspace" ^
                aquasec/trivy:latest image ^
                --input /workspace/sit223-devops-app.tar ^
                --severity HIGH,CRITICAL ^
                --exit-code 0 ^
                --scanners vuln
                '''
            }
        }

        stage('Deployment') {
            steps {
                echo 'Deploying application container...'

                bat '''
                docker rm -f sit223-devops-running 2>NUL || echo No previous container found

                docker run -d ^
                --name sit223-devops-running ^
                -p 8082:8080 ^
                sit223-devops-app:%BUILD_NUMBER%

                timeout /t 5 /nobreak

                powershell -NoProfile -Command "$response = Invoke-WebRequest -UseBasicParsing http://localhost:8082/health; Write-Host 'Deployment health response:' $response.Content; if ($response.StatusCode -ne 200) { exit 1 }"
                '''
            }
        }

        stage('Release') {
            steps {
                echo "Creating automated versioned release ${BUILD_NUMBER}..."

                bat '''
                docker tag sit223-devops-app:%BUILD_NUMBER% sit223-devops-app:release-%BUILD_NUMBER%

                echo ========================================
                echo RELEASE CREATED SUCCESSFULLY
                echo Release Version: %BUILD_NUMBER%
                echo Docker Image: sit223-devops-app:release-%BUILD_NUMBER%
                echo ========================================

                docker images sit223-devops-app
                '''
            }
        }

        stage('Monitoring') {
            steps {
                echo 'Monitoring deployed application...'

                bat '''
                echo ========================================
                echo APPLICATION MONITORING
                echo ========================================

                powershell -NoProfile -Command "$response = Invoke-WebRequest -UseBasicParsing http://localhost:8082/health; Write-Host 'Health endpoint response:' $response.Content; Write-Host 'HTTP Status:' $response.StatusCode; if ($response.StatusCode -ne 200) { exit 1 }"

                echo ========================================
                echo Application monitoring check PASSED
                echo ========================================
                echo Container status:

                docker ps --filter "name=sit223-devops-running"

                echo ========================================
                '''
            }
        }
    }

    post {

        success {
            echo '========================================'
            echo 'PIPELINE COMPLETED SUCCESSFULLY!'
            echo "Release ${BUILD_NUMBER} completed successfully."
            echo 'Build: PASSED'
            echo 'Tests: PASSED'
            echo 'Code Quality: COMPLETED'
            echo 'Security Scan: COMPLETED'
            echo 'Deployment: SUCCESSFUL'
            echo 'Release: SUCCESSFUL'
            echo 'Monitoring: HEALTHY'
            echo '========================================'
        }

        failure {
            echo '========================================'
            echo 'PIPELINE FAILED'
            echo 'Check the failed Jenkins stage for details.'
            echo '========================================'
        }

        always {
            echo "Pipeline execution finished for build ${BUILD_NUMBER}."
        }
    }
}