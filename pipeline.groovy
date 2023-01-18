pipeline {
    agent {
        node ('node-agent')
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
