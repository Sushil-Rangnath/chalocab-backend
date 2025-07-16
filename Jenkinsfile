pipeline {
    agent any

    tools {
        maven 'MAVEN'  // Make sure Jenkins tool name matches exactly (case-sensitive)
    }

   
    stages {
        stage('Build - Maven Clean Install') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        // Optional: Add Deployment Stage Here
        // stage('Deploy to EC2') {
        //     steps {
        //         sh '''
        //             scp -i /path/to/key.pem target/cabBooking-0.0.1-SNAPSHOT.jar ubuntu@<EC2_IP>:/home/ubuntu/
        //             ssh -i /path/to/key.pem ubuntu@<EC2_IP> 'java -jar /home/ubuntu/cabBooking-0.0.1-SNAPSHOT.jar &'
        //         '''
        //     }
        // }
    }

    post {
        success {
            echo ' Build completed successfully.'
        }
        failure {
            echo ' Build failed.'
        }
    }
}
