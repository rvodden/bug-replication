package bug.replication.specifications

import groovy.util.slurpersupport.GPathResult
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

abstract class JenkinsSpecification extends Specification {
    private static final Logger Logger = LoggerFactory.getLogger(JenkinsSpecification.getName());

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    def setup() {
        RuleBootstrapper.setup(jenkinsRule)
        approveInProcessScriptSignatures()
    }

    protected approveInProcessScriptSignatures() {
        final String approvalFilePath = '/scriptApproval.xml'

        Logger.debug('Approving signatures from ' + approvalFilePath)

        final XmlSlurper xmlSlurper = new XmlSlurper()
        final URL url = getClass().getResource(approvalFilePath)
        final ScriptApproval scriptApproval = ScriptApproval.get()

        GPathResult xml = xmlSlurper.parseText(url.text)

        Logger.debug('Found ' + xml.scriptApproval.approvedSignatures.string.size().toString() + ' signatures to approve.')

        xml.approvedSignatures.string.each {
            final ScriptApproval.PendingSignature pendingSignature = new ScriptApproval.PendingSignature(it.text(),false, ApprovalContext.create())
            scriptApproval.getPendingSignatures().add(pendingSignature)
            scriptApproval.approveSignature(pendingSignature.signature)
            JenkinsSpecification.Logger.debug("Approved '${it.text()}'")
        }
    }
}
