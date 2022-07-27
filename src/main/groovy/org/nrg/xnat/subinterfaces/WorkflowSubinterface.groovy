package org.nrg.xnat.subinterfaces

import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Workflow
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

import static org.nrg.xnat.pogo.Workflow.*

@SuppressWarnings('UnnecessaryQualifiedReference') // constants must be referenced with the class name because they won't be available in XnatInterface
class WorkflowSubinterface extends XnatFunctionalitySubinterface {

    public static final List<String> TERMINAL_STATUSES = ['Complete', 'Failed', 'Killed'].asImmutable()
    public static final int WORKFLOW_POLLING_RATE = 1000
    public static final int WORKFLOW_WAIT_TIMEOUT = 60

    @Override
    List<String> getHandledEndpoints() {
        [
                '/services/workflows/{PIPELINE_NAME}',
                '/workflows/{WORKFLOW_ID}'
        ]
    }

    Workflow readWorkflow(Object workflowId) {
        jsonQuery().get(formatRestUrl("/workflows/${workflowId}")).then().assertThat().statusCode(200).and().extract().jsonPath().getObject('items[0].data_fields', Workflow)
    }

    String readWorkflowStatus(Object workflowId) {
        readWorkflow(workflowId).status
    }

    boolean isWorkflowTerminal(Object workflowId) {
        isWorkflowTerminal(readWorkflowStatus(workflowId))
    }

    boolean isWorkflowTerminal(String actualStatus) {
        WorkflowSubinterface.TERMINAL_STATUSES.any { terminalStatus ->
            actualStatus.startsWith(terminalStatus)
        }
    }

    XnatInterface waitForPipelineCompletion(ImagingSession session, String pipelineName, int maxTimeInSeconds = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {
        final int workflowId = jsonQuery().queryParams('experiment', xnatInterface.getAccessionNumber(session)).get(formatRestUrl("/services/workflows/${pipelineName}")).
                then().assertThat().statusCode(200).and().extract().jsonPath().getInt('items.get(0).data_fields.wrk_workflowData_id')
        waitForWorkflowComplete(workflowId, maxTimeInSeconds)
    }

    XnatInterface verifyNoWorkflow(ImagingSession session, String pipelineName, int timeToWait = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {
        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        // ensure workflow doesn't run within timeToWait
        while (!TimeUtils.maxTimeReached(stopWatch, timeToWait)) {
            int status = jsonQuery().queryParams('experiment', xnatInterface.getAccessionNumber(session)).get(formatRestUrl("/services/workflows/${pipelineName}")).statusCode()

            if (status != 404) {
                throw new RuntimeException("${pipelineName} ran on ${session.getLabel()}.")
            }
            TimeUtils.sleep(1000)
        }
        xnatInterface
    }

    XnatInterface waitForWorkflowComplete(int workflowId, int maxTimeInSeconds = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {
        waitForWorkflowStatus(workflowId, maxTimeInSeconds, WORKFLOW_COMPLETE, WORKFLOW_FAILED)
    }

    XnatInterface waitForWorkflowFailed(int workflowId, int maxTimeInSeconds = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {
        waitForWorkflowStatus(workflowId, maxTimeInSeconds, WORKFLOW_FAILED, WORKFLOW_COMPLETE)
    }

    XnatInterface waitForWorkflowStatus(int workflowId, int maxTimeInSeconds, String desiredStatus, String... exceptionStatuses) {
        waitForWorkflowSatisfying(workflowId, maxTimeInSeconds, desiredStatus, { workflow, actualStatus ->
            if (actualStatus == desiredStatus) {
                return true
            } else if (exceptionStatuses.length && exceptionStatuses.contains(actualStatus)) {
                throw new RuntimeException("Workflow ${workflowId} has status ${actualStatus} rather than ${desiredStatus}.")
            }
            return false
        })
    }

    XnatInterface waitForWorkflowTerminal(int workflowId, int maxTimeInSeconds = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {
        waitForWorkflowSatisfying(workflowId, maxTimeInSeconds, 'terminal', { workflow, String actualStatus ->
            return isWorkflowTerminal(actualStatus)
        })
    }

    XnatInterface waitForWorkflowSatisfying(int workflowId, int maxTimeInSeconds, String humanReadableExpectation, Closure<Boolean> acceptanceCriteria) {
        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        while (true) {
            final String status = readWorkflowStatus(workflowId)

            TimeUtils.checkStopWatch(stopWatch, maxTimeInSeconds, "Workflow ${workflowId} status is not ${humanReadableExpectation} in allotted number of seconds: ${maxTimeInSeconds}. The last known status of the workflow is: ${status}.")

            if (acceptanceCriteria(workflowId, status)) {
                return xnatInterface
            }

            TimeUtils.sleep(WorkflowSubinterface.WORKFLOW_POLLING_RATE)
        }
    }

    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = WorkflowSubinterface.WORKFLOW_WAIT_TIMEOUT) {}

    XnatInterface putWorkflow(Workflow workflow) {
        queryBase().queryParams(SerializationUtils.serializeToMap(workflow)).put(formatRestUrl('workflows')).then().assertThat().statusCode(200)
        xnatInterface
    }

}
