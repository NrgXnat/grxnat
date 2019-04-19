package org.nrg.xnat.pogo

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.extensions.reconstruction.ReconstructionExtension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.util.ListUtils
import org.nrg.xnat.util.RandomUtils

class Reconstruction extends Extensible<Reconstruction> {

    ImagingSession parentSession
    String label
    String type
    String baseScanType
    private final List<Resource> resources = []

    Reconstruction(ImagingSession session, String label) {
        setLabel(label)
        if (session != null) setParentSession(session)
    }

    Reconstruction(ImagingSession session) {
        this(session, RandomUtils.randomID())
    }

    Reconstruction() {
        this(null)
    }

    List<Resource> getResources() {
        resources
    }

    void setResources(List<Resource> resourceList) {
        ListUtils.copyInto(resourceList, resources)
    }

    Reconstruction parentSession(ImagingSession session) {
        setParentSession(session)
        this
    }

    void setParentSession(ImagingSession session) {
        parentSession = session
        session.addReconstruction(this)
    }

    Reconstruction type(String type) {
        setType(type)
        this
    }

    Reconstruction baseScanType(String baseType) {
        setBaseScanType(baseType)
        this
    }

    Reconstruction resources(List<Resource> resources) {
        setResources(resources)
        this
    }

    Reconstruction addResource(Resource resource) {
        if (!resources.contains(resource)) resources << resource
        this
    }

    ReconstructionExtension getExtension() {
        (ReconstructionExtension) super.extension
    }

    void setExtension(ReconstructionExtension extension) {
        super.setExtension(extension)
    }

    Reconstruction extension(ReconstructionExtension extension) {
        setExtension(extension)
        this
    }

    @Override
    String toString() {
        label
    }

}
