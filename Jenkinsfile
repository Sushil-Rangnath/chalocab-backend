pipeline {
    agent any

    tools {
        maven 'MAVEN' // Must match Maven name in Jenkins > Global Tools
    }

    stages {
        stage('Build') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
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
