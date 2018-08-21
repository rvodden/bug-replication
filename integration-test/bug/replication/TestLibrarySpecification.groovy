package bug.replication


import hudson.model.Job
import hudson.model.queue.QueueTaskFuture
import jenkins.model.ParameterizedJobMixIn

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

import org.slf4j.LoggerFactory
import org.slf4j.Logger

class TestLibrarySpecification extends bug.replication.specifications.JenkinsSpecification {
    private static final Logger Logger = LoggerFactory.getLogger(TestLibrarySpecification.getName());
    private static final String COMMIT = "5482b35aa71d7b3c0a57da49944b2eca63d542e9"

    def "testLibrary is used in a job"() {
        given: "a job contains a call to a library which uses a global variable"
        WorkflowJob workflowJob = jenkinsRule.createProject(WorkflowJob, 'project')

        workflowJob.definition = new CpsFlowDefinition("""
            testLibrary = library('testLibrary') _
            
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
                                } catch (Exception e) {
                                    echo e.toString()
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        """.stripIndent(), true)

        when: "the job is run"
        QueueTaskFuture queueTaskFuture = new ParameterizedJobMixIn() {
            @Override protected Job asJob() {
                return workflowJob;
            }
        }.scheduleBuild2(0);

        then: "the build should run successfully"
        WorkflowRun result = jenkinsRule.assertBuildStatusSuccess(queueTaskFuture)
    }
}