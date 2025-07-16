pipeline {
    agent any

    tools {
        maven 'MAVEN'
    }

    stages {
        stage('Build - Clean Install (No Tests)') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    sh 'mvn clean install -Dmaven.test.skip=true'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build successful without tests.'
        }
        failure {
            echo '❌ Build failed.'
        }
    }
}
