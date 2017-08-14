package org.nrg.xnat.pogo.experiments

import org.joda.time.LocalDate
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.custom_variable.CustomVariableContainer
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils

abstract class Experiment extends CustomVariableContainer<Experiment> {

    Project primaryProject
    Subject subject
    String label
    DataType dataType
    LocalDate date
    String accessionNumber
    protected final List<Resource> resources = []
    protected final List<Share> shares = []

    List<Resource> getResources() {
        return resources
    }

    void setResources(List<Resource> resources) {
        ListUtils.copyInto(resources, this.resources)
    }

    List<Share> getShares() {
        return shares
    }

    void setShares(List<Share> shares) {
        ListUtils.copyInto(shares, this.shares)
    }

    Extension<Experiment> getExtension() {
        return super.getExtension()
    }

    protected void setExtension(Extension<Experiment> extension) {
        super.setExtension(extension)
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T project(Project project) {
        setPrimaryProject(project)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T subject(Subject subject) {
        setSubject(subject)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T label(String label) {
        setLabel(label)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T dataType(DataType dataType) {
        setDataType(dataType)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T date(LocalDate date) {
        setDate(date)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T accessionNumber(String accessionNumber) {
        setAccessionNumber(accessionNumber)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T resources(List<Resource> resources) {
        setResources(resources)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T addResource(Resource resource) {
        if (!resources.contains(resource)) resources.add(resource)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T shares(List<Share> shares) {
        setShares(shares)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T addShare(Share share) {
        shares.add(share)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    protected <T extends Experiment> T extension(Extension<Experiment> extension) {
        setExtension(extension)
        return (T)this
    }

    @Override
    String toString() {
        return label
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Experiment that = (Experiment) o

        if (label != that.label) return false
        if (primaryProject != that.primaryProject) return false

        return true
    }

    int hashCode() {
        int result
        result = (primaryProject != null ? primaryProject.hashCode() : 0)
        result = 31 * result + (label != null ? label.hashCode() : 0)
        return result
    }

}
