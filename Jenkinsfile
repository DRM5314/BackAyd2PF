pipeline{
        agent any
        tools { 
                maven 'Maven'
            }
        environment {
                EMAIL = 'davidrodolfo-martinezmiranda@cunoc.edu.gt'
        }
        stage('Clone-Repository') {
            steps {
                git branch: "${env.BRANCH_NAME}", url: 'https://github.com/DRM5314/BackAyd2PF.git'
                echo 'Repo clone successful'
            }
        }
        
          stage("Test") {
            steps{
                    sh 'mvn clean compile test'
            }
          }
        
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Integration Test') {
            when {
                expression {
                    return env.BRANCH_NAME == 'dev'
                }
            }
            steps {
                sh 'mvn verify'
                echo 'Integration tests successful'
            }
        }


        stage('Deploy') {
            when {
                expression {
                    return env.BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    echo 'Deployment successful test'
                }
            }
        }
        
        
        post {
            failure {
                emailext(
                    subject: "- Build # $BUILD_NUMBER - ${currentBuild.currentResult}!",
                    mimeType: 'text/html',
                    to: "${env.EMAIL}",
                    body: " - Build # $BUILD_NUMBER - ${currentBuild.currentResult}:\n\n\t\tCheck console output at $BUILD_URL to view the results."
                )
            }
            success {
                emailext(
                    subject: "- Build # $BUILD_NUMBER - ${currentBuild.currentResult}!",
                    mimeType: 'text/html',
                    to: "${env.EMAIL}",
                    body: " - Build # $BUILD_NUMBER - ${currentBuild.currentResult}:\n\n\t\tCheck console output at $BUILD_URL to view the results."
                )
            }
    }

}
