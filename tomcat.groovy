pipeline {
    agent {
        node ('bigboss-agent')
    }
    stages{
        stage('code-pull') {
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
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