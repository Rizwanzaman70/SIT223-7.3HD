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
                bat 'docker build -t sit223-devops-app:%BUILD_NUMBER% .'
                bat 'docker tag sit223-devops-app:%BUILD_NUMBER% sit223-devops-app:latest'
            }
        }

        stage('Security') {
            steps {
                bat '''
                docker run --rm ^
                -v /var/run/docker.sock:/var/run/docker.sock ^
                aquasec/trivy:latest image ^
                --severity HIGH,CRITICAL ^
                --exit-code 1 ^
                --ignore-unfixed ^
                sit223-devops-app:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }
    }
}