package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.experiments.Experiment

import java.time.LocalDate

class ExperimentDeserializer<T extends Experiment> extends CustomDeserializer<T> {

    ExperimentDeserializer(Class<T> experimentClass) {
        super(experimentClass)
    }

    @Override
    Experiment deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final T experiment

        if (fieldNonnull(node, 'xsiType')) {
            final DataType dataType = DataType.lookup(node.get('xsiType').asText())
            final Class<? extends Experiment> associated = dataType.associatedExperimentClass
            if (associated != null && T.class.isAssignableFrom(associated)) {
                experiment = associated.newInstance() as T
            } else {
                experiment = handledType().newInstance() as T
                experiment.setDataType(dataType)
            }
        } else {
            experiment = handledType().newInstance() as T
        }

        setStringIfNonnull(node, 'label', experiment.&setLabel)

        if (fieldNonempty(node, 'date')) {
            experiment.setDate(new LocalDate(node.get('date').asText()))
        }

        if (fieldNonnull(node, 'xsiType')) {
            final DataType dataType = DataType.lookup(node.get('xsiType').asText())
            experiment.setDataType(dataType)
        }
        
        setStringIfNonnull(node, 'ID', experiment.&setAccessionNumber)
        setStringIfNonempty(node, 'note', experiment.&setNotes)

        experiment
    }

}
