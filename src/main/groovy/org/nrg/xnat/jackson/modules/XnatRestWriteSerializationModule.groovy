package org.nrg.xnat.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import org.nrg.xnat.jackson.serializers.*
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.resources.ResourceFile

class XnatRestWriteSerializationModule {

    static SimpleModule build() {
        final SimpleModule module = new SimpleModule("XNAT_REST_Serializers")

        module.addSerializer(Project, new ProjectSerializer())
        module.addSerializer(Subject, new SubjectSerializer())
        module.addSerializer(Experiment, new ExperimentSerializer())
        module.addSerializer(Scan, new ScanSerializer())
        module.addSerializer(Reconstruction, new ReconstructionSerializer())

        module.addSerializer(Resource, new ResourceSerializer())
        module.addSerializer(ResourceFile, new ResourceFileSerializer())

        module.addSerializer(User, new UserSerializer())
        module.addSerializer(Investigator, new InvestigatorSerializer())

        module
    }
}
