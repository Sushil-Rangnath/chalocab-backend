pipeline {
    agent any

    tools {
        maven 'MAVEN' // Make sure this matches the name in Global Tools Config
    }

    environment {
        DEPLOY_DIR = '/opt/chalocab'
        JAR_NAME = 'cabBooking-0.0.1-SNAPSHOT.jar'
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
                echo '🚀 Deploying to EC2...'

                // Stop existing app (if running)
                sh 'pkill -f $JAR_NAME || true'

                // Copy the new JAR to the deploy directory
                sh 'cp target/$JAR_NAME $DEPLOY_DIR/'

                // Run your deployment script
                sh '$DEPLOY_DIR/deploy.sh'
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
