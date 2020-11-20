package org.nrg.xnat.enums

import org.nrg.testing.CommonStringUtils

enum WorkflowStatus {
    QUEUED    ('Queued'),
    RUNNING   ('Running'),
    COMPLETE  ('Complete'),
    FAILED    ('Failed')

    final String status

    WorkflowStatus(String status) {
        this.status = status
    }

    static WorkflowStatus get(String statusString) {
        values().find { workflowStatus ->
            workflowStatus.status == CommonStringUtils.stripEnclosingBrackets(statusString)
        }
    }
}
