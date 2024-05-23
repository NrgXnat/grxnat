package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.HasUri
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils
import org.nrg.xnat.util.XnatUriUtils

import java.time.LocalTime

class Scan extends Extensible<Scan> implements HasUri {

    private ImagingSession session
    String id
    String xsiType
    String seriesDescription
    String type
    String note
    String quality = "usable"
    String uid
    LocalTime startTime
    Map<String, String> addParams = [:]
    private final List<Resource> scanResources = []

    Scan(ImagingSession session, String id) {
        this.session = session
        this.id = id
        if (session != null) {
            session.addScan(this)
        }
        final DataType dataType = DataType.lookupDataTypeByAssociatedClass(this.class)
        if (dataType != null) setXsiType(dataType.xsiType)
    }

    Scan() {
        this(null, null)
    }

    @Override
    String getUri() {
        XnatUriUtils.scanUri(
                (session.primaryProject ?: session.subject.primaryProject).id,
                session.subject.label,
                session.label,
                id,
                true
        )
    }

    ImagingSession getSession() {
        session
    }

    void setSession(ImagingSession session) {
        this.session = session
        if (session != null) session.addScan(this)
    }

    List<Resource> getScanResources() {
        scanResources
    }

    void setScanResources(List<Resource> scanResources) {
        ListUtils.copyInto(scanResources, this.scanResources)
    }

    Extension<Scan> getExtension() {
        return super.getExtension()
    }

    void setExtension(Extension<Scan> extension) {
        super.setExtension(extension)
    }

    Scan session(ImagingSession session) {
        setSession(session)
        this
    }

    Scan id(String id) {
        setId(id)
        this
    }

    Scan xsiType(String xsiType) {
        setXsiType(xsiType)
        this
    }

    Scan seriesDescription(String seriesDescription) {
        setSeriesDescription(seriesDescription)
        this
    }

    Scan type(String type) {
        setType(type)
        this
    }

    Scan note(String note) {
        setNote(note)
        this
    }

    Scan quality(String quality) {
        setQuality(quality)
        this
    }

    Scan uid(String uid) {
        setUid(uid)
        this
    }

    Scan startTime(LocalTime startTime) {
        setStartTime(startTime)
        this
    }

    Scan scanResources(List<Resource> scanResources) {
        setScanResources(scanResources)
        this
    }

    Scan addResource(Resource resource) {
        if (!scanResources.contains(resource)) scanResources.add(resource)
        this
    }

    Scan extension(Extension<Scan> extension) {
        setExtension(extension)
        this
    }

    Scan addParam(String fieldName, String fieldValue) {
        addParams.put(fieldName, fieldValue)
        this
    }

    @Override
    String toString() {
        id
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Scan scan = (Scan) o

        if (id != scan.id) return false
        if (session != scan.session) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

}
