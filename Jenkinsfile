pipeline{
        agent any
        tools { 
                maven 'Maven'
            }
        environment {
                EMAIL = 'davidrodolfo-martinezmiranda@cunoc.edu.gt'
                SSH_KEY = credentials('key-ec2-deploy')
                EC2_INSTANCE = 'ubuntu@ec2-44-201-186-170.compute-1.amazonaws.com'
                PATH_TO_JAR = '/var/lib/jenkins/workspace/ayd2-multibranch-pipeline_master/target/library-0.0.1-SNAPSHOT.jar'
                REMOTE_PATH = '/home/ubuntu/library-0.0.1-SNAPSHOT.jar'
        }
        stages {

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
                withCredentials([sshUserPrivateKey(credentialsId: 'key-ec2-deploy', keyFileVariable: 'SSH_KEY')]) {
                script {                     
                        sh """
                        # Copiar el nuevo archivo JAR a la instancia EC2
                        scp -v -o StrictHostKeyChecking=no -i $SSH_KEY  $PATH_TO_JAR $EC2_INSTANCE:$REMOTE_PATH
                        """
                    }   
                }
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
