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

class XnatRestWriteSerializationModule extends SimpleModule {

    XnatRestWriteSerializationModule() {
        super('XNAT_REST_Serializers')

        addSerializer(Project, new ProjectSerializer())
        addSerializer(Subject, new SubjectSerializer())
        addSerializer(Experiment, new ExperimentSerializer())
        addSerializer(Scan, new ScanSerializer())
        addSerializer(Reconstruction, new ReconstructionSerializer())

        addSerializer(Resource, new ResourceSerializer())
        addSerializer(ResourceFile, new ResourceFileSerializer())

        addSerializer(User, new UserSerializer())
        addSerializer(Investigator, new InvestigatorSerializer())
    }

}
