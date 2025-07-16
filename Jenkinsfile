pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven 'MAVEN'
    }

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=${HOME}/.m2/repository"
    }

    stages {
        stage('Build without Tests') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                echo '📦 Starting Maven build'
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }
    }

    post {
        success {
            echo '✅ Build successful. Tests were skipped.'
        }
        failure {
            echo '❌ Build failed.'
        }
        always {
            echo '🔁 Pipeline finished.'
        }
    }
}
