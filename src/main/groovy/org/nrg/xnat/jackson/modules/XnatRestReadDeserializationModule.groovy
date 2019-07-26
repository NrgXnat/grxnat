package org.nrg.xnat.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import org.nrg.xnat.jackson.deserializers.*
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.users.User
import org.reflections.Reflections

class XnatRestReadDeserializationModule extends SimpleModule {

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

}
