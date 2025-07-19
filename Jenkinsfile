pipeline {
    agent any

    tools {
        maven 'MAVEN' // Match with Jenkins Global Tool Configuration
    }

    environment {
        DEPLOY_DIR = '/opt/chalocab'
        JAR_NAME = 'cabBooking-0.0.1-SNAPSHOT.jar'
        LOG_FILE = '/opt/chalocab/app.log'
    }

    stages {
        stage('Build') {
            steps {
                echo '🛠️ Building the project...'
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deploying application...'
                sh """
                    cp target/${JAR_NAME} ${DEPLOY_DIR}/
                    bash ${DEPLOY_DIR}/deploy.sh
                """
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deployment Successful!'
        }
        failure {
            echo '❌ Build or Deployment Failed.'
        }
    }
}
