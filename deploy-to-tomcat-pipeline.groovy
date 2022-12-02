pipeline {
    agent any
    stages{
        stage('code-pull') {
            steps {
                git credentialsId: 'mohit', url: 'git@github.com:mohit-decoder/onlinebookstore.git'
            }
        }
        stage('build-maven') {
            steps {
               // sh 'sudo apt update -y'
               // sh 'sudo apt install maven -y'
                sh 'mvn clean package'
            }
        }
        stage('artifact to s3-bucket') {
            steps {
                withAWS(credentials: 'ubuntu', region: 'us-west-2') {
               // sh 'sudo apt update -y'
               // sh 'sudo apt install awscli -y'
                sh 'aws s3 ls'
               // sh 'aws s3 mb s3://bahubali-bucket-builder --region us-west-2'
               // sh 'sudo mv /var/lib/jenkins/workspace/maven-project/target/onlinebookstore.war /tmp/onlinebookstore${BUILD_ID}.war'
               // sh 'aws s3 cp /tmp/onlinebookstore${BUILD_ID}.war s3://bahubali-bucket-builder'    
                }
            }
        }
        stage('deploy to tomcat-server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'mohit', keyFileVariable: 'private-key', usernameVariable: 'mohit')]) {
                 sh'''
                    sudo ssh -i ${private-key}  -o StrictHostKeyChecking=no ubuntu@34.222.220.222<<EOF
                    aws s3 cp s3://bahubali-bucket-builder/onlinebookstore-${BUILD_ID}.war .
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.78/bin/apache-tomcat-8.5.78.tar.gz
                    sudo tar -xvf apache-tomcat-8.5.78.tar.gz -C /opt/
                    sudo sh /opt/apache-tomcat-8.5.78/bin/shutdown.sh
                    sudo cp -rv onlinebookstore${BUILD_ID}.war onlinebookstore.war
                    sudo cp -rv onlinebookstore.war /opt/apache-tomcat-8.5.78/webapps/
                    sudo sh /opt/apache-tomcat-8.5.78/bin/startup.sh
                '''    
                }
            }
        }
    }
}