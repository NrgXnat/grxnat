package org.nrg.xnat.jackson.naming

import org.nrg.xnat.pogo.DataType

class WorkflowNamer extends DataTypePropertyNamer {

    WorkflowNamer() {
        super(DataType.WORKFLOW.xsiType, new SnakeCaseStrategy())
    }

}
