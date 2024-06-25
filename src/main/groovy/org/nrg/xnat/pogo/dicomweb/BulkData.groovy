package org.nrg.xnat.pogo.dicomweb

class BulkData {

    String uuid
    String uri

    void validate() throws DicomValidationException {
        if (uri == null) {
            if (uuid == null) throw new DicomValidationException('Since uri for BulkData is not provided, uuid must be specified.')
        } else {
            if (uuid != null) throw new DicomValidationException('Since uri for BulkData has been specified, uuid must not be present.')
        }
    }

    BulkData uuid(String uuid) {
        setUuid(uuid)
        this
    }

    BulkData uri(String uri) {
        setUri(uri)
        this
    }

}