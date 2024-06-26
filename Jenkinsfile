pipeline{
        agent any
        tools { 
                maven 'Maven'
            }
        environment {
                EMAIL = 'davidrodolfo-martinezmiranda@cunoc.edu.gt'
        }
        stages{
          stage("Clone project") {
            steps{
                    git branch: 'master', url: 'https://github.com/DRM5314/BackAyd2PF.git'
                    echo 'Repo clone successful'
            }
          }
        
          stage("Test") {
            steps{
                    sh 'mvn clean compile test'
            }
          }
        }
        
        post {
            failure {
                    to: "${env.EMAIL}",
                    subject: "${JOB_NAME}.${BUILD_NUMBER} FAILED",
                    mimeType: 'text/html',
                    body: "${JOB_NAME}.${BUILD_NUMBER} FAILED"
            }
            success {
                    to: "${env.EMAIL}",
                    subject: "${JOB_NAME}.${BUILD_NUMBER} PASSED",
                    mimeType: 'text/html',
                    body: "${JOB_NAME}.${BUILD_NUMBER} PASSED"
            }
    }

}
