package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.CustomFieldScope
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestCustomFieldScope {

    private static final String PROJ_ID = 'PROJ_0001'
    private static final String SUBJ_ID = 'XNAT_S00001'
    private static final String SUBJ_LABEL = 'SUBJ001'
    private static final String EXPT_ID = 'XNAT_E00001'
    private static final String EXPT_LABEL = SUBJ_LABEL + '_MR1'
    private static final String ASSESSOR_ID = 'XNAT_E00002'
    private static final String ASSESSOR_LABEL = EXPT_LABEL + '_QC'
    private static final String SCAN_ID = '1'
    private static final String PROJECTS = 'projects'
    private static final String SUBJECTS = 'subjects'
    private static final String EXPERIMENTS = 'experiments'
    private static final String ASSESSORS = 'assessors'
    private static final String SCANS = 'scans'

    @Test
    void testPublicScope() {
        assertEquals(
                formatUriFragment(PROJECTS, PROJ_ID),
                new CustomFieldScope().project(new Project(PROJ_ID)).buildUriFragment()
        )
    }

    @Test
    void testFullAssessorPathWithIDs() {
        assertEquals(
                formatUriFragment(PROJECTS, PROJ_ID, SUBJECTS, SUBJ_ID, EXPERIMENTS, EXPT_ID, ASSESSORS, ASSESSOR_ID),
                new CustomFieldScope().
                        project(new Project(PROJ_ID)).
                        subject(new Subject().label(SUBJ_LABEL).accessionNumber(SUBJ_ID)).
                        experiment(new ImagingSession().label(EXPT_LABEL).accessionNumber(EXPT_ID) as SubjectAssessor).
                        assessor(new SessionAssessor().label(ASSESSOR_LABEL).accessionNumber(ASSESSOR_ID) as SessionAssessor).
                        buildUriFragment()
        )
    }

    @Test
    void testScanPathWithLabels() {
        assertEquals(
                formatUriFragment(PROJECTS, PROJ_ID, SUBJECTS, SUBJ_LABEL, EXPERIMENTS, EXPT_LABEL, SCANS, SCAN_ID),
                new CustomFieldScope().
                        project(new Project(PROJ_ID)).
                        subject(new Subject().label(SUBJ_LABEL)).
                        experiment(new ImagingSession().label(EXPT_LABEL) as SubjectAssessor).
                        scan(new Scan().id(SCAN_ID)).
                        buildUriFragment()
        )
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    private String formatUriFragment(String... strings) {
        final List<String> toJoin = ['custom-fields']
        toJoin.addAll(strings)
        toJoin.join('/')
    }

}
