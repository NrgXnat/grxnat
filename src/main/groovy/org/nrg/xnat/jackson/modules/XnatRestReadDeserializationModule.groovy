package org.nrg.xnat.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import org.nrg.xnat.jackson.deserializers.*
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.Scan
import org.reflections.Reflections

class XnatRestReadDeserializationModule {

    static SimpleModule build() {
        final SimpleModule module = new SimpleModule("XNAT_REST_Deserializers")

        module.addDeserializer(Investigator, new InvestigatorDeserializer())
        module.addDeserializer(Project, new ProjectDeserializer())
        module.addDeserializer(Subject, new SubjectDeserializer())
        for (Class<? extends Experiment> experiment : new Reflections("org.nrg.xnat.pojo.experiments").getSubTypesOf(Experiment)) {
            module.addDeserializer(experiment, new ExperimentDeserializer(experiment))
        }
        module.addDeserializer(Scan, new ScanDeserializer())

        module
    }

}
