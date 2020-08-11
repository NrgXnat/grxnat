package org.nrg.xnat.pogo

import org.nrg.xnat.enums.Gender
import org.nrg.xnat.enums.Handedness
import org.nrg.xnat.pogo.custom_variable.CustomVariableContainer
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.pogo.extensions.subject.SubjectExtension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils
import org.nrg.xnat.util.RandomUtils
import org.nrg.xnat.util.XnatUriUtils

import java.time.LocalDate

class Subject extends CustomVariableContainer<Subject> implements Shareable, HasUri {

    private Project project
    String label
    private Gender gender = Gender.UNKNOWN
    int yob
    int age
    LocalDate dob
    private Handedness handedness = Handedness.UNKNOWN
    String race
    String ethnicity
    int height
    int weight
    String src
    int education
    String group
    String accessionNumber
    Investigator pi
    private final List<Share> shares = []
    private final List<Resource> resources = []
    private final List<SubjectAssessor> experiments = []

    Subject(Project project, String label) {
        this.label = label
        setProject(project)
    }

    Subject(Project project) {
        this(project, RandomUtils.randomID())
    }

    Subject() {
        this(null)
    }

    @Override
    String getUri() {
        XnatUriUtils.subjectUri(project.id, label, true)
    }

    Project getProject() {
        return project
    }

    void setProject(Project project) {
        this.project = project
        if (project != null) project.addSubject(this)
    }

    Gender getGender() {
        return gender
    }

    void setGender(Gender gender) {
        this.gender = gender
    }

    void setGender(String gender) {
        if (gender != null) setGender(Gender.get(gender))
    }

    Handedness getHandedness() {
        return handedness
    }

    void setHandedness(Handedness handedness) {
        this.handedness = handedness
    }

    void setHandedness(String handedness) {
        if (handedness != null) setHandedness(Handedness.get(handedness))
    }

    List<Share> getShares() {
        return shares
    }

    void setShares(List<Share> shares) {
        ListUtils.copyInto(shares, this.shares)
    }

    List<Resource> getResources() {
        return resources
    }

    void setResources(List<Resource> resources) {
        ListUtils.copyInto(resources, this.resources)
    }

    List<SubjectAssessor> getExperiments() {
        return ListUtils.copy(experiments)
    }

    void setExperiments(List<SubjectAssessor> experiments) {
        ListUtils.copyInto(experiments, this.experiments)
    }

    void removeExperiment(SubjectAssessor subjectAssessor) {
        experiments.remove(subjectAssessor)
    }

    List<ImagingSession> getSessions() {
        experiments.findAll { it instanceof ImagingSession } as List<ImagingSession>
    }

    SubjectExtension getExtension() {
        return (SubjectExtension) super.getExtension()
    }

    void setExtension(SubjectExtension extension) {
        super.setExtension(extension)
    }

    Subject project(Project project) {
        setProject(project)
        this
    }

    Subject label(String label) {
        setLabel(label)
        this
    }

    Subject gender(Gender gender) {
        setGender(gender)
        this
    }

    Subject yob(int yob) {
        setYob(yob)
        this
    }

    Subject age(int age) {
        setAge(age)
        this
    }

    Subject dob(LocalDate dob) {
        setDob(dob)
        this
    }

    Subject handedness(Handedness handedness) {
        setHandedness(handedness)
        this
    }

    Subject race(String race) {
        setRace(race)
        this
    }

    Subject ethnicity(String ethnicity) {
        setEthnicity(ethnicity)
        this
    }

    Subject height(int height) {
        setHeight(height)
        this
    }

    Subject weight(int weight) {
        setWeight(weight)
        this
    }

    Subject src(String src) {
        setSrc(src)
        this
    }

    Subject education(int education) {
        setEducation(education)
        this
    }

    Subject shares(List<Share> shares) {
        setShares(shares)
        this
    }

    Subject addShare(Share share) {
        shares.add(share)
        this
    }

    Subject resources(List<Resource> resources) {
        setResources(resources)
        this
    }

    Subject addResource(Resource resource) {
        if (!resources.contains(resource)) resources.add(resource)
        this
    }

    Subject experiments(List<SubjectAssessor> experiments) {
        setExperiments(experiments)
        this
    }

    Subject addExperiment(SubjectAssessor experiment) {
        if (!experiments.contains(experiment)) experiments.add(experiment)
        this
    }

    Subject extension(SubjectExtension extension) {
        setExtension(extension)
        this
    }

    Subject accessionNumber(String accessionNumber) {
        setAccessionNumber(accessionNumber)
        this
    }

    Subject pi(Investigator investigator) {
        setPi(investigator)
        this
    }

    Subject group(String group) {
        setGroup(group)
        this
    }

    SubjectAssessor findSubjectAssessor(String label) {
        experiments.find { it.label == label }
    }

    @Override
    String fieldBaseDataType() {
        'xnat:subjectData'
    }

    @Override
    Project getPrimaryProject() {
        project
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Subject subject = (Subject) o

        if (label != subject.label) return false
        if (project != subject.project) return false

        return true
    }

    @Override
    int hashCode() {
        int result
        result = (project != null ? project.hashCode() : 0)
        result = 31 * result + (label != null ? label.hashCode() : 0)
        return result
    }

    @Override
    String toString() {
        label
    }

}
