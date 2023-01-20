pipeline {
    agent {
        label 'tomcat-server'
    }
    stages{
        stage('code-pull'){
            steps {
                git credentialsId: 'sk', url: 'https://github.com/sanskrutikasarlewar/student-ui.git'
                sh 'sudo apt update -y'
                sh 'sudo apt install git -y'
            }
        }
        stage('code-build'){
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'sudo mvn clean package'
            }
        } 
    }   
}