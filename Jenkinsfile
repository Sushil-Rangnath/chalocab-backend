pipeline {
  agent any

  tools {
    maven 'MAVEN'
  }

  environment {
    DEPLOY_DIR = '/opt/chalocab'
    JAR_NAME   = 'cabBooking-0.0.1-SNAPSHOT.jar'
    LOG_FILE   = '/opt/chalocab/app.log'
    SPRING_PROFILES_ACTIVE = 'prod' // <<< ensure prod profile
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
        withCredentials([
          string(credentialsId: 'RDS_URL',           variable: 'SPRING_DATASOURCE_URL'),
          usernamePassword(credentialsId: 'RDS_USER', usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD'),
          string(credentialsId: 'JWT_SECRET',        variable: 'JWT_SECRET'),
          string(credentialsId: 'FAST2SMS_API_KEY',  variable: 'FAST2SMS_API_KEY'),
          string(credentialsId: 'FLYWAY_BASELINE_ON_MIGRATE', variable: 'SPRING_FLYWAY_BASELINE_ON_MIGRATE'), // <<< REMOVE after first prod deploy
          string(credentialsId: 'FLYWAY_BASELINE_VERSION',    variable: 'SPRING_FLYWAY_BASELINE_VERSION')    // <<< REMOVE after first prod deploy
        ]) {
          sh """
            set -e
            install -d -m 755 ${DEPLOY_DIR}
            cp target/${JAR_NAME} ${DEPLOY_DIR}/app.jar

            # Write runtime.env for systemd/nohup to consume
            cat > ${DEPLOY_DIR}/runtime.env <<'EOF'
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
JWT_SECRET=${JWT_SECRET}
FAST2SMS_API_KEY=${FAST2SMS_API_KEY}
SPRING_FLYWAY_BASELINE_ON_MIGRATE=${SPRING_FLYWAY_BASELINE_ON_MIGRATE}  # <<< REMOVE after first prod deploy
SPRING_FLYWAY_BASELINE_VERSION=${SPRING_FLYWAY_BASELINE_VERSION}        # <<< REMOVE after first prod deploy
EOF
            chmod 600 ${DEPLOY_DIR}/runtime.env

            # Kick the deploy script
            bash ${DEPLOY_DIR}/deploy.sh
          """
        }
      }
    }
  }

  post {
    success { echo '✅ Build & Deployment Successful!' }
    failure { echo '❌ Build or Deployment Failed.' }
  }
}
