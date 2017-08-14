package org.nrg.xnat.pogo.dicom

class DicomObjectIdentifier {

    public static final DicomObjectIdentifier DEFAULT = new DicomObjectIdentifier("dicomObjectIdentifier", "Default DICOM object identifier (ClassicDicomObjectIdentifier)")

    String id
    String displayName

    DicomObjectIdentifier(String id, String displayName) {
        setId(id)
        setDisplayName(displayName)
    }

    DicomObjectIdentifier() {}

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DicomObjectIdentifier that = (DicomObjectIdentifier) o

        if (displayName != that.displayName) return false
        if (id != that.id) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0)
        return result
    }

}