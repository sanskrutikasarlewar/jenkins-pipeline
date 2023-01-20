pipeline {
    agent {
        label ('ECS1')
    }
    stages{
        stage('code-pull') {
            steps {
               
                // sh 'sudo apt-get update -y'
                sh 'apt-get install git -y'
                git 'https://github.com/sanskrutikasarlewar/student-ui.git'
            }
        }
        stage('code-build') {
            steps {
                sh '''apt-get update -y
                apt-get install maven -y
                mvn clean package
                 '''
            }
        } 
    }   
}