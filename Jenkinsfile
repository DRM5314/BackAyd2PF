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
                emailext(
                    subject: "- Build # $BUILD_NUMBER - $BUILD_STATUS!",
                    mimeType: 'text/html',
                    to: "${env.EMAIL}",
                    body: " - Build # $BUILD_NUMBER - $BUILD_STATUS:\n\n\t\tCheck console output at $BUILD_URL to view the results."
                )
            }
            success {
                emailext(
                    subject: "- Build # $BUILD_NUMBER - $BUILD_STATUS!",
                    mimeType: 'text/html',
                    to: "${env.EMAIL}",
                    body: " - Build # $BUILD_NUMBER - $BUILD_STATUS:\n\n\t\tCheck console output at $BUILD_URL to view the results."
                )
            }
    }

}
