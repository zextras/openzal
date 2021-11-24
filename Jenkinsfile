pipeline {
    agent {
        node {
            label 'zextras-agent-v3'
        }
    }
    environment {
        JAVA_OPTS="-Dfile.encoding=UTF8"
        LC_ALL="C.UTF-8"
        jenkins_build="true"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '25'))
        timeout(time: 2, unit: 'HOURS')
    }
    stages {
        stage('Setup') {
            steps {
                withCredentials([file(credentialsId: 'jenkins-maven-settings.xml', variable: 'SETTINGS_PATH')]) {
                    sh "cp ${SETTINGS_PATH} settings-jenkins.xml"
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn --settings settings-jenkins.xml -Dzimbra.version=8.8.15  package'
                sh 'mvn --settings settings-jenkins.xml -Dzimbra.version=21.00.0 package'
            }
        }
        stage('Publish tagged version') {
            when {
                anyOf {
                    buildingTag()
                }
            }
            steps {
                sh 'mvn --settings settings-jenkins.xml -Dzimbra.version=8.8.15  deploy'
                sh 'mvn --settings settings-jenkins.xml -Dzimbra.version=21.00.0 deploy'
            }
        }
    }
}
