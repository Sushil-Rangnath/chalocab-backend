pipeline {
  agent any

  tools {
    maven 'MAVEN'
  }

  environment {
    DEPLOY_DIR = '/opt/chalocab'
    JAR_NAME   = 'cabBooking-0.0.1-SNAPSHOT.jar'
    LOG_FILE   = '/opt/chalocab/app.log'
    SPRING_PROFILES_ACTIVE = 'prod'
  }

  stages {
    stage('Build') {
      steps {
        echo '🛠️ Building the project...'
        sh 'mvn -B clean package -Dmaven.test.skip=true'
      }
    }

    stage('Deploy') {
      steps {
        echo '🚀 Starting deploy...'
        withCredentials([
          string(credentialsId: 'RDS_URL', variable: 'SPRING_DATASOURCE_URL'),
          usernamePassword(credentialsId: 'RDS_USER', usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD'),
          string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
          string(credentialsId: 'FAST2SMS_API_KEY', variable: 'FAST2SMS_API_KEY'),
          string(credentialsId: 'FLYWAY_BASELINE_ON_MIGRATE', variable: 'SPRING_FLYWAY_BASELINE_ON_MIGRATE'),
          string(credentialsId: 'FLYWAY_BASELINE_VERSION', variable: 'SPRING_FLYWAY_BASELINE_VERSION')
        ]) {
          sh '''#!/bin/bash
set -euo pipefail

echo "📦 Ensuring deploy dir exists: ${DEPLOY_DIR}"
install -d -m 755 "${DEPLOY_DIR}"

echo "🧪 Checking built jar"
test -f "target/${JAR_NAME}"

echo "📤 Copying JAR to deploy location"
cp -f "target/${JAR_NAME}" "${DEPLOY_DIR}/app.jar"
chown jenkins:jenkins "${DEPLOY_DIR}/app.jar"
chmod 755 "${DEPLOY_DIR}/app.jar"

echo "📝 Writing runtime.env"
cat > "${DEPLOY_DIR}/runtime.env" <<EOF
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
JWT_SECRET=${JWT_SECRET}
FAST2SMS_API_KEY=${FAST2SMS_API_KEY}
SPRING_FLYWAY_BASELINE_ON_MIGRATE=${SPRING_FLYWAY_BASELINE_ON_MIGRATE}
SPRING_FLYWAY_BASELINE_VERSION=${SPRING_FLYWAY_BASELINE_VERSION}
EOF
chmod 640 "${DEPLOY_DIR}/runtime.env"
chown jenkins:jenkins "${DEPLOY_DIR}/runtime.env"

echo "🔄 Running deploy script..."
sudo -u jenkins bash -lc "${DEPLOY_DIR}/deploy.sh"

echo "✅ Deployment complete."
'''
        }
      }
    }
  }

  post {
    success { echo '✅ Build & Deployment Successful!' }
    failure { echo '❌ Build or Deployment Failed. Check logs on EC2.' }
  }
}
