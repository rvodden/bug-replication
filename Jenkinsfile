testLibrary = libraryFromLocalRepo()

echo this.currentBuild.toString()

pipeline {
    agent none
    stages {
        stage ('Test Bitbucket notifications') {
            agent any
            steps {
                script {
                    try {
                        testEcho
                    } catch (Exception E) {
                        echo E.toString()
                        throw E
                    }
                }
            }
        }
    }
    options {
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }
}


def libraryFromLocalRepo() {
    // Workaround for loading the current repo as shared build lib.
    // Checks out to workspace local folder named like the identifier.
    // We have to pass an identifier with version (which is ignored). Otherwise the build fails.
    library(identifier: 'local-library@snapshot', retriever: legacySCM(scm))
}