pipeline {
    agent { docker { image 'gradle:jdk11-alpine' }  }

    stages {
      stage("build") {
        steps {
          sh "gradle shadowJar --no-daemon"
        }
      }
    }
}