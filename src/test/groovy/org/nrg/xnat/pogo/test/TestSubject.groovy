package org.nrg.xnat.pogo.test

import org.nrg.xnat.enums.Gender
import org.nrg.xnat.enums.Handedness
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.NonimagingAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.SubjectResource
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

class TestSubject {

    @Test
    void testDefaults() {
        final Gender gender = Gender.UNKNOWN
        final Handedness handedness = Handedness.UNKNOWN
        final Project project = new Project()
        final Subject subject = new Subject(project)

        assertNotNull(subject.label)
        assertEquals(gender, subject.gender)
        assertEquals(handedness, subject.handedness)
    }

    @Test
    void testFullSubject() {
        final Project project = new Project()
        final Project otherProject = new Project()
        final Project otherProject2 = new Project()

        final String label = 'test_subjectA'
        final Gender gender = Gender.MALE
        final int yob = 1990
        final Handedness handedness = Handedness.LEFT
        final String race = 'Indianapolis 500'
        final String ethnicity = 'Not latino'
        final int height = 72
        final int weight = 200
        final String src = 'src'
        final Subject subject = new Subject(project, label).gender(gender).yob(yob).handedness(handedness).
                race(race).ethnicity(ethnicity).height(height).weight(weight).src(src)

        assertEquals(label, subject.label)
        assertEquals(gender, subject.gender)
        assertEquals(yob, subject.yob)
        assertEquals(handedness, subject.handedness)
        assertEquals(race, subject.race)
        assertEquals(ethnicity, subject.ethnicity)
        assertEquals(height, subject.height)
        assertEquals(weight, subject.weight)
        assertEquals(src, subject.src)

        final Share share1 = new Share(otherProject, 'new_label')
        final Share share2 = new Share(otherProject2, 'new_label2')
        final List<Share> shares = [share1, share2]
        final Resource resource = new SubjectResource(project, subject, 'resource')
        final List<Resource> resources = [resource]
        final SubjectAssessor session = new ImagingSession(project, subject)
        final SubjectAssessor assessor = new NonimagingAssessor(project, subject, 'form1')
        final List<SubjectAssessor> subjectAssessors = [session, assessor]
        subject.shares(shares).resources(resources).experiments(subjectAssessors)

        assertEquals(shares, subject.shares)
        assertEquals(resources, subject.resources)
        assertEquals(subjectAssessors, subject.experiments)
    }
}
