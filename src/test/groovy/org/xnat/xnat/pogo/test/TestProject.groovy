package org.xnat.xnat.pogo.test

import org.nrg.xnat.enums.Accessibility
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.resources.ProjectResource
import org.nrg.xnat.pogo.resources.Resource
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

class TestProject {

    @Test
    void testDefaults() {
        final Project project = new Project()
        final String generatedId = project.id

        assertNotNull(generatedId)
        assertEquals(generatedId, project.runningTitle)
        assertEquals(generatedId, project.title)
        assertEquals(Accessibility.PRIVATE, project.accessibility)
    }

    @Test
    void testFullProject() {
        final Investigator investigator1 = new Investigator().firstname('Test').lastname('User')
        final Investigator investigator2 = new Investigator().firstname('Other').lastname('Person')
        final User userA = new User('userA')
        final User userB = new User('userB')
        final User userC = new User('userC')
        final User userD = new User('userD')

        final String id = 'test_project001'
        final String runningTitle = 'Test Study 001'
        final String title = 'XNAT POGO Test Study 001'
        final String description = 'This is part of a unit test for POGOs in grxnat.'
        final List<String> keywords = ['key1', 'key2']
        final List<String> aliases = ['altName1', 'altNameA']
        final Investigator pi = investigator1
        final List<Investigator> investigators = [investigator2]
        final List<User> owners = [userA]
        final List<User> members = [userB]
        final List<User> collaborators = [userC, userD]
        final Accessibility accessibility = Accessibility.PUBLIC

        final Project project = new Project(id).runningTitle(runningTitle).title(title).description(description).keywords(keywords).aliases(aliases).
                pi(pi).investigators(investigators).owners(owners).members(members).collaborators(collaborators).accessibility(accessibility)
        assertEquals(id, project.id)
        assertEquals(runningTitle, project.runningTitle)
        assertEquals(title, project.title)
        assertEquals(description, project.description)
        assertEquals(keywords, project.keywords)
        assertEquals(aliases, project.aliases)
        assertEquals(pi, project.pi)
        assertEquals(investigators, project.investigators)
        assertEquals(owners, project.owners)
        assertEquals(members, project.members)
        assertEquals(collaborators, project.collaborators)
        assertEquals(accessibility, project.accessibility)
        assertTrue(project.subjects.isEmpty())
        assertTrue(project.projectResources.isEmpty())

        final Subject subjectA = new Subject(project, 'testSubjectA')
        final Subject subjectB = new Subject(project, 'testSubjectB')
        final Subject subjectC = new Subject(project, 'testSubjectC')
        final List<Subject> subjects = [subjectA, subjectB, subjectC]
        final Resource resourceA = new ProjectResource(project, 'resourceA')
        final Resource resourceB = new ProjectResource(project, 'resourceB')
        final List<Resource> resources = [resourceA, resourceB]

        project.resources([resourceA, resourceB]).subjects([subjectA, subjectB, subjectC])
        assertEquals(subjects, project.subjects)
        assertEquals(resources, project.projectResources)
    }

}
