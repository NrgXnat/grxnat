package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.util.ListUtils

class ImagingSession extends SubjectAssessor {

    protected final List<Scan> scans = []
    protected final List<SessionAssessor> assessors = []

    ImagingSession(Project project, Subject subject, String label) {
        super(project, subject, label)
    }

    ImagingSession(Project project, Subject subject) {
        this(project, subject, null)
    }

    ImagingSession(String label) {
        this(null, null, label)
    }

    ImagingSession() {
        this(null, null, null)
    }

    List<Scan> getScans() {
        return scans
    }

    void setScans(List<Scan> scans) {
        ListUtils.copyInto(scans, this.scans)
    }

    void removeScan(Scan scan) {
        scans.remove(scan)
    }

    List<SessionAssessor> getAssessors() {
        return ListUtils.copy(assessors)
    }

    void setAssessors(List<SessionAssessor> assessors) {
        ListUtils.copyInto(assessors, this.assessors)
    }

    void removeAssessor(SessionAssessor assessor) {
        assessors.remove(assessor)
    }

    @SuppressWarnings("unchecked")
    <T extends ImagingSession> T scans(List<Scan> scans) {
        ListUtils.copyInto(scans, this.scans)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends ImagingSession> T addScan(Scan scan) {
        if (!scans.any { it.id == scan.id }) scans.add(scan)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends ImagingSession> T assessors(List<SessionAssessor> assessors) {
        ListUtils.copyInto(assessors, this.assessors)
        return (T)this
    }

    @SuppressWarnings("unchecked")
    <T extends ImagingSession> T addAssessor(SessionAssessor assessor) {
        if (!assessors.contains(assessor)) assessors.add(assessor)
        return (T)this
    }

    SessionAssessor findSessionAssessor(String label) {
        assessors.find { it.label == label }
    }

    Scan findScan(String scanId) {
        scans.find { it.id == scanId }
    }

    List<Scan> filterScansByType(String type) {
        filterScansByType([type])
    }

    List<Scan> filterScansByType(List<String> acceptableTypes) {
        scans.findAll { it.type in acceptableTypes }
    }

    Scan findSeriesByUid(String uid) {
        scans.find { it.uid == uid }
    }

    List<SessionAssessor> filterAssessorsByDataType(List<DataType> dataTypes) {
        assessors.findAll { it.dataType.xsiType in dataTypes.xsiType }
    }

}
