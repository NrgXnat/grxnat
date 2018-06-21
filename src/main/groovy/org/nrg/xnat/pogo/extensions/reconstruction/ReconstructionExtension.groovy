package org.nrg.xnat.pogo.extensions.reconstruction

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession

abstract class ReconstructionExtension extends Extension<Reconstruction> {

    ReconstructionExtension(Reconstruction reconstruction) {
        super(reconstruction)
    }

    abstract void create(Project project, Subject subject, ImagingSession session)

}