package org.nrg.xnat.pogo.dicom

class DicomScpReceiver {

    int port
    String aeTitle
    String host
    String identifier                 = DicomObjectIdentifier.DEFAULT.id
    boolean enabled                   = true
    boolean customProcessing          = false
    boolean directArchive             = false
    boolean anonymizationEnabled      = true
    boolean whitelistEnabled          = false
    List<String> whitelist            = []
    boolean routingExpressionsEnabled = false
    String projectRoutingExpression
    String subjectRoutingExpression
    String sessionRoutingExpression
    Integer id

    DicomScpReceiver(String aeTitle, int port, boolean enabled, String host, String identifier,
                     boolean customProcessing     = false,
                     boolean directArchive        = false,
                     boolean whitelistEnabled     = false,
                     boolean anonymizationEnabled = true,
                     List<String> whitelist       = new ArrayList<>(),
                     boolean routingExpressionsEnabled = false,
                     String projectRoutingExpression = "",
                     String subjectRoutingExpression = "",
                     String sessionRoutingExpression = "") {
        this.aeTitle              = aeTitle
        this.port                 = port
        this.enabled              = enabled
        this.host                 = host
        this.identifier           = identifier
        this.customProcessing     = customProcessing
        this.directArchive        = directArchive
        this.anonymizationEnabled = anonymizationEnabled
        this.whitelistEnabled   = whitelistEnabled
        this.whitelist.addAll(whitelist)
        this.routingExpressionsEnabled = routingExpressionsEnabled
        this.projectRoutingExpression = projectRoutingExpression
        this.subjectRoutingExpression = subjectRoutingExpression
        this.sessionRoutingExpression = sessionRoutingExpression
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

    DicomScpReceiver anonymizationEnabled(boolean anonymizationEnabled) {
        setAnonymizationEnabled(anonymizationEnabled)
        return this
    }

    DicomScpReceiver whitelistEnabled(boolean whitelistEnabled) {
        setWhitelistEnabled(whitelistEnabled)
        return this
    }

    DicomScpReceiver whitelist(List<String> whitelist) {
        setWhitelist(whitelist)
        return this
    }

    DicomScpReceiver routingExpressionsEnabled(boolean routingExpressionsEnabled) {
        setRoutingExpressionsEnabled(routingExpressionsEnabled)
        return this
    }

    DicomScpReceiver projectRoutingExpression(String projectRoutingExpression) {
        setProjectRoutingExpression(projectRoutingExpression)
        return this
    }

    DicomScpReceiver subjectRoutingExpression(String subjectRoutingExpression) {
        setSubjectRoutingExpression(subjectRoutingExpression)
        return this
    }

    DicomScpReceiver sessionRoutingExpression(String sessionRoutingExpression) {
        setSessionRoutingExpression(sessionRoutingExpression)
        return this
    }

    String toString() {
        "(aeTitle: ${aeTitle}, port: ${port}, enabled: ${enabled}, host: ${host}${usesDefaultIdentifier() ? '' : ', identifier: ' + identifier}, anonymizationEnabled: ${anonymizationEnabled}, whitelistEnabled: ${whitelistEnabled}, whitelist: ${whitelist.join(',')}, routingExpressionEnabled: ${routingExpressionsEnabled}, projectRoutingExpression: ${projectRoutingExpression}, subjectRoutingExpression: ${subjectRoutingExpression}, sessionRoutingExpression: ${sessionRoutingExpression})"
    }

}
