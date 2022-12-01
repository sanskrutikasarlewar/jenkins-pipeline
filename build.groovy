pipeline {
    agent any
    stages {
        stage('code-pulling') {
            steps {
                git credentialsId: 'github', url: 'git@github.com:mohit-decoder/student-ui.git'
            }
        }
        stage('build-maven') {
            steps {
                sh 'mvn clean package' 
            }
        }
        stage('artifact to s3-bucket') {
            steps {
                withAWS(credentials: 'mohit', region: 'us-west-2') {
                sh 'sudo apt update -y'
                sh 'sudo apt install awscli -y'
                sh 'sudo s3 ls'
                sh 'aws s3 mb s3://chorbucket-builder --region us-west-2'        
            }
            }
        }
    }
}
