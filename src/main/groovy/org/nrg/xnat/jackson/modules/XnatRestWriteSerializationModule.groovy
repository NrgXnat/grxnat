package org.nrg.xnat.jackson.modules

import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleModule
import org.nrg.xnat.jackson.XnatSerializationProvider
import org.nrg.xnat.jackson.XnatSerializationProviderManager
import org.nrg.xnat.jackson.serializers.*
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Reconstruction
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.versions.XnatVersion

class XnatRestWriteSerializationModule extends SimpleModule {

    @Deprecated
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

    XnatRestWriteSerializationModule(Class<? extends XnatVersion> versionClass) {
        super('XNAT_REST_Serializers')

        registerSerializer(Project, ProjectSerializer, versionClass)
        registerSerializer(Subject, SubjectSerializer, versionClass)
        registerSerializer(Experiment, ExperimentSerializer, versionClass)
        registerSerializer(Scan, ScanSerializer, versionClass)
        registerSerializer(Reconstruction, ReconstructionSerializer, versionClass)

        registerSerializer(Resource, ResourceSerializer, versionClass)
        registerSerializer(ResourceFile, ResourceFileSerializer, versionClass)

        registerSerializer(User, UserSerializer, versionClass)
        registerSerializer(Investigator, InvestigatorSerializer, versionClass)
    }

    protected void registerSerializer(Class<? extends Extensible> objectClass, Class<? extends XnatSerializationProvider> serializerClass, Class<? extends XnatVersion> xnatVersion) {
        addSerializer(objectClass, XnatSerializationProviderManager.getProviderFor(serializerClass, xnatVersion).newInstance()) // TODO: why aren't the generics specific enough?
    }

}
