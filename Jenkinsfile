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
          string(credentialsId: 'FLYWAY_BASELINE_ON_MIGRATE', variable: 'SPRING_FLYWAY_BASELINE_ON_MIGRATE'),
          string(credentialsId: 'FLYWAY_BASELINE_VERSION',    variable: 'SPRING_FLYWAY_BASELINE_VERSION')
        ]) {
          sh '''#!/bin/bash
set -euo pipefail

echo "📦 Ensuring deploy dir exists: ${DEPLOY_DIR}"
sudo install -d -m 755 "${DEPLOY_DIR}"

echo "📁 Listing target directory:"
ls -lah target || true

echo "🧪 Verifying JAR exists: target/${JAR_NAME}"
test -f "target/${JAR_NAME}"

echo "📤 Copying JAR to ${DEPLOY_DIR}/app.jar"
sudo cp -f "target/${JAR_NAME}" "${DEPLOY_DIR}/app.jar"
sudo chown jenkins:jenkins "${DEPLOY_DIR}/app.jar"
sudo chmod 640 "${DEPLOY_DIR}/app.jar"

echo "📝 Writing runtime.env (secrets are masked in Jenkins console)"
cat > /tmp/runtime.env.$$ <<EOF
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
JWT_SECRET=${JWT_SECRET}
FAST2SMS_API_KEY=${FAST2SMS_API_KEY}
SPRING_FLYWAY_BASELINE_ON_MIGRATE=${SPRING_FLYWAY_BASELINE_ON_MIGRATE}
SPRING_FLYWAY_BASELINE_VERSION=${SPRING_FLYWAY_BASELINE_VERSION}
EOF

sudo chown jenkins:jenkins /tmp/runtime.env.$$
sudo chmod 640 /tmp/runtime.env.$$
sudo mv /tmp/runtime.env.$$ "${DEPLOY_DIR}/runtime.env"
echo "🔐 runtime.env written (owner=jenkins:jenkins, perms=640)."

echo "🔍 runtime.env preview (masked):"
sed -E 's/(SPRING_DATASOURCE_PASSWORD=).*/\1*****/; s/(JWT_SECRET=).*/\1*****/; s/(FAST2SMS_API_KEY=).*/\1*****/' "${DEPLOY_DIR}/runtime.env" | cat

# ensure start wrapper exists (idempotent)
if [ ! -f "${DEPLOY_DIR}/start.sh" ]; then
  sudo tee "${DEPLOY_DIR}/start.sh" > /dev/null <<'STARTSH'
#!/usr/bin/env bash
set -o allexport
if [ -f /opt/chalocab/runtime.env ]; then
  # shellcheck disable=SC1090
  source /opt/chalocab/runtime.env
fi
set +o allexport
exec java -jar /opt/chalocab/app.jar
STARTSH
  sudo chmod +x "${DEPLOY_DIR}/start.sh"
  sudo chown jenkins:jenkins "${DEPLOY_DIR}/start.sh"
fi

echo "🚀 Invoking server-side deploy script (if present)"
# run deploy.sh (it should be idempotent); we do this as jenkins if the script expects that
if [ -f "${DEPLOY_DIR}/deploy.sh" ]; then
  sudo chown jenkins:jenkins "${DEPLOY_DIR}/deploy.sh" || true
  sudo chmod +x "${DEPLOY_DIR}/deploy.sh" || true
  sudo -u jenkins bash -lc "bash ${DEPLOY_DIR}/deploy.sh || true"
fi

# Start the app via start.sh as jenkins (safe, idempotent)
sudo -u jenkins nohup "${DEPLOY_DIR}/start.sh" > "${LOG_FILE}" 2>&1 &

sleep 5

echo "📜 Tail last 120 lines of app log:"
sudo tail -n 120 "${LOG_FILE}" || true
'''
        }
      }
    }

    stage('Post-deploy Verification') {
      steps {
        sh '''
echo "🔎 Checking port 9090 and java process"
sudo ss -ltnp | grep ':9090' || echo "port 9090 not listening"
sudo ps aux | grep java | grep -v grep || true
sudo tail -n 80 /opt/chalocab/app.log || true
'''
      }
    }
  }

  post {
    success { echo '✅ Build & Deployment Successful!' }
    failure {
      echo '❌ Build or Deployment Failed. Check console output and /opt/chalocab/app.log on the host.'
    }
  }
}
