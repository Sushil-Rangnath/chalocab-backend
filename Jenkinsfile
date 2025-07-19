pipeline {
    agent any

    tools {
        maven 'MAVEN' // This must match the name in Jenkins Global Tools Config
    }

    environment {
        EC2_USER = 'ubuntu'
        EC2_IP = '13.203.122.224'
        REMOTE_DIR = '/opt/chalocab'
        DEPLOY_SCRIPT = '/opt/chalocab/deploy.sh'
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

                // Copy JAR to EC2
                sh """
                    scp target/${JAR_NAME} ${EC2_USER}@${EC2_IP}:${REMOTE_DIR}/
                """

                // Execute deployment script on EC2
                sh """
                    ssh ${EC2_USER}@${EC2_IP} 'bash ${DEPLOY_SCRIPT}'
                """
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
