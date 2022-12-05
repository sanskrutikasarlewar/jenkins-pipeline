pipeline {
    agent any
    stages {
        stage('code-pulling') {
            steps {
                git credentialsId: 'ssh-key', url: 'git@github.com:mohit-decoder/onlinebookstore.git'
            }
        }
        stage('build-maven') {
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'mvn clean package' 
            }
        }
        stage('artifact to s3') {
            steps {
                 withAWS(credentials: 'ubuntu', region: 'us-west-2') {
                     sh 'sudo apt update -y'
                     sh 'sudo apt install awscli -y'
                     sh 'aws s3 ls'
                     sh 'aws s3 mb s3://chorbucket-builder --region us-west-2' 
                     sh 'sudo mv /var/lib/jenkins/workspace/bookstore-project/target/onlinebookstore.war /tmp/onlinebookstore${BUILD_ID}.war'
                     sh 'aws s3 cp /tmp/onlinebookstore${BUILD_ID}.war  s3://chorbucket-builder/' 
                }
            }        
        }
        stage('deploy-to-tomcat-server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'key', keyFileVariable: 'mohit', usernameVariable: 'ubuntu')]) {
                    sh'''
                    sudo ssh -i ${mohit} -T -o StrictHostKeyChecking=no ubuntu@35.88.234.65<<EOF
                    sudo apt update -y
                    sudo apt install awscli -y
                    sudo apt install openjdk-11-jre -y
                    export AWS_ACCESS_KEY_ID=AKIA3UOQDMGBAAXDP2YX
                    export AWS_SECRET_ACCESS_KEY=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
                    export AWS_DEFAULT_REGION=us-west-2
                    aws s3 ls
                    aws s3 cp s3://chorbucket-builder/onlinebookstore${BUILD_ID}.war .
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.69/bin/apache-tomcat-9.0.69.tar.gz
                    sudo tar -xzvf apache-tomcat-9.0.69.tar.gz -C /opt/
                    sudo chmod 777 /opt/apache-tomcat-9.0.69/*  && sudo chown ubuntu: /opt/apache-tomcat-9.0.69/*
                    sudo cp -rv onlinebookstore${BUILD_ID}.war onlinebookstore.war
                    sudo cp -rv onlinebookstore.war /opt/apache-tomcat-9.0.69/webapps/
                    sudo sh /opt/apache-tomcat-9.0.69/bin/startup.sh
                    '''
                }
            }
        }
    }
}