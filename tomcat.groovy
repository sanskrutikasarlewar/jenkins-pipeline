pipeline {
    agent {
        node ('bigboss-agent')
    }
    stages{
        stage('code-pull'){
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git 'https://github.com/sanskrutikasarlewar/student-ui.git'
            }
        }
        stage('code-build'){
            steps {
                sh '''
                sudo apt-get update -y
                sudo apt-get install maven -y
                mvn clean package
                '''
            }
        }
        stage('copy-s3'){
            steps{
                sh '''
                sudo apt-get update -y
                #sudo apt-get install unzip -y
                #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                #unzip awscliv2.zip
                #sudo ./aws/install
                #aws s3 ls
                sudo mv /home/ubuntu/workspace/pipe/target/studentapp-2.2-SNAPSHOT.war /tmp/student-${BUILD_ID}.war
                aws s3 cp /tmp/student-${BUILD_ID}.war s3://student-app-artifact1 
                '''
            }
        }
        stage('tomcat-server-build'){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId: 'tommy', keyFileVariable: 'tommy', usernameVariable: 'ubuntu')]) {

                    sh '''
                    ssh -i ${tommy} -o StrictHostKeyChecking=no ubuntu@54.206.228.25<<EOF
                    sudo apt-get update -y
                    ls /
                    #sudo apt-get install unzip git -y
                    #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    #unzip awscliv2.zip
                    #sudo ./aws/install
                    $PWD
                    aws s3 cp s3://student-app-artifact1/student-${BUILD_ID}.war .
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                    sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /opt/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/shutdown.sh
                    sudo cp -rv student-${BUILD_ID}.war studentapp.war
                    sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.85/webapps/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/startup.sh
               '''
                }
            }
        }
    }   
}