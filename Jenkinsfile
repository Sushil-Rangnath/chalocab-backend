pipeline {
    agent any

    tools {
        maven 'MAVEN'     // This should match your configured Maven tool name in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/Sushil-Rangnath/chalocab-backend.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        // Add this only after testing that build works
        // stage('Deploy') {
        //     steps {
        //         sh 'scp target/*.jar ubuntu@<EC2_PUBLIC_IP>:/home/ubuntu/deployments/'
        //         sh 'ssh ubuntu@<EC2_PUBLIC_IP> "sudo systemctl restart chalocab.service"'
        //     }
        // }
    }
}