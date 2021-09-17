package org.nrg.xnat.pogo.dicom

class DicomScpReceiver {

    String aeTitle
    int port
    boolean enabled = true
    String host
    String identifier = DicomObjectIdentifier.DEFAULT.id
    boolean customProcessing = false
    boolean directArchive = false
    Integer id

    DicomScpReceiver(String aeTitle, int port, boolean enabled, String host, String identifier, boolean customProcessing = false, boolean directArchive = false) {
        this.aeTitle = aeTitle
        this.port = port
        this.enabled = enabled
        this.host = host
        this.identifier = identifier
        this.customProcessing = customProcessing
        this.directArchive = directArchive
    }

    DicomScpReceiver() {}

    boolean usesDefaultIdentifier() {
        DicomObjectIdentifier.DEFAULT.id == identifier
    }

    DicomScpReceiver aeTitle(String aeTitle) {
        setAeTitle(aeTitle)
        return this
    }

    DicomScpReceiver port(int port) {
        setPort(port)
        return this
    }

    DicomScpReceiver enabled(boolean enabled) {
        setEnabled(enabled)
        return this
    }

    DicomScpReceiver host(String host) {
        setHost(host)
        return this
    }

    DicomScpReceiver identifier(String identifier) {
        setIdentifier(identifier)
        return this
    }

    DicomScpReceiver identifier(DicomObjectIdentifier identifier) {
        identifier(identifier.id)
    }

    DicomScpReceiver customProcessing(boolean customProcessing) {
        setCustomProcessing(customProcessing)
        return this
    }

    DicomScpReceiver directArchive(boolean directArchive) {
        setDirectArchive(directArchive)
        return this
    }

    String toString() {
        "(aeTitle: ${aeTitle}, port: ${port}, enabled: ${enabled}, host: ${host}${usesDefaultIdentifier() ? '' : ', identifier: ' + identifier})"
    }

}
