pipeline {
    agent node-agent
    stages {
        stage('code-pull') {
            steps {
               git branch: 'main', credentialsId: 'Sanskruti', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
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
