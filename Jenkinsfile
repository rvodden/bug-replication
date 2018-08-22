testLibrary = libraryFromLocalRepo()

echo currentBuild.toString()

pipeline {
    agent any
    stages {
        stage ('Test Echo') {
            steps {
                script {
                    try {
                        testEcho()
                    } catch (Exception exception) {
                        echo exception.toString()
                        throw exception
                    }
                }
            }
        }
    }
}


def libraryFromLocalRepo() {
    // Workaround for loading the current repo as shared build lib.
    // Checks out to workspace local folder named like the identifier.
    // We have to pass an identifier with version (which is ignored). Otherwise the build fails.
    library(identifier: 'local-library@snapshot', retriever: legacySCM(scm))
}