pipeline{
        agent any
        tools { 
                maven 'Maven 3.3.9' 
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

}
