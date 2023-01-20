pipeline {
    agent {
        label ('ECS1')
    }
    stages{
        stage('code-pull') {
            steps {
             git credentialsId: 'sk', url: 'https://github.com/sanskrutikasarlewar/student-ui.git'
                sh '''
                sudo apt update -y
                sudo apt install git -y
                '''
            }
        }
        stage('code-build') {
            steps {
                sh ''' sudo apt update -y
                sudo apt install maven -y
                 sudo mvn clean package
                 '''
            }
        } 
    }   
}