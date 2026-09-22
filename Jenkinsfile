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
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
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
                bat 'mvn package -DskipTests'
            }
        }

        stage('Code Quality') {
            steps {
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
                bat '''
                docker build -t sit223-devops-app:%BUILD_NUMBER% .
                docker tag sit223-devops-app:%BUILD_NUMBER% sit223-devops-app:latest
                '''
            }
        }

        stage('Security') {
            steps {
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
                bat '''
                docker rm -f sit223-devops-running 2>NUL || echo No previous container found

                docker run -d ^
                --name sit223-devops-running ^
                -p 8082:8080 ^
                sit223-devops-app:%BUILD_NUMBER%

                timeout /t 5 /nobreak

                powershell -Command "$response = Invoke-WebRequest -UseBasicParsing http://localhost:8082/health; Write-Host $response.Content; if ($response.StatusCode -ne 200) { exit 1 }"
                '''
            }
        }

        stage('Release') {
            steps {
                bat '''
                echo Creating release for Jenkins Build %BUILD_NUMBER%

                docker tag sit223-devops-app:%BUILD_NUMBER% sit223-devops-app:release-%BUILD_NUMBER%

                echo ----------------------------------------
                echo Release created successfully
                echo Release version: release-%BUILD_NUMBER%
                echo Docker image: sit223-devops-app:release-%BUILD_NUMBER%
                echo Jenkins build: %BUILD_NUMBER%
                echo ----------------------------------------

                docker images sit223-devops-app
                '''
            }
        }

        stage('Monitoring') {
            steps {
                bat '''
                echo ========================================
                echo Monitoring deployed application
                echo ========================================

                powershell -Command ^
                "$failed = $false; ^
                for ($i = 1; $i -le 3; $i++) { ^
                    Write-Host ('Health check ' + $i + ' of 3'); ^
                    try { ^
                        $response = Invoke-WebRequest -UseBasicParsing http://localhost:8082/health; ^
                        Write-Host ('HTTP Status: ' + $response.StatusCode); ^
                        Write-Host ('Response: ' + $response.Content); ^
                        if ($response.StatusCode -ne 200) { $failed = $true }; ^
                    } catch { ^
                        Write-Host ('Monitoring failure: ' + $_.Exception.Message); ^
                        $failed = $true; ^
                    }; ^
                    if ($i -lt 3) { Start-Sleep -Seconds 2 } ^
                }; ^
                if ($failed) { exit 1 }; ^
                Write-Host 'Application monitoring completed successfully.'"
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            echo "Release ${env.BUILD_NUMBER} completed successfully."
            echo 'Deployment health monitoring passed.'
        }

        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }
    }
}