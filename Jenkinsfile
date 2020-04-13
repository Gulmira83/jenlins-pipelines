pipeline {
  agent any
  stages {
    stage('pull repo') {
      steps {
        git 'https://github.com/Gulmira83/jenlins-pipelines.git'
      }
    }

    stage('stage2') {
      steps {
        sh 'echo "Hello"'
      }
    }

    stage('stage3') {
      steps {
        echo 'send message'
      }
    }

  }
}