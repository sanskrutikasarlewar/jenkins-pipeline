pipeline {
    agent {
        node ('bigboss-agent')
    }
    stages{
        stage('code-pull'){
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git "https://github.com/sanskrutikasarlewar/student-ui.git"
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
                withCredentials([sshUserPrivateKey(credentialsId: 'bigboss-agent', keyFileVariable: 'tomcat', usernameVariable: 'ubuntu')]) {
                    sh'''
                    ssh -i ${tomcat} -o StrictHostKeyChecking=no ${ubuntu}@13.211.141.151
                    sudo apt-get update -y
                    #sudo apt-get install unzip git -y
                    #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    #unzip awscliv2.zip
                    #sudo ./aws/install
                    sudo apt-get install openjdk-11-jdk -y
                    aws s3 cp s3://student-app-artifact1/student-${BUILD_ID}.war .
                    cd /mnt/
                    sudo wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.zip
                    sudo unzip apache-tomcat-8.5.85.zip
                    sudo sh apache-tomcat-8.5.85/bin/shutdown.sh
                    sudo cp -rv ./student-${BUILD_ID}.war ./studentapp
                    sudo cp -rv ./studentapp /mnt/apache-tomcat-8.5.85/webapp/
                    sudo sh apache-tomcat-8.5.85/bin/startup.sh
                    '''
                }
            }
        }
    }   
}