package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils

class Scan extends Extensible<Scan> {

    private ImagingSession session
    String id
    String xsiType
    String seriesDescription
    String type
    String note
    String quality = "usable"
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

    ImagingSession getSession() {
        return session
    }

    void setSession(ImagingSession session) {
        this.session = session
        if (session != null) session.addScan(this)
    }

    List<Resource> getScanResources() {
        return scanResources
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
        return this
    }

    Scan id(String id) {
        setId(id)
        return this
    }

    Scan xsiType(String xsiType) {
        setXsiType(xsiType)
        return this
    }

    Scan seriesDescription(String seriesDescription) {
        setSeriesDescription(seriesDescription)
        return this
    }

    Scan type(String type) {
        setType(type)
        return this
    }

    Scan note(String note) {
        setNote(note)
        return this
    }

    Scan quality(String quality) {
        setQuality(quality)
        return this
    }

    Scan scanResources(List<Resource> scanResources) {
        setScanResources(scanResources)
        return this
    }

    Scan addResource(Resource resource) {
        if (!scanResources.contains(resource)) scanResources.add(resource)
        return this
    }

    Scan extension(Extension<Scan> extension) {
        setExtension(extension)
        return this
    }

    @Override
    String toString() {
        return id
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Scan scan = (Scan) o

        if (id != scan.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

}
