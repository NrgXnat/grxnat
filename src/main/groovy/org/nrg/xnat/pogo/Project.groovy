package org.nrg.xnat.pogo

import org.nrg.xnat.enums.Accessibility
import org.nrg.xnat.enums.PrearchiveCode
import org.nrg.xnat.pogo.custom_variable.CustomVariableSet
import org.nrg.xnat.pogo.events.Subscription
import org.nrg.xnat.pogo.extensions.project.ProjectExtension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.users.CustomUserGroup
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.users.UserGroup
import org.nrg.xnat.pogo.users.UserGroups
import org.nrg.xnat.util.ListUtils
import org.nrg.xnat.util.RandomUtils

class Project extends Extensible<Project> {

    String id
    private String runningTitle
    private String title
    String description
    private final List<String> keywords = []
    private final List<String> aliases = []
    Investigator pi
    private final List<Investigator> investigators = []
    private final Map<UserGroup, List<User>> users = constructDefaultUserMap()
    private Accessibility accessibility = Accessibility.PRIVATE
    PrearchiveCode prearchiveCode
    private final List<Subject> subjects = []
    private final List<Subject> secondarySubjects = []
    private final List<Resource> projectResources = []
    boolean subjectParallelization = false
    boolean subjectAssessorParallelization = false
    boolean sessionAssessorParallelization = false
    boolean scanParallelization = false
    private final List<Subscription> subscriptions = []
    private final List<CustomVariableSet> variableSets = []

    Project(String id) {
        super()
        setId(id)
    }

    Project() {
        this(RandomUtils.randomID())
    }

    String getRunningTitle() {
        runningTitle ?: id
    }

    void setRunningTitle(String runningTitle) {
        this.runningTitle = runningTitle
    }

    String getTitle() {
        title ?: id
    }

    void setTitle(String title) {
        this.title = title
    }

    List<String> getKeywords() {
        keywords
    }

    void setKeywords(List<String> keywords) {
        ListUtils.copyInto(keywords, this.keywords)
    }

    List<String> getAliases() {
        aliases
    }

    void setAliases(List<String> aliases) {
        ListUtils.copyInto(aliases, this.aliases)
    }

    List<Investigator> getInvestigators() {
        investigators
    }

    void setInvestigators(List<Investigator> investigators) {
        ListUtils.copyInto(investigators, this.investigators)
    }

    Map<UserGroup, List<User>> getUsers() {
        users
    }

    List<User> getOwners() {
        users.get(UserGroups.OWNER)
    }

    void setOwners(List<User> owners) {
        ListUtils.copyInto(owners, getOwners())
    }

    List<User> getMembers() {
        users.get(UserGroups.MEMBER)
    }

    void setMembers(List<User> members) {
        ListUtils.copyInto(members, getMembers())
    }

    List<User> getCollaborators() {
        users.get(UserGroups.COLLABORATOR)
    }

    void setCollaborators(List<User> collaborators) {
        ListUtils.copyInto(collaborators, getCollaborators())
    }

    Accessibility getAccessibility() {
        accessibility
    }

