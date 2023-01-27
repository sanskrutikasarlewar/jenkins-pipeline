pipeline {
    agent {
        node ('agent')
    }
    parameters {
        string defaultValue: '0.0.1', description: 'Kindly Specify the version', name: 'Version'
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
                aws s3 ls
                sudo mv /home/ubuntu/workspace/Tomcat-pipe/target/studentapp-2.2-SNAPSHOT.war /tmp/student-${Version}.war
                aws s3 cp /tmp/student-${Version}.war s3://student-app-artifact121
                '''
            }
        }
        stage('tomcat-server-build'){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId: 'Adminn', keyFileVariable: 'tomcat', usernameVariable: 'ubuntu')]) {


                    sh '''
                    ssh -i ${tomcat} -o StrictHostKeyChecking=no ubuntu@3.26.43.172<<EOF
                    sudo apt-get update -y
                    sudo apt-get install default-jre -y
                    sudo apt-get install default-jdk -y
                    sudo apt-get install unzip git -y
                    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    unzip awscliv2.zip
                    sudo ./aws/install
                    $PWD
                    aws s3 cp s3://student-app-artifact121/student-${Version}.war .
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                    sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /opt/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/shutdown.sh
                    sudo cp -rv student-${Version}.war studentapp.war
                    sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.85/webapps/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/startup.sh
               '''
                }
            }
        }
    }   
}