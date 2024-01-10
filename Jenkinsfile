def mvnCmd(String cmd) {
  sh 'mvn --settings settings.xml -B ' + cmd
}

def runTests() {
    mvnCmd("verify")
}

def deployJfrog() {
    def result = sh (script: "git log -1 | grep '.*\\[deploy jfrog\\].*'", returnStatus: true)
    return (result == 0) || (params.PUBLISH_TO_ARTIFACTORY == true)
}

pipeline {
    agent {
        node {
            label 'zextras-agent-v3'
        }
    }
    parameters {
        booleanParam defaultValue: false, description: 'Whether to upload the packages in playground repositories', name: 'PLAYGROUND'
        booleanParam defaultValue: false, description: 'Publish artifact to artifactory', name: 'PUBLISH_TO_ARTIFACTORY'
    }
    environment {
        JAVA_OPTS="-Dfile.encoding=UTF8"
        LC_ALL="C.UTF-8"
        jenkins_build="true"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '25'))
        timeout(time: 2, unit: 'HOURS')
        skipDefaultCheckout()
    }
    stages {
        stage('Setup') {
            steps {
                checkout scm
                withCredentials([file(credentialsId: 'jenkins-maven-settings.xml', variable: 'SETTINGS_PATH')]) {
                    sh "cp ${SETTINGS_PATH} settings.xml"
                }
            }
        }
        stage('Build') {
            steps {
                mvnCmd("package")
            }
        }
        stage('Tests') {
            steps {
                runTests()
            }
        }
        stage('SonarQube') {
            environment {
                SCANNER_HOME = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv(credentialsId: 'sonarqube-user-token', installationName: 'SonarQube instance') {
                    mvnCmd('sonar:sonar')
                }
            }
        }
        stage('Publish') {
            when {
                anyOf {
                    expression { deployJfrog() }
                    buildingTag()
                    branch 'main'
                }
            }
            steps {
                mvnCmd("deploy -DskipTests")
            }
        }
        stage('Build deb/rpm') {
          stages {
              stage('Stash') {
                  steps {
                      sh 'cp target/zal.jar packages/'
                      stash includes: "yap.json,packages/**", name: 'binaries'
                  }
              }
              stage('yap') {
                  parallel {
                      stage('Ubuntu') {
                          agent {
                              node {
                                  label 'yap-agent-ubuntu-20.04-v2'
                              }
                          }
                          steps {
                              unstash 'binaries'
                              sh 'sudo yap build ubuntu .'
                              stash includes: 'artifacts/', name: 'artifacts-deb'
                          }
                          post {
                              always {
                                  archiveArtifacts artifacts: "artifacts/*.deb", fingerprint: true
                              }
                          }
                      }

                      stage('Rocky') {
                          agent {
                              node {
                                  label 'yap-agent-rocky-8-v2'
                              }
                          }
                          steps {
                              unstash 'binaries'
                              sh 'sudo yap build rocky .'
                              stash includes: 'artifacts/x86_64/', name: 'artifacts-rpm'
                          }
                          post {
                              always {
                                  archiveArtifacts artifacts: "artifacts/x86_64/*.rpm", fingerprint: true
                              }
                          }
                      }
                  }
              }
          }
      }
      stage('Upload To Playground') {
          when {
              anyOf {
                  branch 'playground/*'
                  expression { params.PLAYGROUND == true }
              }
          }
          steps {
              unstash 'artifacts-deb'
              unstash 'artifacts-rpm'
              script {
                  def server = Artifactory.server 'zextras-artifactory'
                  def buildInfo
                  def uploadSpec

                  buildInfo = Artifactory.newBuildInfo()
                  uploadSpec = """{
                      "files": [
                          {
                              "pattern": "artifacts/carbonio-zal*.deb",
                              "target": "ubuntu-playground/pool/",
                              "props": "deb.distribution=focal;deb.distribution=jammy;deb.component=main;deb.architecture=all"
                          },
                          {
                              "pattern": "artifacts/x86_64/(carbonio-zal)-(*).rpm",
                              "target": "centos8-playground/zextras/{1}/{1}-{2}.rpm",
                              "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras"
                          },
                          {
                              "pattern": "artifacts/x86_64/(carbonio-zal)-(*).rpm",
                              "target": "rhel9-playground/zextras/{1}/{1}-{2}.rpm",
                              "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras"
                          }
                      ]
                  }"""
                  server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
              }
          }
      }
      stage('Upload To Devel') {
          when {
            branch 'main'
          }
          steps {
              unstash 'artifacts-deb'
              script {
                  def server = Artifactory.server 'zextras-artifactory'
                  def buildInfo
                  def uploadSpec

                  buildInfo = Artifactory.newBuildInfo()
                  uploadSpec = '''{
                      "files": [
                          {
                              "pattern": "artifacts/*.deb",
                              "target": "ubuntu-devel/pool/",
                              "props": "deb.distribution=focal;deb.component=main;deb.architecture=all"
                          }
                      ]
                  }'''
                  server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
              }
          }
      }
      stage('Upload & Promotion Config') {
          when {
              anyOf {
                  branch 'release/*'
                  buildingTag()
              }
          }
          steps {
              unstash 'artifacts-deb'
              unstash 'artifacts-rpm'
              script {
                  def server = Artifactory.server 'zextras-artifactory'
                  def buildInfo
                  def uploadSpec
                  def config

                  //ubuntu
                  buildInfo = Artifactory.newBuildInfo()
                  buildInfo.name += "-ubuntu"
                  uploadSpec= """{
                      "files": [
                          {
                              "pattern": "artifacts/carbonio-zal*.deb",
                              "target": "ubuntu-rc/pool/",
                              "props": "deb.distribution=focal;deb.distribution=jammy;deb.component=main;deb.architecture=all"
                          }
                      ]
                  }"""
                  server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                  config = [
                          'buildName'          : buildInfo.name,
                          'buildNumber'        : buildInfo.number,
                          'sourceRepo'         : 'ubuntu-rc',
                          'targetRepo'         : 'ubuntu-release',
                          'comment'            : 'Do not change anything! Just press the button',
                          'status'             : 'Released',
                          'includeDependencies': false,
                          'copy'               : true,
                          'failFast'           : true
                  ]
                  Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: "Ubuntu Promotion to Release"
                  server.publishBuildInfo buildInfo

                  //rhel8
                  buildInfo = Artifactory.newBuildInfo()
                  buildInfo.name += "-centos8"
                  uploadSpec= """{
                      "files": [
                          {
                              "pattern": "artifacts/x86_64/(carbonio-zal)-(*).rpm",
                              "target": "centos8-rc/zextras/{1}/{1}-{2}.rpm",
                              "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras"
                          }
                      ]
                  }"""
                  server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                  config = [
                          'buildName'          : buildInfo.name,
                          'buildNumber'        : buildInfo.number,
                          'sourceRepo'         : 'centos8-rc',
                          'targetRepo'         : 'centos8-release',
                          'comment'            : 'Do not change anything! Just press the button',
                          'status'             : 'Released',
                          'includeDependencies': false,
                          'copy'               : true,
                          'failFast'           : true
                  ]
                  Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: "Centos8 Promotion to Release"
                  server.publishBuildInfo buildInfo

                  //rhel9
                  buildInfo = Artifactory.newBuildInfo()
                  buildInfo.name += "-rhel9"
                  uploadSpec= """{
                      "files": [
                          {
                              "pattern": "artifacts/x86_64/(carbonio-zal)-(*).rpm",
                              "target": "rhel9-rc/zextras/{1}/{1}-{2}.rpm",
                              "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras"
                          }
                      ]
                  }"""
                  server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                  config = [
                          'buildName'          : buildInfo.name,
                          'buildNumber'        : buildInfo.number,
                          'sourceRepo'         : 'rhel9-rc',
                          'targetRepo'         : 'rhel9-release',
                          'comment'            : 'Do not change anything! Just press the button',
                          'status'             : 'Released',
                          'includeDependencies': false,
                          'copy'               : true,
                          'failFast'           : true
                  ]
                  Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: "Centos8 Promotion to Release"
                  server.publishBuildInfo buildInfo
              }
          }
       }
    }
}
