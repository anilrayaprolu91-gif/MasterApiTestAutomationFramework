/*
 * Jenkinsfile — Master API Test Automation Framework
 * Pipeline Type : Declarative
 * JDK           : 17 (must be configured in Jenkins → Global Tool Configuration as "JDK-17")
 * Maven         : 3.9+ (must be configured as "MAVEN-3.9")
 * Allure Plugin : Required — install "Allure Jenkins Plugin" from the Plugin Manager
 *
 * Usage:
 *   1. Create a new Jenkins Pipeline job.
 *   2. In "Pipeline Definition", select "Pipeline script from SCM".
 *   3. Point to your Git repository and set Script Path to "Jenkinsfile".
 *   4. Save and trigger the first build.
 */

pipeline {

    agent any

    // ── Tool bindings (names must match Jenkins Global Tool Configuration) ──
    tools {
        jdk   'JDK-17'
        maven 'MAVEN-3.9'
    }

    // ── Pipeline-wide environment variables ──────────────────────────────────
    environment {
        ALLURE_RESULTS = 'target/allure-results'
        ALLURE_REPORT  = 'target/allure-report'
        // Suppress Maven download progress bars; keeps logs readable in Jenkins.
        MAVEN_OPTS     = '-Dmaven.artifact.threads=8 -XX:+TieredCompilation -XX:TieredStopAtLevel=1'
    }

    // ── Parameter definitions — override from Build with Parameters ──────────
    parameters {
        string(
            name:         'BRANCH_NAME',
            defaultValue: 'main',
            description:  'Git branch to test'
        )
        choice(
            name:    'TEST_SUITE',
            choices: ['testng.xml', 'testng-smoke.xml'],
            description: 'TestNG suite file to execute'
        )
    }

    // ── Build options ─────────────────────────────────────────────────────────
    options {
        // Keep only the last 20 builds to conserve disk space.
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        // Mark build as ABORTED if no activity for 30 minutes (catches hanging tests).
        timeout(time: 30, unit: 'MINUTES')
        // Prevent multiple builds of the same branch from running simultaneously.
        disableConcurrentBuilds()
        // Add timestamps to every console log line.
        timestamps()
        // Colour ANSI escape codes in the console (requires AnsiColor plugin).
        ansiColor('xterm')
    }

    stages {

        // ── Stage 1: Source Checkout ─────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "Checking out branch: ${params.BRANCH_NAME}"
                checkout([
                    $class:           'GitSCM',
                    branches:         [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: scm.userRemoteConfigs
                ])
            }
        }

        // ── Stage 2: Build Verification ──────────────────────────────────────
        stage('Compile') {
            steps {
                echo 'Compiling production and test sources...'
                sh 'mvn -B --no-transfer-progress compile test-compile'
            }
        }

        // ── Stage 3: Execute API Tests ────────────────────────────────────────
        stage('Run API Tests') {
            steps {
                echo "Executing suite: ${params.TEST_SUITE}"
                sh """
                    mvn -B --no-transfer-progress test \
                        -Dsurefire.suiteXmlFiles=${params.TEST_SUITE}
                """
            }

            post {
                always {
                    // Publish TestNG XML reports for the built-in Jenkins test trend graph.
                    junit(
                        testResults:           'target/surefire-reports/TEST-*.xml',
                        allowEmptyResults:     true,
                        skipPublishingChecks:  false
                    )
                }
            }
        }

        // ── Stage 4: Generate Allure Report ───────────────────────────────────
        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure HTML report...'
                // The allure() step is provided by the Allure Jenkins Plugin.
                allure([
                    includeProperties: false,
                    jdk:               'JDK-17',
                    results:           [[path: "${env.ALLURE_RESULTS}"]],
                    report:            "${env.ALLURE_REPORT}",
                    reportBuildPolicy: 'ALWAYS'
                ])
            }
        }
    }

    // ── Post-build actions ────────────────────────────────────────────────────
    post {

        always {
            echo 'Archiving raw Allure results and logs...'
            archiveArtifacts(
                artifacts:     "${env.ALLURE_RESULTS}/**,target/logs/**,target/surefire-reports/**",
                allowEmptyArchive: true
            )
        }

        success {
            echo "\u2705 All tests passed — Build: ${env.BUILD_NUMBER} on branch ${params.BRANCH_NAME}"
        }

        failure {
            echo "\u274C Test failures detected — inspect the Allure report for details."
            // Add email/Slack notification here using the Email Extension or Slack plugin.
            // mail(to: 'qa-team@your-company.com', subject: "Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}", body: "Check console output at ${env.BUILD_URL}")
        }

        unstable {
            echo "\u26A0\uFE0F Build is UNSTABLE — some tests may have failed or were skipped."
        }

        cleanup {
            // Remove workspace artefacts that don't need to persist between builds.
            cleanWs(patterns: [[pattern: 'target/allure-results', type: 'INCLUDE']])
        }
    }
}