    void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility
    }

    void setAccessibility(String accessibility) {
        setAccessibility(Accessibility.get(accessibility))
    }

    List<Subject> getSubjects() {
        ListUtils.copy(subjects)
    }

    void setSubjects(List<Subject> subjects) {
        ListUtils.copyInto(subjects, this.subjects)
    }

    void removeSubject(Subject subject) {
        subjects.remove(subject)
    }

    List<Subject> getSecondarySubjects() {
        secondarySubjects
    }

    void setSecondarySubjects(List<Subject> secondarySubjects) {
        ListUtils.copyInto(secondarySubjects, this.secondarySubjects)
    }

    List<Subject> getAllSubjects() {
        subjects + secondarySubjects
    }

    List<Resource> getProjectResources() {
        projectResources
    }

    void setProjectResources(List<Resource> projectResources) {
        ListUtils.copyInto(projectResources, this.projectResources)
    }

    ProjectExtension getExtension() {
        super.extension as ProjectExtension
    }

    void setExtension(ProjectExtension extension) {
        super.setExtension(extension)
    }

    void setCustomUserGroups(List<CustomUserGroup> userGroups) {
        userGroups.each { group ->
            users[group] = []
        }
    }

    Set<CustomUserGroup> getCustomUserGroups() {
        users.keySet().findAll { it instanceof CustomUserGroup } as Set<CustomUserGroup>
    }

    void setSubscriptions(List<Subscription> subscriptions) {
        ListUtils.copyInto(subscriptions, this.subscriptions)
    }

    List<Subscription> getSubscriptions() {
        subscriptions
    }

    void setVariableSets(List<CustomVariableSet> sets) {
        ListUtils.copyInto(sets, variableSets)
    }

    List<CustomVariableSet> getVariableSets() {
        variableSets
    }

    Project id(String id) {
        setId(id)
        this
    }

    Project runningTitle(String runningTitle) {
        setRunningTitle(runningTitle)
        this
    }

    Project title(String title) {
        setTitle(title)
        this
    }

    Project description(String description) {
        setDescription(description)
        this
    }

    Project keywords(List<String> keywords) {
        setKeywords(keywords)
        this
    }

    Project addKeyword(String keyword) {
        keywords.add(keyword)
        this
    }

    Project aliases(List<String> aliases) {
        setAliases(aliases)
        this
    }

    Project addAlias(String alias) {
        aliases.add(alias)
        this
    }

    Project pi(Investigator pi) {
        setPi(pi)
        this
    }

    Project investigators(List<Investigator> investigators) {
        setInvestigators(investigators)
        this
    }

    Project owners(List<User> owners) {
        setOwners(owners)
        this
    }

    Project addOwner(User owner) {
        getOwners().add(owner)
        this
    }

    Project members(List<User> members) {
        setMembers(members)
        this
    }

    Project addMember(User member) {
        getMembers().add(member)
        this
    }

    Project collaborators(List<User> collaborators) {
        setCollaborators(collaborators)
        this
    }

    Project addCollaborator(User collaborator) {
        getCollaborators().add(collaborator)
        this
    }

    Project accessibility(Accessibility accessibility) {
        setAccessibility(accessibility)
        this
    }

    Project accessibility(String accessibility) {
        setAccessibility(accessibility)
        this
    }

    Project prearchiveCode(PrearchiveCode code) {
        setPrearchiveCode(code)
        this
    }

    Project subjects(List<Subject> subjects) {
        setSubjects(subjects)
        this
    }

    Project secondarySubjects(List<Subject> subjects) {
        setSecondarySubjects(subjects)
        this
    }

    Project addSubject(Subject subject) {
        if (!subjects.contains(subject)) subjects.add(subject)
        this
    }

    Project resources(List<Resource> resources) {
        setProjectResources(resources)
        this
    }

    Project addResource(Resource resource) {
        if (!projectResources.contains(resource)) projectResources.add(resource)
        this
    }

    Project extension(ProjectExtension extension) {
        setExtension(extension)
        this
    }

    Project addUserGroup(UserGroup userGroup, List<User> userList) {
        final List<User> listCopy = ListUtils.copy(userList)
        users.put(userGroup, listCopy)
        this
    }

    Project enableSubjectParallelization() {
        setSubjectParallelization(true)
        this
    }

    Project enableSubjectAssessorParallelization() {
        setSubjectAssessorParallelization(true)
        this
    }

    Project enableSessionAssessorParallelization() {
        setSessionAssessorParallelization(true)
        this
    }

    Project enableScanParallelization() {
        setScanParallelization(true)
        this
    }

    Project subscriptions(List<Subscription> subscriptions) {
        setSubscriptions(subscriptions)
        this
    }

    Project variableSets(List<CustomVariableSet> variableSets) {
        setVariableSets(variableSets)
        this
    }

    Subject findSubject(String label) {
        subjects.find { it.label == label }
    }

    private Map<UserGroup, List<User>> constructDefaultUserMap() {
        final Map<UserGroup, List<User>> userMap = [:]
        userMap.put(UserGroups.OWNER, [])
        userMap.put(UserGroups.MEMBER, [])
        userMap.put(UserGroups.COLLABORATOR, [])
        userMap
    }

    @Override
    String toString() {
        id
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Project project = (Project) o

        if (id != project.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

}
