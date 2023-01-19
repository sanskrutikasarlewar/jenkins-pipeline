pipeline {
    agent {
        node ('node-agent')
    }
    stages {
        stage('code-pull') {
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install git unzip maven -y'
                sh 'mvm clean package'
                sh 'wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.84/bin/apache-tomcat-8.5.84.zip'
                sh 'unzip apache-tomcat-8.5.84.zip'
                //sh 'mv apache-tomcat-8.5.84 /opt/'
                //sh 'cd /opt/apache-tomcat-8.5.84'
               git branch: 'main', credentialsId: 'Sanskruti', url: 'git@github.com:sanskrutikasarlewar/jenkins-pipeline.git'
               sh 'echo "Hello"'
            
            }
        }
        stage('Build') {
            steps {
               echo 'Build'
               sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                unzip awscliv2.zip
                sudo ./aws/install'
            
            }
        }
        stage('test') {
            steps {
               echo 'Test'
            }
        }
    }
}
