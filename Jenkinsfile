pipeline {
    agent any

    parameters {
        gitParameter(name: 'TAG', defaultValue: '', type: 'PT_TAG', description: 'Тег версии в Git')

    }

    tools {
        jdk 'openjdk-11'
    }

    stages {
        stage('checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: 'refs/tags/${TAG}']]
                ])
            }
        }
        stage('build') {
            steps {
                sh "./gradlew --no-daemon clean build jar"
            }
        }
        stage('deploy') {
            steps {
                sh "docker-compose up -d --build"
            }
        }
    }
}
