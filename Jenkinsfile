pipeline {
    agent {
        node {
            label 'zextras-agent-v3'
        }
    }
    parameters {
        booleanParam defaultValue: false, description: 'Whether to upload the packages in playground repositories', name: 'PLAYGROUND'
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
                    sh "cp ${SETTINGS_PATH} settings-jenkins.xml"
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn --settings settings-jenkins.xml -Dzimbra.version=8.8.15  package'
                //build latest
                sh 'mvn --settings settings-jenkins.xml package'
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
                //deploy latest
                sh 'mvn --settings settings-jenkins.xml deploy'
            }
        }
        stage('Build deb/rpm') {
          when {
              anyOf {
                  branch 'release/*'
                  branch 'custom/*'
                  branch 'beta/*'
                  branch 'playground/*'
                  expression { params.PLAYGROUND == true }
                  buildingTag()
              }
          }
          stages {
              stage('Stash') {
                  steps {
                      sh 'cp target/zal-$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout).jar packages/zal.jar'
                      stash includes: "packages/**", name: 'binaries'
                  }
              }
              stage('pacur') {
                  parallel {
                      stage('Ubuntu 18.04') {
                          agent {
                              node {
                                  label 'pacur-agent-ubuntu-18.04-v1'
                              }
                          }
                          steps {
                              unstash 'binaries'
                              sh 'sudo pacur build ubuntu'
                              stash includes: 'artifacts/', name: 'artifacts-deb'
                          }
                          post {
                              always {
                                  archiveArtifacts artifacts: "artifacts/*.deb", fingerprint: true
                              }
                          }
                      }

                      stage('Centos 8') {
                          agent {
                              node {
                                  label 'pacur-agent-centos-8-v1'
                              }
                          }
                          steps {
                              unstash 'binaries'
                              sh 'sudo pacur build centos'
                              dir("artifacts/") {
                                  sh 'echo carbonio-zal* | sed -E "s#(carbonio-zal[0-9.]*).*#\\0 \\1.x86_64.rpm#" | xargs sudo mv'
                              }
                              stash includes: 'artifacts/', name: 'artifacts-rpm'
                          }
                          post {
                              always {
                                  archiveArtifacts artifacts: "artifacts/*.rpm", fingerprint: true
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
                              "props": "deb.distribution=bionic;deb.distribution=focal;deb.component=main;deb.architecture=amd64"
                          },
                          {
                              "pattern": "artifacts/(carbonio-zal)-(*).rpm",
                              "target": "centos8-playground/zextras/{1}/{1}-{2}.rpm",
                              "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras"
                          }
                      ]
                  }"""
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
                              "props": "deb.distribution=bionic;deb.distribution=focal;deb.component=main;deb.architecture=amd64"
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

                  //centos8
                  buildInfo = Artifactory.newBuildInfo()
                  buildInfo.name += "-centos8"
                  uploadSpec= """{
                      "files": [
                          {
                              "pattern": "artifacts/(carbonio-zal)-(*).rpm",
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

              }
          }
       }
    }
}
