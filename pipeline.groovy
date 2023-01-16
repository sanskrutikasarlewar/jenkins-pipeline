pipeline {
    agent any
    stages {
        stage('code-pull') {
            steps {
               git branch: 'main', credentialsId: 'Sanskruti', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
            }
        }
        stage('build') {
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'mvn clean package'
            }
        }
    }
}
