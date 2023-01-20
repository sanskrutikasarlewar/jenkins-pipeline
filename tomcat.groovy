pipeline {
    agent {
        label ('ECS1')
    }
    stages{
        stage('code-pull') {
            steps {
               
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git 'https://github.com/sanskrutikasarlewar/student-ui.git'
            }
        }
        stage('code-build') {
            steps {
                sh ''' sudo apt-get update -y
                sudo apt-get install maven -y
                 sudo mvn clean package
                 '''
            }
        } 
    }   
}