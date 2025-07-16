pipeline {
    agent any

    tools {
        maven 'MAVEN' // Make sure "MAVEN" is configured in Jenkins global tool config
    }

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=${HOME}/.m2/repository"
    }

    stages {
        stage('Build without Tests') {
            steps {
                timeout(time: 20, unit: 'MINUTES') {
                    echo ' Starting Maven build with tests skipped and using local .m2 cache...'
                    sh 'mvn clean install -Dmaven.test.skip=true'
                }
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
