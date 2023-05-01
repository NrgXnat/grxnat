package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.EqualsAndHashCode
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

@EqualsAndHashCode(includes = ['xsiType', 'code', 'singularName', 'pluralName'])
class DataType {
    static final List<DataType> KNOWN_TYPES = []

    // Abstract types
    public static final DataType SUBJECT_ASSESSOR = new DataType('xnat:subjectAssessorData', null, null, null)
    public static final DataType IMAGE_SESSION = new DataType('xnat:imageSessionData', null, null, null)
    public static final DataType IMAGE_SCAN = new DataType('xnat:imageScanData', null, null, null)
    public static final DataType IMAGE_ASSESSOR = new DataType('xnat:imageAssessorData', null, null, null)
    public static final DataType PROJECT_ASSET = new DataType('xnat:abstractProjectAsset', null, null, null)
    // Core concrete types
    public static final DataType PROJECT = new DataType('xnat:projectData', null, 'Project').enabledByDefault(true)
    public static final DataType SUBJECT = new DataType('xnat:subjectData', null, 'Subject').enabledByDefault(true)
    // Session types
    public static final DataType MR_SESSION = makeSessionDataType('mr', true).associatedClass(MRSession)
    public static final DataType PET_SESSION = makeSessionDataType('pet', true).associatedClass(PETSession)
    public static final DataType CT_SESSION = makeSessionDataType('ct', true).associatedClass(CTSession)
    public static final DataType CR_SESSION = makeSessionDataType('cr', true)
    public static final DataType PET_MR_SESSION = new ImageSessionDataType('xnat:petmrSessionData', 'PETMR', 'PET MR Session', true).associatedClass(PETMRSession)
    public static final DataType DX3DCRANIOFRACTAL_SESSION = new ImageSessionDataType('xnat:dx3DCraniofacialSessionData', 'DX3D', 'DX3D Craniofractal Session')
    public static final DataType DX_SESSION = makeSessionDataType('dx')
    public static final DataType ECG_SESSION = makeSessionDataType('ecg')
    public static final DataType EEG_SESSION = makeSessionDataType('eeg')
    public static final DataType EPS_SESSION = makeSessionDataType('eps')
    public static final DataType ES_SESSION = makeSessionDataType('es')
    public static final DataType ESV_SESSION = makeSessionDataType('esv')
    public static final DataType GM_SESSION = makeSessionDataType('gm')
    public static final DataType GMV_SESSION = makeSessionDataType('gmv')
    public static final DataType HD_SESSION = makeSessionDataType('hd')
    public static final DataType IO_SESSION = makeSessionDataType('io')
    public static final DataType MEG_SESSION = makeSessionDataType('meg')
    public static final DataType MG_SESSION = makeSessionDataType('mg')
    public static final DataType NM_SESSION = makeSessionDataType('nm')
    public static final DataType OP_SESSION = makeSessionDataType('op')
    public static final DataType OPT_SESSION = makeSessionDataType('opt')
    public static final DataType OTHER_SESSION = new ImageSessionDataType('xnat:otherDicomSessionData', 'OT', 'Other Session')
    public static final DataType RF_SESSION = makeSessionDataType('rf')
    public static final DataType RG_SESSION = makeSessionDataType('rg')
    public static final DataType RT_SESSION = makeSessionDataType('rt')
    public static final DataType SM_SESSION = makeSessionDataType('sm')
    public static final DataType SR_SESSION = makeSessionDataType('sr')
    public static final DataType US_SESSION = makeSessionDataType('us')
    public static final DataType XA3D_SESSION = new ImageSessionDataType('xnat:xa3DSessionData', 'XA3D', 'XA3D Session')
    public static final DataType XA_SESSION = makeSessionDataType('xa')
    public static final DataType XC_SESSION = makeSessionDataType('xc')
    public static final DataType XCV_SESSION = makeSessionDataType('xcv')
    // Scan types
    public static final DataType MR_SCAN = makeScanDataType('mr').associatedScanClass(MRScan)
    public static final DataType PET_SCAN = makeScanDataType('pet').associatedScanClass(PETScan)
    public static final DataType CT_SCAN = makeScanDataType('ct').associatedScanClass(CTScan)
    public static final DataType CR_SCAN = makeScanDataType('cr')
    public static final DataType DX3DCRANIOFRACTAL_SCAN = new ImageScanDataType('xnat:dx3DCraniofacialScanData', null, 'DX3D Craniofacial Scan')
    public static final DataType DX_SCAN = makeScanDataType('dx')
    public static final DataType ECG_SCAN = makeScanDataType('ecg')
    public static final DataType EEG_SCAN = makeScanDataType('eeg')
    public static final DataType EPS_SCAN = makeScanDataType('eps')
    public static final DataType ES_SCAN = makeScanDataType('es')
    public static final DataType ESV_SCAN = makeScanDataType('esv')
    public static final DataType GM_SCAN = makeScanDataType('gm')
    public static final DataType GMV_SCAN = makeScanDataType('gmv')
    public static final DataType HD_SCAN = makeScanDataType('hd')
    public static final DataType IO_SCAN = makeScanDataType('io')
    public static final DataType MEG_SCAN = makeScanDataType('meg')
    public static final DataType MG_SCAN = makeScanDataType('mg')
    public static final DataType MRS_SCAN = makeScanDataType('mrs')
    public static final DataType NM_SCAN = makeScanDataType('nm')
    public static final DataType OP_SCAN = makeScanDataType('op')
    public static final DataType OPT_SCAN = makeScanDataType('opt')
    public static final DataType OTHER_SCAN = new ImageScanDataType('xnat:otherDicomScanData', null, 'Other Scan')
    public static final DataType RF_SCAN = makeScanDataType('rf')
    public static final DataType RG_SCAN = makeScanDataType('rg')
    public static final DataType RT_SCAN = new ImageScanDataType('xnat:rtImageScanData', null, 'RT Scan')
    public static final DataType SC_SCAN = makeScanDataType('sc')
    public static final DataType SEG_SCAN = makeScanDataType('seg')
    public static final DataType SM_SCAN = makeScanDataType('sm')
    public static final DataType SR_SCAN = makeScanDataType('sr')
    public static final DataType US_SCAN = makeScanDataType('us')
    public static final DataType VOICE_AUDIO_SCAN = new ImageScanDataType('xnat:voiceAudioScanData', null, 'Voice Audio Scan')
    public static final DataType XA3D_SCAN = new ImageScanDataType('xnat:xa3DScanData', null, 'XA3D Scan')
    public static final DataType XA_SCAN = makeScanDataType('xa')
    public static final DataType XC_SCAN = makeScanDataType('xc')
    public static final DataType XCV_SCAN = makeScanDataType('xcv')
    // Other concrete types
    public static final DataType SCID_RESEARCH = new DataType('xnat_a:scidResearchData', 'SCID', 'DSM-III-R Clincal Interview')
    public static final DataType PITTSBURGH_SIDE_EFFECTS = new DataType('xnat_a:sideEffectsPittsburghData', 'PSES', 'Pittsburgh Side Effects Scale Assessment')
    public static final DataType UPDRS = new DataType('xnat_a:updrs3Data', 'UPDRS', 'Unified Parkinson Disease Rating Scale Assessment')
    public static final DataType YBOCS = new DataType('xnat_a:ybocsData', 'YBOCS', 'Yale-Brown Obsessive-Compulsive Scale Assessment')
    public static final DataType YGTSS = new DataType('xnat_a:ygtssData', 'YGTSS', 'Yale Global Tic Severity Scale Assessment')
    public static final DataType MANUAL_QC = new DataType('xnat:qcManualAssessorData', 'MQC', 'Manual QC').associatedClass(ManualQC)
    public static final DataType QC = new DataType('xnat:qcAssessmentData', 'QC', 'Auto QC').associatedClass(org.nrg.xnat.pogo.experiments.assessors.QC)
    public static final DataType FREESURFER = new DataType('fs:fsData', null, 'Freesurfer')
    public static final DataType WORKFLOW = new DataType('wrk:workflowData', null, 'Workflow')

