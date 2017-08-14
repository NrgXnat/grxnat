package org.nrg.xnat.pogo.dicom

class DicomScpReceiver {

    String aeTitle
    int port
    boolean enabled = true
    String host
    DicomObjectIdentifier identifier = DicomObjectIdentifier.DEFAULT

    DicomScpReceiver(String aeTitle, int port, boolean enabled, String host, DicomObjectIdentifier identifier) {
        this.aeTitle = aeTitle
        this.port = port
        this.enabled = enabled
        this.host = host
        this.identifier = identifier
    }

    DicomScpReceiver() {}

    boolean usesDefaultIdentifier() {
        return DicomObjectIdentifier.DEFAULT == identifier
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

    DicomScpReceiver identifier(DicomObjectIdentifier identifier) {
        setIdentifier(identifier)
        return this
    }

    String toString() {
        return String.format("(aeTitle: %s, port: %d, enabled: %b, host: %s%s)", aeTitle, port, enabled, host, (!usesDefaultIdentifier()) ? ", identifier: " + identifier.getId() : "")
    }

}
