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
                sudo ./aws/install
                aws s3 ls
                sudo mv /home/ubuntu/workspace/pipe/target/studentapp-2.2-SNAPSHOT.war /tmp/student-${BUILD_ID}.war
                aws s3 cp /tmp/student-${BUILD_ID}.war s3://student-app-artifact1 
                '''
            }
        }
    }   
}