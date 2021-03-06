pipeline {
    agent {
        kubernetes {
            label 'molgenis-jdk11'
        }
    }
    environment {
        LOCAL_REPOSITORY = "${LOCAL_REGISTRY}/molgenis/vibe"
        TIMESTAMP = sh(returnStdout: true, script: "date -u +'%F_%H-%M-%S'").trim()
    }
    stages {
        stage('Prepare') {
            steps {
                script {
                    env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                }
                container('vault') {
                    script {
                        sh "mkdir ${JENKINS_AGENT_WORKDIR}/.m2"
                        sh(script: "vault read -field=value secret/ops/jenkins/maven/settings.xml > ${JENKINS_AGENT_WORKDIR}/.m2/settings.xml")
                        env.GITHUB_TOKEN = sh(script: 'vault read -field=value secret/ops/token/github', returnStdout: true)
                        env.SONAR_TOKEN = sh(script: 'vault read -field=value secret/ops/token/sonar', returnStdout: true)
                        env.PGP_PASSPHRASE = 'literal:' + sh(script: 'vault read -field=passphrase secret/ops/certificate/pgp/molgenis-ci', returnStdout: true)
                        env.GITHUB_USER = sh(script: 'vault read -field=username secret/ops/token/github', returnStdout: true)
                    }
                }
            }
        }
        stage('Build: [ pull request ]') {
            when {
                changeRequest()
            }
            environment {
                // PR-1234-231
                TAG = "PR-${CHANGE_ID}-${BUILD_NUMBER}"
            }
            steps {
                container('maven') {
                	dir('app') {
                    	sh "mvn clean install -Dmaven.test.redirectTestOutputToFile=true -T4"
                	}
                }
            }
            post {
                always {
                    //junit '**/target/surefire-reports/**.xml'
                    container('maven') {
                    	dir('app') {
	                        sh "mvn -q -B sonar:sonar -Dsonar.login=${env.SONAR_TOKEN} -Dsonar.github.oauth=${env.GITHUB_TOKEN} -Dsonar.pullrequest.base=${CHANGE_TARGET} -Dsonar.pullrequest.branch=${BRANCH_NAME} -Dsonar.pullrequest.key=${env.CHANGE_ID} -Dsonar.pullrequest.provider=GitHub -Dsonar.pullrequest.github.repository=molgenis/vibe -Dsonar.ws.timeout=120"
	                        sh "mvn -q -B dockerfile:build dockerfile:tag dockerfile:push -Ddockerfile.tag=${TAG} -Ddockerfile.repository=${LOCAL_REPOSITORY}"
                        }
                    }
                }
            }
        }
        stage('Build: [ master ]') {
            when {
                branch 'master'
            }
            environment {
                TAG = "dev-${TIMESTAMP}"
            }
            steps {
                milestone 1
                container('maven') {
                	dir('app') {
                    	sh "mvn clean install -Dmaven.test.redirectTestOutputToFile=true -T4"
                	}
                }
            }
            post {
                always {
                    //junit '**/target/surefire-reports/**.xml'
                    container('maven') {
                    	dir('app') {
	                        sh "mvn -q -B sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Dsonar.ws.timeout=120"
	                        sh "mvn -q -B dockerfile:build dockerfile:tag dockerfile:push -Ddockerfile.tag=${TAG} -Ddockerfile.repository=${LOCAL_REPOSITORY}"
	                        sh "mvn -q -B dockerfile:tag dockerfile:push -Ddockerfile.tag=dev -Ddockerfile.repository=${LOCAL_REPOSITORY}"
                        }
                    }
                }
            }
        }
        stage('Steps: [ x.x ]') {
            when {
                expression { BRANCH_NAME ==~ /[0-9]+\.[0-9]+/ }
            }
            stages {
                stage('Build [ x.x ]') {
                    steps {
                        container('maven') {
	                        dir('app') {
	                            sh "mvn -q -B clean install -Dmaven.test.redirectTestOutputToFile=true -T4"
	                            sh "mvn -q -B sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Dsonar.branch.name=${BRANCH_NAME} -Dsonar.ws.timeout=120"
	                            sh "mvn -q -B dockerfile:tag dockerfile:push -Ddockerfile.tag=latest"
                        	}
                        }
                    }
                }
                stage('Prepare Release [ x.x ]') {
                    steps {
                        timeout(time: 40, unit: 'MINUTES') {
                            input(message: 'Prepare to release?')
                        }
                        container('maven') {
                        	dir('app') {
                            	sh "mvn -q -B release:prepare -Dmaven.test.redirectTestOutputToFile=true -Darguments=\"-q -B -Dmaven.test.redirectTestOutputToFile=true\""
                        	}
                        }
                    }
                }
                stage('Perform release [ x.x ]') {
                    steps {
                        container('vault') {
                            script {
                                env.PGP_SECRETKEY = "keyfile:${JENKINS_AGENT_WORKDIR}/key.asc"
                                sh(script: "vault read -field=secret.asc secret/ops/certificate/pgp/molgenis-ci > ${JENKINS_AGENT_WORKDIR}/key.asc")
                            }
                        }
                        container('maven') {
                        	dir('app') {
	                            sh "mvn -q -B release:perform -Darguments=\"-q -B -Dmaven.test.redirectTestOutputToFile=true\""
	                            sh "cd target/checkout/app && mvn -q -B dockerfile:build dockerfile:tag dockerfile:push -Ddockerfile.tag=${TAG}"
                            }
                        }
                    }
                }
                stage('Manually close and release on sonatype [ x.x ]') {
                    steps {
                        input(message='Log on to https://oss.sonatype.org/ and manually close and release to maven central.')
                    }
                }
            }
        }
    }
}
