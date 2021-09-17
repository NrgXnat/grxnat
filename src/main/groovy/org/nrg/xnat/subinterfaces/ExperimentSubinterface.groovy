package org.nrg.xnat.subinterfaces

import io.restassured.response.Response
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.experiments.Experiment

class ExperimentSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/experiments/{EXPT_ID}'
        ]
    }

    def <T extends Experiment> T readExperiment(String accessionNumber, Class<T> experimentClass) {
        final Response response = jsonQuery().get(formatRestUrl('experiments', accessionNumber))
        final T experiment = response.jsonPath().getObject('items.get(0).data_fields', experimentClass)
        experiment.dataType(DataType.lookup(response.jsonPath().getString("items.get(0).meta.'xsi:type'")))
    }

}
