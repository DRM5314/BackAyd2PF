pipeline{
        agent any
        tools { 
                maven 'Maven 3.3.9' 
            }
        stages{
          stage("Clone project") {
            git branch: 'master', url: 'https://github.com/DRM5314/BackAyd2PF.git'
          }
        
          stage("Test") {
            sh 'mvn clean compile test'
          }
        }

}
