package org.nrg.xnat.pogo.dicom

class DicomScpReceiver {

    String aeTitle
    int port
    boolean enabled = true
    String host
    String identifier = DicomObjectIdentifier.DEFAULT.getId()
    boolean customProcessing = false
    boolean directArchive = false

    DicomScpReceiver(String aeTitle, int port, boolean enabled, String host, String identifier) {
        this.aeTitle = aeTitle
        this.port = port
        this.enabled = enabled
        this.host = host
        this.identifier = identifier
    }

    DicomScpReceiver(String aeTitle, int port, boolean enabled, String host, String identifier, boolean customProcessing, boolean directArchive) {
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
        return DicomObjectIdentifier.DEFAULT.getId() == identifier
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
        setIdentifier(identifier.getId())
        return this
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
        return String.format("(aeTitle: %s, port: %d, enabled: %b, host: %s%s)", aeTitle, port, enabled, host, (!usesDefaultIdentifier()) ? ", identifier: " + identifier : "")
    }

}
