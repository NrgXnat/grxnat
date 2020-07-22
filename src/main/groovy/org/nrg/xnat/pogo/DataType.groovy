package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonIgnore
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.assessors.ManualQC
import org.nrg.xnat.pogo.experiments.scans.CTScan
import org.nrg.xnat.pogo.experiments.scans.MRScan
import org.nrg.xnat.pogo.experiments.scans.PETScan
import org.nrg.xnat.pogo.experiments.sessions.CTSession
import org.nrg.xnat.pogo.experiments.sessions.MRSession
import org.nrg.xnat.pogo.experiments.sessions.PETMRSession
import org.nrg.xnat.pogo.experiments.sessions.PETSession

class DataType {

    static final List<DataType> KNOWN_TYPES = []

    // Concrete types
    public static final DataType PROJECT = new DataType('xnat:projectData', null, 'Project')
    public static final DataType SUBJECT = new DataType('xnat:subjectData', null, 'Subject')
    public static final DataType MR_SESSION = makeSessionDataType('mr').associatedClass(MRSession)
    public static final DataType PET_SESSION = makeSessionDataType('pet').associatedClass(PETSession)
    public static final DataType CT_SESSION = makeSessionDataType('ct').associatedClass(CTSession)
    public static final DataType CR_SESSION = makeSessionDataType('cr')
    public static final DataType PET_MR_SESSION = new DataType('xnat:petmrSessionData', 'PETMR', 'PET MR Session').associatedClass(PETMRSession)
    public static final DataType MANUAL_QC = new DataType('xnat:qcManualAssessorData', 'MQC', 'Manual QC').associatedClass(ManualQC)
    public static final DataType QC = new DataType('xnat:qcAssessmentData', 'QC', 'Auto QC').associatedClass(org.nrg.xnat.pogo.experiments.assessors.QC)
    public static final DataType MR_SCAN = makeScanDataType('mr').associatedScanClass(MRScan)
    public static final DataType PET_SCAN = makeScanDataType('pet').associatedScanClass(PETScan)
    public static final DataType CT_SCAN = makeScanDataType('ct').associatedScanClass(CTScan)
    public static final DataType FREESURFER = new DataType('fs:fsData', null, 'Freesurfer', 'Freesurfers')
    // Abstract types
    public static final DataType SUBJECT_ASSESSOR = new DataType('xnat:subjectAssessorData', null, null, null)
    public static final DataType IMAGE_SESSION = new DataType('xnat:imageSessionData', null, null, null)
    public static final DataType IMAGE_SCAN = new DataType('xnat:imageScanData', null, null, null)
    public static final DataType IMAGE_ASSESSOR = new DataType('xnat:imageAssessorData', null, null, null)

    String xsiType
    String code
    String singularName
    private String pluralName
    @JsonIgnore Class<? extends Experiment> associatedExperimentClass
    @JsonIgnore Class<? extends Scan> associatedScanClass

    DataType(String xsiType, String code, String singularName, String pluralName) {
        this.xsiType = xsiType
        this.code = code
        this.singularName = singularName
        this.pluralName = pluralName
        KNOWN_TYPES << this
    }

    DataType(String xsiType, String code, String singularName) {
        this(xsiType, code, singularName, "${singularName}s")
    }

    DataType() {}

    static DataType makeSessionDataType(String sessionCode) {
        new DataType("xnat:${sessionCode.toLowerCase()}SessionData", sessionCode.toUpperCase(), "${sessionCode.toUpperCase()} Session")
    }

    static DataType makeScanDataType(String sessionCode) {
        new DataType("xnat:${sessionCode.toLowerCase()}ScanData", null, "${sessionCode.toUpperCase()} Scan")
    }

    static DataType lookup(String xsiType) {
        KNOWN_TYPES.find {dataType ->
            dataType.xsiType == xsiType
        } ?: new DataType().xsiType(xsiType)
    }

    static DataType lookupDataTypeByAssociatedClass(Class aClass) {
        KNOWN_TYPES.find { it.associatedExperimentClass == aClass || it.associatedScanClass == aClass }
    }

    String getPluralName() {
        return (pluralName == null) ? singularName + 's' : pluralName
    }

    void setPluralName(String pluralName) {
        this.pluralName = pluralName
    }

    DataType xsiType(String xsiType) {
        setXsiType(xsiType)
        this
    }

    DataType code(String code) {
        setCode(code)
        this
    }

    DataType singularName(String singularName) {
        setSingularName(singularName)
        this
    }

    DataType pluralName(String pluralName) {
        setPluralName(pluralName)
        this
    }

    DataType associatedClass(Class<? extends Experiment> aClass) {
        setAssociatedExperimentClass(aClass)
        this
    }

    DataType associatedScanClass(Class<? extends Scan> aClass) {
        setAssociatedScanClass(aClass)
        this
    }

    @Override
    String toString() {
        xsiType
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DataType dataType = (DataType) o

        if (code != dataType.code) return false
        if (pluralName != dataType.pluralName) return false
        if (singularName != dataType.singularName) return false
        if (xsiType != dataType.xsiType) return false

        return true
    }

    int hashCode() {
        int result
        result = (xsiType != null ? xsiType.hashCode() : 0)
        result = 31 * result + (code != null ? code.hashCode() : 0)
        result = 31 * result + (singularName != null ? singularName.hashCode() : 0)
        result = 31 * result + (pluralName != null ? pluralName.hashCode() : 0)
        return result
    }

}
