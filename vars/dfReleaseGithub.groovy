def call(String project) {
    sh 'docker container run --rm -it -v $PWD:/src vfarcic/gox docker-flow-proxy'
    withCredentials([usernamePassword(credentialsId: "github-token", variable: "GIHBUT_TOKEN")]) {
        script {
            def msg = sh(returnStdout: true, script: "git log --format=%B -1").trim()
            if (msg.contains("[release]")) {
                def lines = msg.split("\n")
                def releaseTitle = ""
                def releaseMsg = ""
                for (i = 0; i <lines.length; i++) {
                    if (i == 0) {
                        releaseTitle = lines[i]
                    } else {
                        releaseMsg = lines[i] + "\n"
                    }
                }
                sh "docker container run --rm -it -e GITHUB_TOKEN=${GITHUB_TOKEN} -v ${pwd}:/src -w /src vfarcic/github-release git tag -a ${currentBuild.displayName} -m '${releaseMsg}'"
                sh "docker container run --rm -it -e GITHUB_TOKEN=${GITHUB_TOKEN} -v ${pwd}:/src -w /src vfarcic/github-release git push --tags
            }
        }
    }
}