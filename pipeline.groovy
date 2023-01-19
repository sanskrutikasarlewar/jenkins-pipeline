pipeline {
    agent any 
    stages {
        stage('code-pull') {
            steps{
                git branch: 'main', credentialsId: 'Admin', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
               sh 'echo "code-pull"'
               sh 'echo "Hello"'
            
            }
        }
        stage('Build') {
            steps {
               echo 'Build'
            
            }
        }
        stage('test') {
            steps {
               echo 'Test'
            }
        }
    }
}
