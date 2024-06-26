node {
  stage("Clone project") {
    git branch: 'master', url: 'https://github.com/<GitHub username>/<repo name>.git'
  }

  stage("Test") {
    sh 'mvn clean compile test'
  }
}
