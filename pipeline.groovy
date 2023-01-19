pipeline {
    agent any {
        git branch: 'main', credentialsId: 'Admin', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
        //label ('ECS-LABEL')
    }
    stages {
        stage('code-pull') {
            steps{
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
