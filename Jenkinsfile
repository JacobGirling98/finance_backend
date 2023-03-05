pipeline {
    agent { docker { gradle:jdk11-alpine }  }

    stages {
      stage("build") {
        steps {
          sh "gradle shadowJar --no-daemon"
        }
      }
    }
}