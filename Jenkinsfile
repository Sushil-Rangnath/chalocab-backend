pipeline {
  agent any

  tools {
    maven 'MAVEN'
  }

  environment {
    DEPLOY_DIR = '/opt/chalocab'
    JAR_NAME   = 'cabBooking-0.0.1-SNAPSHOT.jar'
    LOG_FILE   = '/opt/chalocab/app.log'
    SPRING_PROFILES_ACTIVE = 'prod' // ensure prod profile
  }

  stages {
    stage('Build') {
      steps {
        echo '🛠️ Building the project...'
        // Batch mode (-B) = cleaner output; fail only on real errors
        sh 'mvn -B clean install -Dmaven.test.skip=true'
        echo '✅ Maven build finished, JAR should be in target/'
      }
    }

    stage('Deploy') {
      steps {
        echo '🔎 Starting Deploy stage...'
        withCredentials([
          string(credentialsId: 'RDS_URL',           variable: 'SPRING_DATASOURCE_URL'),
          usernamePassword(credentialsId: 'RDS_USER', usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD'),
          string(credentialsId: 'JWT_SECRET',        variable: 'JWT_SECRET'),
          string(credentialsId: 'FAST2SMS_API_KEY',  variable: 'FAST2SMS_API_KEY'),
          // ---------- FIRST PROD DEPLOY ONLY ----------
          string(credentialsId: 'FLYWAY_BASELINE_ON_MIGRATE', variable: 'SPRING_FLYWAY_BASELINE_ON_MIGRATE'), // <<< REMOVE after first prod deploy
          string(credentialsId: 'FLYWAY_BASELINE_VERSION',    variable: 'SPRING_FLYWAY_BASELINE_VERSION')    // <<< REMOVE after first prod deploy
          // --------------------------------------------
        ]) {
          // Use bash explicitly so 'set -o pipefail' works on agents where /bin/sh != bash
          sh '''#!/bin/bash
set -euo pipefail

echo "📦 Ensuring deploy dir exists: ${DEPLOY_DIR}"
install -d -m 755 "${DEPLOY_DIR}"

echo "📁 Listing target directory:"
ls -lah target || true

echo "🧪 Verifying JAR exists: target/${JAR_NAME}"
test -f "target/${JAR_NAME}"

echo "📤 Copying JAR to ${DEPLOY_DIR}/app.jar"
cp -f "target/${JAR_NAME}" "${DEPLOY_DIR}/app.jar"

echo "📝 Writing runtime.env (secrets will be masked in Jenkins logs)"
cat > "${DEPLOY_DIR}/runtime.env" <<'EOF'
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
JWT_SECRET=${JWT_SECRET}
FAST2SMS_API_KEY=${FAST2SMS_API_KEY}
SPRING_FLYWAY_BASELINE_ON_MIGRATE=${SPRING_FLYWAY_BASELINE_ON_MIGRATE}  # <<< REMOVE after first prod deploy
SPRING_FLYWAY_BASELINE_VERSION=${SPRING_FLYWAY_BASELINE_VERSION}        # <<< REMOVE after first prod deploy
EOF
chmod 600 "${DEPLOY_DIR}/runtime.env"

echo "🔍 runtime.env preview (masked):"
sed -E 's/(SPRING_DATASOURCE_PASSWORD=).*/\1*****/; s/(JWT_SECRET=).*/\1*****/; s/(FAST2SMS_API_KEY=).*/\1*****/' "${DEPLOY_DIR}/runtime.env" | cat

echo "🚀 Running deploy script"
bash "${DEPLOY_DIR}/deploy.sh"

echo "📜 Tail last 80 lines of app log:"
tail -n 80 "${LOG_FILE}" || true
'''
        }
      }
    }
  }

  post {
    success { echo '✅ Build & Deployment Successful!' }
    failure { echo '❌ Build or Deployment Failed.' }
  }
}
