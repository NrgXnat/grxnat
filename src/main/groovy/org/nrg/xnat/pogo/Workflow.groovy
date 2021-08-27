package org.nrg.xnat.pogo

import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.nrg.xnat.jackson.naming.OverrideFieldBase
import org.nrg.xnat.jackson.naming.WorkflowNamer

@JsonNaming(WorkflowNamer)
class Workflow {

    public static final String WORKFLOW_COMPLETE = 'Complete'
    public static final String WORKFLOW_FAILED = 'Failed'
    public static final String WORKFLOW_QUEUED = 'Queued'

    String launchTime
    String comments
    @OverrideFieldBase('ExternalID') String externalId
    String dataType
    String justification
    @OverrideFieldBase('ID') String id
    String category
    String type
    @OverrideFieldBase('wrk_workflowData_id') int workflowDataId
    String pipelineName
    String status

}
