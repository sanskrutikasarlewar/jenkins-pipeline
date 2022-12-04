pipeline {
    agent any
    stages{
        stage('code-pull') {
            steps {
                git credentialsId: 'mohit', url: 'git@github.com:mohit-decoder/onlinebookstore.git'
            }
        }
        stage('build-maven') {
            steps{
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'mvn clean package'
            }
        }
        stage('artifact to s3-bucket') {
            steps {
                withAWS(credentials: 'ubuntu', region: 'us-west-2') {
               sh 'sudo apt update -y'
               sh 'sudo apt install awscli -y'
               sh 'aws s3 ls'
               sh 'aws s3 mb s3://bahubali-bucket-builder --region us-west-2'
               sh 'sudo mv /var/lib/jenkins/workspace/maven-project/target/onlinebookstore.war /tmp/onlinebookstore${BUILD_ID}.war'
               sh 'aws s3 cp /tmp/onlinebookstore${BUILD_ID}.war s3://bahubali-bucket-builder'    
                }
            }
        }
        stage('deploy to tomcat-server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'private-key', keyFileVariable: 'global', usernameVariable: 'ubuntu')]) {
                sh 'sudo ssh -i ${global} -T -o StrictHostKeyChecking=no ubuntu@34.222.220.222 <<EOF'
                sh 'sudo apt update -y'
                sh 'sudo apt install default-jdk -y' 
                sh 'sudo apt install awscli -y' 
                sh 'curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.84/bin/apache-tomcat-8.5.84.zip'
                sh 'sudo apt install unzip -y'
                sh 'unzip apache-tomcat-8.5.84.zip'
                sh 'sudo mv apache-tomcat-8.5.84 /opt/'
                sh 'chmod 777 /opt/apache-tomcat-8.5.84/*'
                sh 'sudo chown ubuntu: /opt/apache-tomcat-8.5.84/* '
                sh 'sudo sh /opt/apache-tomcat-8.5.84/bin/shutdown.sh' 
                sh 'aws s3 cp s3://bahubali-bucket-builder/onlinebookstore${BUILD_ID}.war'
                    }   
                }
            }
        }
    }