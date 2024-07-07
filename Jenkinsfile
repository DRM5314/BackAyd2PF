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
        }
        stages {
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
                withCredentials([sshUserPrivateKey(credentialsId: 'key-ec2-deploy', keyFileVariable: 'SSH_KEY')]) {
                script {
                       
                       
                        // Detener la aplicación Java si está en ejecución, copiar el nuevo archivo JAR y reiniciar la aplicación
                        sh """
                        ssh -i \$SSH_KEY ${EC2_INSTANCE} '
                            # Detener la aplicación Java si está en ejecución
                            sudo pkill -f "java -jar ${REMOTE_PATH}" || true
                        '
                        
                        # Copiar el nuevo archivo JAR a la instancia EC2
                        scp -v -o StrictHostKeyChecking=no -i \$SSH_KEY  ${JAR_FILE} ${EC2_INSTANCE}:${REMOTE_PATH}
                        
                        ssh -i \$SSH_KEY ${EC2_INSTANCE} '
                            # Iniciar la aplicación Java
                            sudo java -jar ${REMOTE_PATH} > /dev/null 2>&1 &
                        '
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
