package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonProperty

class Workflow {

    @JsonProperty('launch_time') String launchTime
    String comments
    @JsonProperty('ExternalID') String externalId
    @JsonProperty('data_type') DataType dataType
    String justification
    @JsonProperty('ID') String id
    String category
    String type
    @JsonProperty('wrk_workflowData_id') int workflowDataId
    @JsonProperty('pipeline_name') String pipelineName
    String status

    void setDataType(String dataType) {
        setDataType(DataType.lookup(dataType))
    }

    void setDataType(DataType dataType) {
        this.dataType = dataType
    }

}
