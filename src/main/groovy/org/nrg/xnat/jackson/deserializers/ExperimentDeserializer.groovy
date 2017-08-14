package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.joda.time.LocalDate
import org.nrg.xnat.pogo.experiments.Experiment

class ExperimentDeserializer<T extends Experiment> extends CustomDeserializer<Experiment> {

    ExperimentDeserializer(Class<? extends Experiment> ExperimentClass) {
        super(ExperimentClass)
    }

    @Override
    Experiment deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final Experiment experiment = handledType().newInstance() as Experiment

        setStringIfNonnull(node, 'label', experiment.&setLabel)

        if (fieldNonnull(node, 'date')) {
            experiment.setDate(new LocalDate(node.get('date').asText()))
        }
        
        setStringIfNonnull(node, 'ID', experiment.&setAccessionNumber)

        experiment
    }

}
