package org.nrg.xnat.pogo

import org.dcm4che3.data.*

/**
 * DicomDataSet
 * Class to build a DICOM object programmatically. All objects are initialized with default values for some essential and some common
 * tags.
 *         Tag.StudyInstanceUID,  "1.2.3.4"
 *         Tag.SeriesInstanceUID, "1.2.3.4.5"
 *         Tag.SOPInstanceUID,    "1.2.3.4.5.6"
 *         Tag.SOPClassUID,       UID.MRImageStorage
 *         Tag.PatientName,       "PatientName"
 *         Tag.PatientID,         "PatientID"
 *         Tag.StudyID,           "34567"
 *         Tag.AccessionNumber,   "12345"
 *         Tag.StudyDate,         "20200519"
 *         Tag.Modality,          "MR"
 *         Tag.AcquisitionNumber, "101"
 *         Tag.InstanceNumber,    "1"
 */
class DicomDataSet {
    private Attributes attributes

    private static final String DEFAULT_STUDY_INSTANCE_UID = "1.2.3.4"
    private static final String DEFAULT_SERIES_INSTANCE_UID = "1.2.3.4.5"
    private static final String DEFAULT_SOP_INSTANCE_UID = "1.2.3.4.5.6"
    private static final String DEFAULT_SOP_CLASS_UID = UID.MRImageStorage
    private static final String DEFAULT_MODALITY = "MR"
    private static final String DEFAULT_PATIENT_NAME = "PatientName"
    private static final String DEFAULT_PATIENT_ID = "PatientID"
    private static final String DEFAULT_STUDY_ID = "34567"
    private static final String DEFAULT_ACCESSION_NUMBER = "12345"
    private static final String DEFAULT_STUDY_DATE = "20200519"
    private static final String DEFAULT_ACQUISITION_NUMBER = "101"
    private static final String DEFAULT_INSTANCE_NUMBER = "1"

    DicomDataSet() {
        this.attributes = new Attributes()
        setDefaults()
    }

    Attributes getDataset() {
        attributes.addAll(attributes.createFileMetaInformation(UID.ExplicitVRLittleEndian))
        attributes
    }

    DicomDataSet setTag(int tag, String value) {
        attributes.setValue(tag, ElementDictionary.vrOf(tag, null), value)
        return this
    }

    String getString(int tag) {
        return attributes.getString(tag)
    }

    DicomDataSet setTagArray(int[] tags, String value) {
        return setTagList(tags[0..-1], value)
    }

    DicomDataSet setTagList(List<Integer> tags, String value) {
        setString(tags, attributes, value)
        return this
    }

    String getString(int[] tags) {
        return getString(tags[0..-1], attributes)
    }

    String getString(List<Integer> tags) {
        return getString(tags, attributes)
    }

    protected String getString(List<Integer> tags, Attributes attributes) {
        if (isEven(tags.size())) {
            return null
        } else if (tags.size() == 1) {
            return attributes.getString(tags[0])
        } else {
            ItemPointer ip = new ItemPointer(tags[0], tags[1])
            return getString(tags[2..-1], attributes.getNestedDataset(ip))
        }
    }

    protected boolean isEven(int i) {
        return i % 2 == 0
    }

    /**
     * setString
     * Set the value of the specified tag in the dataset. Create sequence items as needed.
     *
     * @param tags array of integer tags specifying a tag in a sequence item.
     * @param attributes the parent dataset to contain the specified tag.
     * @param value the value-to-be-set as a String.
     * @return the parent dataset.
     */
    protected Attributes setString(List<Integer> tags, Attributes attributes, String value) {
        if (isEven(tags.size())) {
            throw new UnsupportedOperationException("Can not set value on a sequence tag.")
        }
        if (tags.size() == 1) {
            attributes.setValue(tags[0], ElementDictionary.vrOf(tags[0], null), value)
        } else {
            int tag = tags[0], itemIndex = tags[1]
            Object o = attributes.getValue(tag)
            Sequence seq
            if (o == null) {
                seq = attributes.newSequence(tag, 10)
            } else if (Sequence.class.isInstance(o)) {
                seq = Sequence.class.cast(o)
            } else {
                throw new IllegalArgumentException("tag " + Integer.toHexString(tag) + " is not a sequence tag.")
            }
            Attributes itemAttributes
            if (seq.size() > itemIndex) {
                itemAttributes = seq.get(itemIndex)
                if (itemAttributes == null) {
                    itemAttributes = new Attributes()
                }
            } else {
                itemAttributes = new Attributes()
            }
            seq.add(itemIndex, setString(tags[2..-1], itemAttributes, value))
        }
        attributes
    }

    /**
     * A minimum set of tags must be set for viable uploads to XNAT. Set with default values if not already set.
     */
    protected void setDefaults() {
        setDefault(Tag.SOPInstanceUID, DEFAULT_SOP_INSTANCE_UID)
        setDefault(Tag.SOPClassUID, DEFAULT_SOP_CLASS_UID)
        setDefault(Tag.PatientName, DEFAULT_PATIENT_NAME)
        setDefault(Tag.PatientID, DEFAULT_PATIENT_ID)
        setDefault(Tag.StudyInstanceUID, DEFAULT_STUDY_INSTANCE_UID)
        setDefault(Tag.SeriesInstanceUID, DEFAULT_SERIES_INSTANCE_UID)
        setDefault(Tag.StudyID, DEFAULT_STUDY_ID)
        setDefault(Tag.AccessionNumber, DEFAULT_ACCESSION_NUMBER)
        setDefault(Tag.StudyDate, DEFAULT_STUDY_DATE)
        setDefault(Tag.Modality, DEFAULT_MODALITY)
        setDefault(Tag.AcquisitionNumber, DEFAULT_ACQUISITION_NUMBER)
        setDefault(Tag.InstanceNumber, DEFAULT_INSTANCE_NUMBER)
    }

    protected void setDefault(int tag, String defaultValue) {
        String value = attributes.getString(tag)
        if (value == null) {
            attributes.setString(tag, ElementDictionary.vrOf(tag, null), defaultValue)
        }
    }
}
