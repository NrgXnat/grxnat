package org.nrg.xnat.subinterfaces

import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.enums.WorkflowStatus
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.experiments.ImagingSession

class WorkflowSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/services/workflows/{PIPELINE_NAME}',
                '/workflows/{WORKFLOW_ID}'
        ]
    }

    WorkflowStatus readWorkflowStatus(int workflowId) {
        WorkflowStatus.get(
                jsonQuery().get(formatRestUrl("/workflows/${workflowId}")).then().assertThat().statusCode(200).and().extract().jsonPath().getString('items.data_fields.status')
        )
    }

    XnatInterface waitForPipelineCompletion(ImagingSession session, String pipelineName, int maxTimeInSeconds = 60) {
        final int workflowId = jsonQuery().queryParams('experiment', xnatInterface.getAccessionNumber(session)).get(formatRestUrl("/services/workflows/${pipelineName}")).
                then().assertThat().statusCode(200).and().extract().jsonPath().getInt('items.get(0).data_fields.wrk_workflowData_id')
        waitForWorkflowComplete(workflowId, maxTimeInSeconds)
    }

    XnatInterface verifyNoWorkflow(ImagingSession session, String pipelineName, int timeToWait = 60) {
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

    XnatInterface waitForWorkflowComplete(int workflowId, int maxTimeInSeconds = 60) {
        waitForWorkflowStatus(workflowId, maxTimeInSeconds, WorkflowStatus.COMPLETE, WorkflowStatus.FAILED)
    }

    XnatInterface waitForWorkflowFailed(int workflowId, int maxTimeInSeconds = 60) {
        waitForWorkflowStatus(workflowId, maxTimeInSeconds, WorkflowStatus.FAILED, WorkflowStatus.COMPLETE)
    }

    XnatInterface waitForWorkflowStatus(int workflowId, int maxTimeInSeconds, WorkflowStatus desiredStatus, WorkflowStatus... exceptionStatuses) {
        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        while (true) {
            TimeUtils.checkStopWatch(stopWatch, maxTimeInSeconds, "Workflow ${workflowId} status is not ${desiredStatus} in allotted number of seconds: ${maxTimeInSeconds}")

            final WorkflowStatus status = readWorkflowStatus(workflowId)

            if (status == desiredStatus) {
                return xnatInterface
            } else if (exceptionStatuses.length && exceptionStatuses.contains(status)) {
                throw new RuntimeException("Workflow ${workflowId} has status ${status} rather than ${desiredStatus}.")
            }
            TimeUtils.sleep(1000)
        }
    }

    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {}

}
