package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import org.nrg.xnat.enums.ShareMethod
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.sharing.BatchSharePayload
import org.nrg.xnat.pogo.sharing.ShareRequest

@RequirePlugin(PluginRegistry.BATCH_SHARE_ID)
class BatchShareSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/batch_share'
        ]
    }

    XnatInterface launchBatchShare(BatchSharePayload shareRequests) {
        queryBase().contentType(ContentType.JSON).body(shareRequests).post(formatXapiUrl('batch_share')).
            then().assertThat().statusCode(200)
        xnatInterface
    }

    String launchBatchShare(List<ShareRequest> shareRequests) {
        final String tracker = UUID.randomUUID().toString()
        launchBatchShare(new BatchSharePayload(
                trackingId: tracker,
                requests: shareRequests
        ))
        tracker
    }

    String launchBatchShare(List<Subject> subjects, Project destinationProject, ShareMethod shareMethod) {
        launchBatchShare(subjects.collect { subject ->
            new ShareRequest(
                    operation: shareMethod,
                    id: xnatInterface.getAccessionNumber(subject),
                    destinationProject: destinationProject.id
            )
        })
    }

}
