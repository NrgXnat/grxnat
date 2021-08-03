package org.nrg.xnat.importer.params

trait TriggerPipelinesRequest<X extends TriggerPipelinesRequest<X>> {

    Boolean triggerPipelines

    X triggerPipelines(Boolean setting = true) {
        setTriggerPipelines(setting)
        this as X
    }

}