    String xsiType
    String code
    boolean enabledByDefault
    String singularName
    String pluralName
    @JsonIgnore DataType parent
    @JsonIgnore Class<? extends Experiment> associatedExperimentClass
    @JsonIgnore Class<? extends Scan> associatedScanClass

    DataType(String xsiType, String code, String singularName, String pluralName, boolean enabledByDefault = false) {
        this.xsiType = xsiType
        this.code = code
        this.singularName = singularName
        this.pluralName = pluralName
        this.enabledByDefault = enabledByDefault
        KNOWN_TYPES << this
    }

    DataType(String xsiType, String code, String singularName, boolean enabledByDefault = false) {
        this(xsiType, code, singularName, "${singularName}s", enabledByDefault)
    }

    DataType() {}

    static DataType makeSessionDataType(String sessionCode, boolean enabledByDefault = false) {
        new ImageSessionDataType(
                "xnat:${sessionCode.toLowerCase()}SessionData",
                sessionCode.toUpperCase(),
                "${sessionCode.toUpperCase()} Session",
                enabledByDefault
        )
    }

    static DataType makeScanDataType(String sessionCode) {
        new ImageScanDataType(
                "xnat:${sessionCode.toLowerCase()}ScanData",
                null,
                "${sessionCode.toUpperCase()} Scan"
        )
    }

    static DataType lookup(String xsiType) {
        KNOWN_TYPES.find {dataType ->
            dataType.xsiType == xsiType
        } ?: new DataType().xsiType(xsiType)
    }

    static DataType lookupDataTypeByAssociatedClass(Class aClass) {
        KNOWN_TYPES.find { it.associatedExperimentClass == aClass || it.associatedScanClass == aClass }
    }

    static List<DataType> lookupAllKnownScanTypes() {
        KNOWN_TYPES.findAll {
            it.parent == IMAGE_SCAN
        }
    }

    static List<DataType> lookupSessionTypesNotAddedByDefault() {
        KNOWN_TYPES.findAll {
            it.parent == IMAGE_SESSION && !it.enabledByDefault
        }
    }

    // Override default method to fallback to singular + "s"
    String getPluralName() {
        return pluralName ?: singularName + 's'
    }

    DataType xsiType(String xsiType) {
        setXsiType(xsiType)
        this
    }

    DataType code(String code) {
        setCode(code)
        this
    }

    DataType enabledByDefault(boolean enabledByDefault) {
        setEnabledByDefault(enabledByDefault)
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

    DataType parent(DataType parent) {
        setParent(parent)
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

}
