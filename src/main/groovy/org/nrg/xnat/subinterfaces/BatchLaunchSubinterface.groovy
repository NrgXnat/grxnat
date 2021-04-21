package org.nrg.xnat.subinterfaces

import com.jayway.restassured.http.ContentType
import org.nrg.testing.TimeUtils
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.containers.CommandSummaryForContext

import java.util.concurrent.TimeUnit

import static org.testng.AssertJUnit.assertTrue

@RequirePlugin("batchLaunchPlugin")
class BatchLaunchSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/workflows'
        ]
    }

    int determineWorkflowId(DataType dataType, String id, CommandSummaryForContext wrapper) {
        final Map<String, Object> params = new HashMap<>()
        params.put("data_type", dataType.getXsiType())
        params.put("id", id)
        params.put("sort_col", "launchTime")
        params.put("sort_dir", "desc")
        params.put("page", "1")
        params.put("filters", makeFilterMap(wrapper.getWrapperName()))

        final long start = System.currentTimeMillis()
        int workflowId = 0
        while (workflowId == 0 && System.currentTimeMillis() - start < TimeUnit.MINUTES.toMillis(5)) {
            TimeUtils.sleep(1000) // give the thread time to submit these
            workflowId = queryBase().body(params)
                    .contentType(ContentType.JSON)
                    .post(formatXapiUrl("workflows"))
                    .then().assertThat().statusCode(200).and().extract().jsonPath().getInt("wfid.get(0)")
        }

        assertTrue(workflowId > 0)
        workflowId
    }

    Map<String, Object> makeFilterMap(String wrapperName) {
        final Map<String, String> filterVals = new HashMap<>()
        filterVals.put("like", wrapperName)
        filterVals.put("backend", "sql_string")
        return Collections.singletonMap("pipelineName", filterVals)
    }
}
