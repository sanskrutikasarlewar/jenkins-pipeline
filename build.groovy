pipeline {
    agent any
    stages {
        stage('code-pulling') {
            steps {
                git credentialsId: 'github', url: 'git@github.com:mohit-decoder/student-ui.git'
            }
        }
        stage('build-maven') {
            steps {
                sh 'mvn clean package' 
            }
        }
    }
}
