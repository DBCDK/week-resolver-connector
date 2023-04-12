#!groovy

def workerNode = "devel9"

pipeline {
	agent {label workerNode}
	triggers {
		pollSCM("H/03 * * * *")
	}
	options {
		timestamps()
	}
	tools {
        maven "Maven 3"
    }
	stages {
		stage("clear workspace") {
			steps {
				deleteDir()
				checkout scm
			}
		}
		stage("verify") {
			steps {
				sh "mvn verify pmd:pmd"
				junit "target/surefire-reports/TEST-*.xml"
			}
		}
		stage("warnings") {
			agent {label workerNode}
			steps {
				warnings consoleParsers: [
					[parserName: "Java Compiler (javac)"]
				],
					unstableTotalAll: "0",
					failedTotalAll: "0"
			}
		}
		stage("pmd") {
			agent {label workerNode}
			steps {
				step([$class: 'hudson.plugins.pmd.PmdPublisher',
					  pattern: 'target/pmd.xml',
					  unstableTotalAll: "0",
					  failedTotalAll: "0"])
			}
		}
		stage("deploy") {
			when {
				branch "PRE-EE10"
			}
			steps {
				sh "mvn jar:jar deploy:deploy"
			}
		}
	}
}
