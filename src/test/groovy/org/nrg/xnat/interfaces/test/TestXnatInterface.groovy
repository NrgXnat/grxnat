package org.nrg.xnat.interfaces.test

import org.nrg.xnat.interfaces.UnitTestXnatInterface
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.assessors.QC
import org.nrg.xnat.pogo.experiments.scans.MRScan
import org.nrg.xnat.pogo.experiments.sessions.MRSession
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestXnatInterface {
    
    @Test
    void testUrls() {
        final String baseUrl = 'https://xnat.myuniversity.edu'
        final String projectId = 'MY_STUDY_A'
        final String subjectLabel = 'SUBJ_001'
        final String sessionLabel = "${subjectLabel}_MR1"
        final String scanId = '1'
        final String reconstructionLabel = "${sessionLabel}_RECON1"
        final String sessionAssessorLabel = "${sessionLabel}_QC"
        final XnatInterface xnatInterface = new UnitTestXnatInterface('https://xnat.myuniversity.edu') // version of XNAT doesn't matter for this
        final Project project = new Project(projectId)
        final Subject subject = new Subject(project, subjectLabel)
        final ImagingSession session = new MRSession(project, subject, sessionLabel)
        final Scan scan = new MRScan(session, scanId)
        final Reconstruction reconstruction = new Reconstruction(session, reconstructionLabel)
        final SessionAssessor sessionAssessor = new QC(project, subject, session, sessionAssessorLabel)

        assertEquals("${baseUrl}/data/projects/${projectId}", xnatInterface.projectUrl(project))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}", xnatInterface.subjectUrl(project, subject))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}", xnatInterface.subjectUrl(subject))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}", xnatInterface.subjectAssessorUrl(project, subject, session))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}", xnatInterface.subjectAssessorUrl(session))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/scans/${scanId}", xnatInterface.scanUrl(project, subject, session, scan))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/scans/${scanId}", xnatInterface.scanUrl(scan))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/reconstructions/${reconstructionLabel}", xnatInterface.reconstructionUrl(project, subject, session, reconstruction))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/reconstructions/${reconstructionLabel}", xnatInterface.reconstructionUrl(reconstruction))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/assessors/${sessionAssessorLabel}", xnatInterface.sessionAssessorUrl(project, subject, session, sessionAssessor))
        assertEquals("${baseUrl}/data/projects/${projectId}/subjects/${subjectLabel}/experiments/${sessionLabel}/assessors/${sessionAssessorLabel}", xnatInterface.sessionAssessorUrl(sessionAssessor))
    }
    
}
