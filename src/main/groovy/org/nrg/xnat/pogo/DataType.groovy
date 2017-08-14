package org.nrg.xnat.pogo

class DataType {

    static final List<DataType> KNOWN_TYPES = []

    static final DataType PROJECT = new DataType("xnat:projectData", null, "Project", "Projects")
    static final DataType SUBJECT = new DataType("xnat:subjectData", null, "Subject", "Subjects")
    static final DataType MR_SESSION = makeSessionDataType("mr")
    static final DataType PET_SESSION = makeSessionDataType("pet")
    static final DataType CT_SESSION = makeSessionDataType("ct")
    static final DataType CR_SESSION = makeSessionDataType("cr")
    static final DataType PET_MR_SESSION = new DataType("xnat:petmrSessionData", "PETMR", "PET MR Session", "PET MR Sessions")
    static final DataType MANUAL_QC = new DataType("xnat:qcManualAssessorData", "MQC", "Manual QC", "Manual QCs")
    static final DataType QC = new DataType("xnat:qcAssessmentData", "QC", "Auto QC", "Auto QCs")
    static final DataType MR_SCAN = makeScanDataType("mr")
    static final DataType PET_SCAN = makeScanDataType("pet")
    static final DataType CT_SCAN = makeScanDataType("ct")

    String xsiType
    String code
    String singularName
    private String pluralName

    DataType(String xsiType, String code, String singularName, String pluralName) {
        this.xsiType = xsiType
        this.code = code
        this.singularName = singularName
        this.pluralName = pluralName
        KNOWN_TYPES.add(this)
    }

    DataType() {}

    static DataType makeSessionDataType(String sessionCode) {
        new DataType(String.format("xnat:%sSessionData", sessionCode.toLowerCase()), sessionCode.toUpperCase(), String.format("%s Session", sessionCode.toUpperCase()), String.format("%s Sessions", sessionCode.toUpperCase()))
    }

    static DataType makeScanDataType(String sessionCode) {
        new DataType(String.format("xnat:%sScanData", sessionCode.toLowerCase()), null, null, null)
    }

    static DataType lookup(String xsiType) {
        for (DataType dataType : KNOWN_TYPES) {
            if (dataType.getXsiType() == xsiType) return dataType
        }
        new DataType().xsiType(xsiType)
    }

    String getPluralName() {
        return (pluralName == null) ? singularName + "s" : pluralName
    }

    void setPluralName(String pluralName) {
        this.pluralName = pluralName
    }

    DataType xsiType(String xsiType) {
        setXsiType(xsiType)
        return this
    }

    DataType code(String code) {
        setCode(code)
        return this
    }

    DataType singularName(String singularName) {
        setSingularName(singularName)
        return this
    }

    DataType pluralName(String pluralName) {
        setPluralName(pluralName)
        return this
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
