pipeline {
    agent { docker { image 'gradle:8.0-jdk11' }  }

    stages {
      stage("build") {
        steps {
          sh "gradle shadowJar --no-daemon"
        }
      }
    }
}