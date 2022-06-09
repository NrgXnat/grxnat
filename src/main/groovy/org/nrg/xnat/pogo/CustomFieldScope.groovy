package org.nrg.xnat.pogo

import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class CustomFieldScope {

    String projectRep
    String subjectRep
    String experimentRep
    String assessorRep
    String scanRep

    CustomFieldScope project(Project projectValue) {
        project(projectValue.id)
    }

    CustomFieldScope project(String projectValue) {
        setProjectRep(projectValue)
        this
    }

    CustomFieldScope subject(Subject subjectValue) {
        subject(subjectValue.accessionNumber ?: subjectValue.label)
    }

    CustomFieldScope subject(String subjectValue) {
        setSubjectRep(subjectValue)
        this
    }

    CustomFieldScope experiment(SubjectAssessor subjectAssessor) {
        experiment(subjectAssessor.accessionNumber ?: subjectAssessor.label)
    }

    CustomFieldScope experiment(String subjectAssessor) {
        setExperimentRep(subjectAssessor)
        this
    }

    CustomFieldScope assessor(SessionAssessor sessionAssessor) {
        assessor(sessionAssessor.accessionNumber ?: sessionAssessor.label)
    }

    CustomFieldScope assessor(String sessionAssessor) {
        setAssessorRep(sessionAssessor)
        this
    }

    CustomFieldScope scan(Scan imageScan) {
        scan(imageScan.id)
    }

    CustomFieldScope scan(String scanId) {
        setScanRep(scanId)
        this
    }

    String buildUriFragment() {
        final List<String> components = ['custom-fields']
        if (projectRep != null) {
            components << 'projects'
            components << projectRep
        }
        if (subjectRep != null) {
            components << 'subjects'
            components << subjectRep
        }
        if (experimentRep != null) {
            components << 'experiments'
            components << experimentRep
        }
        if (assessorRep != null) {
            components << 'assessors'
            components << assessorRep
        }
        if (scanRep != null) {
            components << 'scans'
            components << scanRep
        }
        CommonStringUtils.formatUrl(components.toArray())
    }

}
