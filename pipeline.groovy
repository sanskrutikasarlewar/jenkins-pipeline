pipeline {
    agent any
    stages {
        stage('code-pull') {
            steps {
                git credentialsId: 'jeetu', url: 'git@github.com:mohit-decoder/student-ui.git'
            }
        }
        stage('build') {
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'mvn clean package'
            }
        }
        stage('artifact-to-s3') {
            steps {
                withAWS(credentials: 'key', region: 'us-east-2') {
                    sh 'sudo apt update -y'
                sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" '
                sh 'sudo apt install unzip -y'
                sh 'unzip awscliv2.zip'
                sh 'sudo ./aws/install'
                sh 'aws s3 ls'
                sh 'aws s3 mb s3://artifact11-bucket-builder --region us-west-2'
                sh 'sudo mv /var/lib/jenkins/workspace/pipeline/target/studentapp-2.2-SNAPSHOT.war /tmp/student-${BUILD_ID}.war'
                sh 'aws s3 cp /tmp/student-${BUILD_ID}.war  s3://artifact11-bucket-builder'
                } 
            }
        }
        stage('deploy-to-tomcat-server') {
            steps {
            withCredentials([sshUserPrivateKey(credentialsId: 'jeet', keyFileVariable: 'tommy', usernameVariable: 'mohit')]) {
                sh'''
    sudo curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    sudo unzip awscliv2.zip
    sudo ./aws/install
    sudo ssh -i ${tommy} -T -o StrictHostKeyChecking=no ec2-user@34.212.87.217<<EOF
    aws s3 cp s3://artifact11-bucket-builder/student-${BUILD_ID}.war /home/ec2-user/
    curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.69/bin/apache-tomcat-9.0.69.tar.gz
    sudo tar -xzvf apache-tomcat-9.0.69.tar.gz -C /opt/
    sudo sudo chmod 777 /opt/apache-tomcat-9.0.69/*  && sudo chown ec2-user: /opt/apache-tomcat-9.0.69/*
    sudo cp -rv /opt/student-${BUILD_ID}.war studentapp.war
    sudo cp -rv studentapp.war /opt/apache-tomcat-9.0.69/webapps/
    sudo sh /opt/apache-tomcat-9.0.69/bin/startup.sh
                '''
                }
            }
        }
    }
}