pipeline {
    agent {
        label 'tomcat-server'
    }
    environment {
        app_path = "${WORKSPACE}/apache-tomcat-9.0.70/webapps/"
        app_start = "${WORKSPACE}/apache-tomcat-9.0.70/bin/"
    }
    stages{
        stage('code-pull'){
            steps {
                git credentialsId: 'mayurmwagh', url: 'git@github.com:mayurmwagh/student-ui.git'
            }
        }
        stage('code-build'){
            steps {
                sh 'sudo apt-get install maven -y'
                sh 'sudo mvn clean package'
            }
        } 
        stage('s3-upload'){
            steps{
                withAWS(credentials: 'mayur-cred', region: 'us-east-1') {
                   sh 'aws s3 ls'
                   sh 'aws s3 cp /var/lib/jenkins/workspace/build-job/target/studentapp-2.2-SNAPSHOT.war s3://my-bucketsjufbbe/'
                }
            }
        }   
        stage('Tomcat-download'){
            steps {
                sh 'sudo apt-get install tree -y'
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install awscli -y'
                sh 'sudo aws --version'
                sh 'sudo -i'
                sh 'cd /opt/'
                sh 'sudo wget -P /opt/ https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.70/bin/apache-tomcat-9.0.70.tar.gz'
                sh 'sudo tar -xvf /opt/apache-tomcat-9.0.70.tar.gz -C .'
                
            }
        }
        stage('copying-artifact'){
            steps {
                withAWS(region: 'us-east-2', credentials: 'mayur-cred') {
                echo "$app_path"
                sh 'aws s3 cp s3://my-bucketsjufbbe/studentapp-2.2-SNAPSHOT.war .'
                sh 'sudo mv studentapp-2.2-SNAPSHOT.war $app_path'
                sh 'sudo $app_start/startup.sh'
                sh 'sudo $app_start/shutdown.sh'
                sh 'sudo $app_start/startup.sh'
            }  
            }  
        }        
            
    }    
}