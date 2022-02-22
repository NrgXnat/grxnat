package org.nrg.xnat.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import org.nrg.xnat.jackson.XnatSerializationProvider
import org.nrg.xnat.jackson.XnatSerializationProviderManager
import org.nrg.xnat.jackson.deserializers.*
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.versions.XnatVersion
import org.reflections.Reflections

class XnatRestReadDeserializationModule extends SimpleModule {

    @Deprecated
    XnatRestReadDeserializationModule() {
        super('XNAT_REST_Deserializers')

        addDeserializer(Investigator, new InvestigatorDeserializer())
        addDeserializer(Project, new ProjectDeserializer())
        addDeserializer(Subject, new SubjectDeserializer())
        new Reflections('org.nrg.xnat.pogo.experiments').getSubTypesOf(Experiment).each { experiment ->
            addDeserializer(experiment, new ExperimentDeserializer(experiment))
        }
        addDeserializer(Scan, new ScanDeserializer())
        addDeserializer(User, new UserDeserializer())
        new Reflections('org.nrg.xnat.pogo.resources').getSubTypesOf(Resource).each { resourceClass ->
            addDeserializer(resourceClass, new ResourceDeserializer(resourceClass))
        }
    }

    XnatRestReadDeserializationModule(Class<? extends XnatVersion> versionClass) {
        super('XNAT_REST_Deserializers')

        registerDeserializer(Investigator, InvestigatorDeserializer, versionClass)
        registerDeserializer(Project, ProjectDeserializer, versionClass)
        registerDeserializer(Subject, SubjectDeserializer, versionClass)
        new Reflections('org.nrg.xnat.pogo.experiments').getSubTypesOf(Experiment).each { experiment ->
            registerDeserialzerAltConstructor(experiment, ExperimentDeserializer, versionClass)
        }
        registerDeserializer(Scan, ScanDeserializer, versionClass)
        registerDeserializer(User, UserDeserializer, versionClass)
        new Reflections('org.nrg.xnat.pogo.resources').getSubTypesOf(Resource).each { resourceClass ->
            registerDeserialzerAltConstructor(resourceClass, ResourceDeserializer, versionClass)
        }
    }

    protected void registerDeserializer(Class<? extends Extensible> objectClass, Class<? extends XnatSerializationProvider> deserializerClass, Class<? extends XnatVersion> xnatVersion) {
        addDeserializer(objectClass, XnatSerializationProviderManager.getProviderFor(deserializerClass, xnatVersion).newInstance()) // TODO: why aren't the generics specific enough?
    }

    protected void registerDeserialzerAltConstructor(Class<? extends Extensible> objectClass, Class<? extends XnatSerializationProvider> deserializerClass, Class<? extends XnatVersion> xnatVersion) {
        addDeserializer(objectClass, XnatSerializationProviderManager.getProviderFor(deserializerClass, xnatVersion).newInstance([objectClass]))
    }

}
