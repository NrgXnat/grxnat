package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.util.XnatUriUtils
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestXnatUris {

    final Project project = new Project('PROJECT_A')
    final Subject subject = new Subject(project, 'SUBJECT_001')
    final ImagingSession session = new ImagingSession(project, subject, 'SUBJECT_001_MR1')
    final SessionAssessor sessionAssessor = new SessionAssessor(project, subject, session, 'SUBJECT_001_MR1_QC')
    final Scan scan = new Scan(session, '1')

    @Test
    void testProjectUris() {
        assertEquals('/archive/projects/PROJECT_A', project.uri)
        assertEquals('/projects/PROJECT_A', XnatUriUtils.projectUri(project.id))
    }

    @Test
    void testSubjectUris() {
        assertEquals('/archive/projects/PROJECT_A/subjects/SUBJECT_001', subject.uri)
        assertEquals('/projects/PROJECT_A/subjects/SUBJECT_001', XnatUriUtils.subjectUri(project.id, subject.label))
    }

    @Test
    void testSubjectAssessorUris() {
        assertEquals('/archive/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1', session.uri)
        assertEquals('/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1', XnatUriUtils.subjectAssessorUri(project.id, subject.label, session.label))
    }

    @Test
    void testSessionAssessorUris() {
        assertEquals('/archive/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1/assessors/SUBJECT_001_MR1_QC', sessionAssessor.uri)
        assertEquals('/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1/assessors/SUBJECT_001_MR1_QC', XnatUriUtils.sessionAssessorUri(project.id, subject.label, session.label, sessionAssessor.label))
    }

    @Test
    void testScanUris() {
        assertEquals('/archive/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1/scans/1', scan.uri)
        assertEquals('/projects/PROJECT_A/subjects/SUBJECT_001/experiments/SUBJECT_001_MR1/scans/1', XnatUriUtils.scanUri(project.id, subject.label, session.label, scan.id))

    }

}
