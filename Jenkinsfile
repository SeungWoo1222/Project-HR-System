pipeline {
    agent any

    stages {
        stage('Build JAR') {
            steps {
                sh './gradlew clean build'
            }
        }
    }
}
