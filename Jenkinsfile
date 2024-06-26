pipeline{
        agent any
        tools { 
                maven 'Maven'
            }
        parameters {
        string(
            name: 'email', 
            defaultValue: 'davidrodolfo-martinezmiranda@cunoc.edu.gt', 
            description: 'Email address to send notification' )
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
                    echo 'Send email...'
                emailext(
                    subject: "${JOB_NAME}.${BUILD_NUMBER} FAILED",
                    mimeType: 'text/html',
                    to: "$email",
                    body: "${JOB_NAME}.${BUILD_NUMBER} FAILED"
                )
            }
            success {
                emailext(
                    subject: "${JOB_NAME}.${BUILD_NUMBER} PASSED",
                    mimeType: 'text/html',
                    to: "$email",
                    body: "${JOB_NAME}.${BUILD_NUMBER} PASSED"
                )
            }
                echo '...Send email!!'
    }

}
