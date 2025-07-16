pipeline {
    agent any

    tools {
        maven 'MAVEN'
    }

    stages {
        stage('Build without Tests') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
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
    }
}
