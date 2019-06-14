package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Share
import org.nrg.xnat.pogo.Shareable
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.custom_variable.CustomVariableContainer
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils
import org.nrg.xnat.util.MapUtils

import java.time.LocalDate

abstract class Experiment extends CustomVariableContainer<Experiment> implements Shareable {

    Project primaryProject
    Subject subject
    String label
    DataType dataType
    LocalDate date
    String accessionNumber
    String notes
    protected final Map<String, String> specificFields = [:]
    protected final List<Resource> resources = []
    protected final List<Share> shares = []

    Experiment() {
        final DataType potentialMatch = DataType.lookupDataTypeByAssociatedClass(this.class)
        if (potentialMatch != null) setDataType(potentialMatch)
    }

    Map<String, String> getSpecificFields() {
        specificFields
    }

    void setSpecificFields(Map<String, String> values) {
        MapUtils.copyInto(values, specificFields)
    }

    List<Resource> getResources() {
        resources
    }

    void setResources(List<Resource> resources) {
        ListUtils.copyInto(resources, this.resources)
    }

    List<Share> getShares() {
        shares
    }

    void setShares(List<Share> shares) {
        ListUtils.copyInto(shares, this.shares)
    }

    Extension<Experiment> getExtension() {
        super.getExtension()
    }

    protected void setExtension(Extension<Experiment> extension) {
        super.setExtension(extension)
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T project(Project project) {
        setPrimaryProject(project)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T subject(Subject subject) {
        setSubject(subject)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T label(String label) {
        setLabel(label)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T dataType(DataType dataType) {
        setDataType(dataType)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T date(LocalDate date) {
        setDate(date)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T accessionNumber(String accessionNumber) {
        setAccessionNumber(accessionNumber)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T notes(String notes) {
        setNotes(notes)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T specificFields(Map<String, String> fields) {
        setSpecificFields(fields)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T resources(List<Resource> resources) {
        setResources(resources)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T addResource(Resource resource) {
        if (!resources.contains(resource)) resources.add(resource)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T shares(List<Share> shares) {
        setShares(shares)
        (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T addShare(Share share) {
        shares.add(share)
        (T)this
    }

    @SuppressWarnings("unchecked")
    protected <T extends Experiment> T extension(Extension<Experiment> extension) {
        setExtension(extension)
        (T)this
    }

    @Override
    String fieldBaseDataType() {
        'xnat:experimentData'
    }

    @Override
    String toString() {
        label
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Experiment)) return false

        Experiment that = (Experiment) o

        if (label != that.label) return false
        if (primaryProject != that.primaryProject) return false

        true
    }

    int hashCode() {
        int result
        result = (primaryProject != null ? primaryProject.hashCode() : 0)
        result = 31 * result + (label != null ? label.hashCode() : 0)
        result
    }

}