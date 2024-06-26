node {
  stage("Clone project") {
    git branch: 'master', url: 'https://github.com/DRM5314/BackAyd2PF.git'
  }

  stage("Test") {
    sh 'mvn clean compile test'
  }
}
