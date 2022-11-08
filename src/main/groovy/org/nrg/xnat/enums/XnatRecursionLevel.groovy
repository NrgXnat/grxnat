package org.nrg.xnat.enums

enum XnatRecursionLevel {

    PROJECT_METADATA_ONLY (false, false, false, false),
    SUBJECTS_WITH_METADATA (true, false, false, false),
    SUBJECT_ASSESSORS_WITH_METADATA (true, true, false, false),
    SCANS_WITH_METADATA (true, true, true, false),
    SESSION_ASSESSORS_WITH_METADATA (true, true, false, true),
    FULL_RECURSION (true, true, true, true)

    boolean readSubjects
    boolean readSubjectAssessors
    boolean readScans
    boolean readSessionAssessors

    XnatRecursionLevel(boolean subject, boolean subjectAssessor, boolean scan, boolean sessionAssessor) {
        readSubjects = subject
        readSubjectAssessors = subjectAssessor
        readScans = scan
        readSessionAssessors = sessionAssessor
    }
}