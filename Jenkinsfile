pipeline {
    agent any

    tools {
        maven 'MAVEN' // Make sure this matches the name in Global Tools Config
    }

    stages {
        stage('Build') {
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh 'mvn clean install -Dmaven.test.skip=true'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deployment stage placeholder. Add your script here.'
                // Example:
                // sh 'scp target/cabBooking-0.0.1-SNAPSHOT.jar ubuntu@<EC2_IP>:/home/ubuntu/'
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deploy successful.'
        }
        failure {
            echo '❌ Build or Deploy failed.'
        }
    }
}
