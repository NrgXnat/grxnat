package org.nrg.xnat.util

class XnatUriUtils {

    private static final String ARCHIVE = '/archive'

    static String projectUri(String projectId, boolean includeArchiveComponent = false) {
        "${includeArchiveComponent ? ARCHIVE : ''}/projects/${projectId}"
    }

    static String subjectUri(String projectId, String subjectLabelOrId, boolean includeArchiveComponent = false) {
        "${projectUri(projectId, includeArchiveComponent)}/subjects/${subjectLabelOrId}"
    }

    static String subjectAssessorUri(String projectId, String subjectLabelOrId, String subjectAssessorLabelOrId, boolean includeArchiveComponent = false) {
        "${subjectUri(projectId, subjectLabelOrId, includeArchiveComponent)}/experiments/${subjectAssessorLabelOrId}"
    }

    static String sessionAssessorUri(String projectId, String subjectLabelOrId, String subjectAssessorLabelOrId, String sessionAssessorLabelOrId, boolean includeArchiveComponent = false) {
        "${subjectAssessorUri(projectId, subjectLabelOrId, subjectAssessorLabelOrId, includeArchiveComponent)}/assessors/${sessionAssessorLabelOrId}"
    }

    static String scanUri(String projectId, String subjectLabelOrId, String subjectAssessorLabelOrId, String scanId, boolean includeArchiveComponent = false) {
        "${subjectAssessorUri(projectId, subjectLabelOrId, subjectAssessorLabelOrId, includeArchiveComponent)}/scans/${scanId}"
    }

}
