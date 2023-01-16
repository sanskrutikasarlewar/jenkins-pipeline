pipeline {
    agent any
    stages {
        stage('code-pull') {
            steps {
               git branch: 'main', credentialsId: 'Sanskruti', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
            }
        }
    }
}
