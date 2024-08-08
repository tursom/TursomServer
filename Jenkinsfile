pipeline {
    agent {
        docker {
            image 'openjdk:21'
            args '-v /root/.gradle:/root/.gradle'
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh './gradlew publish'
                script {
                    try {
                        archiveArtifacts artifacts: "**/build/libs/*.jar",fingerprint: true, followSymlinks: false, onlyIfSuccessful: true
                    } catch (Exception err) {
                        echo err.toString()    /* hudson.AbortException: Couldn't find any revision to build. Verify the repository and branch configuration for this job. */
                    }
                }
            }
        }
    }
}