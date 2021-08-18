package org.nrg.xnat.enums

import org.nrg.testing.CommonStringUtils

enum WorkflowStatus {
    QUEUED     ('Queued'),
    _QUEUED    ('_Queued'),
    DIE        ('die'),
    _DIE       ('_die'),
    RUNNING    ('Running'),
    COMPLETE   ('Complete'),
    STAGING    ('Staging'),
    CREATED    ('Created'),
    FINALIZING ('Finalizing'),
    FAILED     ('Failed')

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
