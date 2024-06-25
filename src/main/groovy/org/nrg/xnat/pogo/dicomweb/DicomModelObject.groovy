package org.nrg.xnat.pogo.dicomweb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.EqualsAndHashCode
import org.nrg.testing.DicomUtils

import static org.dcm4che3.data.Tag.*

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
class DicomModelObject {

    public static final List<Integer> REQUIRED_STUDY_ATTRIBUTES = [StudyDate, StudyTime, AccessionNumber, InstanceAvailability, ModalitiesInStudy, ReferringPhysicianName, RetrieveURL, PatientName, PatientID, PatientBirthDate, PatientSex, StudyInstanceUID, StudyID, NumberOfStudyRelatedSeries, NumberOfStudyRelatedInstances]
    public static final List<Integer> REQUIRED_SERIES_ATTRIBUTES = [Modality, RetrieveURL, SeriesInstanceUID, SeriesNumber, NumberOfSeriesRelatedInstances]
    List<DicomAttribute> attributes = []

    DicomAttribute findAttributeByTag(String tag) {
        attributes.find { it.tag == tag }
    }

    DicomAttribute findAttributeByHexInt(int hexInt) {
        attributes.find { it.tag == DicomUtils.intToSimpleHeaderString(hexInt) }
    }

    DicomAttribute findAttributeByKeyword(String keyword) {
        attributes.find { it.keyword == keyword }
    }

    String studyDate() {
        findAttributeByHexInt(StudyDate).getSingletonStringValue()
    }

    String studyTime() {
        findAttributeByHexInt(StudyTime).getSingletonStringValue()
    }

    String accessionNumber() {
        findAttributeByHexInt(AccessionNumber).getSingletonStringValue()
    }

    String instanceAvailability() {
        findAttributeByHexInt(InstanceAvailability).getSingletonStringValue()
    }

    List<String> modalitiesInStudy() {
        findAttributeByHexInt(ModalitiesInStudy).value
    }

    String retrieveUrl() {
        findAttributeByHexInt(RetrieveURL).getSingletonStringValue()
    }

    DicomPersonNames patientName() {
        findAttributeByHexInt(PatientName).getSingletonDicomPersonNames()
    }

    String patientID() {
        findAttributeByHexInt(PatientID).getSingletonStringValue()
    }

    String studyInstanceUid() {
        findAttributeByHexInt(StudyInstanceUID).getSingletonStringValue()
    }

    String studyID() {
        findAttributeByHexInt(StudyID).getSingletonStringValue()
    }

    String patientBirthDate() {
        findAttributeByHexInt(PatientBirthDate).getSingletonStringValue()
    }

    String patientSex() {
        findAttributeByHexInt(PatientSex).getSingletonStringValue()
    }

    long numStudyRelatedSeries() {
        findAttributeByHexInt(NumberOfStudyRelatedSeries).getSingletonLongValue()
    }

    long numStudyRelatedInstances() {
        findAttributeByHexInt(NumberOfStudyRelatedInstances).getSingletonLongValue()
    }

    String modality() {
        findAttributeByHexInt(Modality).getSingletonStringValue()
    }

    String seriesDescription() {
        findAttributeByHexInt(SeriesDescription).getSingletonStringValue()
    }

    String seriesInstanceUid() {
        findAttributeByHexInt(SeriesInstanceUID).getSingletonStringValue()
    }

    Long seriesNumber() {
        findAttributeByHexInt(SeriesNumber).getSingletonLongValue()
    }

    long numSeriesRelatedInstances() {
        findAttributeByHexInt(NumberOfSeriesRelatedInstances).getSingletonLongValue()
    }

    String performedProcedureStepStartDate() {
        findAttributeByHexInt(PerformedProcedureStepStartDate).getSingletonStringValue()
    }

    String performedProcedureStepStartTime() {
        findAttributeByHexInt(PerformedProcedureStepStartTime).getSingletonStringValue()
    }

    List<String> missingStudyAttributes() {
        missingAttributes(REQUIRED_STUDY_ATTRIBUTES)
    }

    List<String> missingSeriesAttributes() {
        missingAttributes(REQUIRED_SERIES_ATTRIBUTES)
    }

    private List<String> missingAttributes(List<Integer> expected) {
        expected.collect { DicomUtils.intToSimpleHeaderString(it) }.findAll { attribute ->
            !attributes.any { attribute == it.tag }
        }
    }

}
