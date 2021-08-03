package org.nrg.xnat.importer.params

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

trait DestRequest<X extends DestRequest<X>> {

    private static final String PROJECT_PARAM_NAME = 'project'
    private static final String SUBJECT_PARAM_NAME = 'subject'
    private static final String TIMESTAMP_PARAM_NAME = 'timestamp'

    String dest

    X dest(String dest) {
        setDest(dest)
        this as X
    }

    X destPrearchive(Project project = null, String timestamp = null, String session = null) {
        String destination = '/prearchive'
        if (project != null) {
            destination += '/projects/' + project.id
        }
        if (timestamp != null) {
            complainParamMissing(PROJECT_PARAM_NAME, project?.id)
            destination += '/' + timestamp
        }
        if (session != null) {
            complainParamMissing(TIMESTAMP_PARAM_NAME, timestamp)
            complainParamMissing(PROJECT_PARAM_NAME, project?.id)
            destination += '/' + session
        }
        dest(destination)
    }

    X destArchive(Project project = null, Subject subject = null, ImagingSession session = null) {
        String destination = '/archive'
        if (project != null) {
            destination += '/projects/' + project.id
        }
        if (subject != null) {
            complainParamMissing(PROJECT_PARAM_NAME, project?.id)
            destination += '/subjects/' + subject.label
        }
        if (session != null) {
            complainParamMissing(SUBJECT_PARAM_NAME, subject?.label)
            complainParamMissing(PROJECT_PARAM_NAME, project?.id)
            destination += '/experiments/' + session.label
        }
        dest(destination)
    }

    void complainParamMissing(String paramName, String paramValue) {
        if (paramValue == null) {
            throw new UnsupportedOperationException("Other dest path params being provided require ${paramName} to also be provided.")
        }
    }

}